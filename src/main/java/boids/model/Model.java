package boids.model;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Mapper;
import akka.japi.pf.ReceiveBuilder;
import akka.util.Timeout;
import boids.model.enums.BordersAvoidanceFunction;
import boids.model.messages.*;
import boids.view.View;
import javafx.util.Pair;
import scala.concurrent.Future;

import javax.vecmath.Vector2d;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static akka.dispatch.Futures.sequence;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class Model extends AbstractActor {
    public static double obstacleRadius = 15.0;
    public static double separationWeight = 2.0;
    public static double cohesionWeight = 1.0;
    public static double alignmentWeight = 1.0;
    public static double opponentWeight = 1.5;
    public static double bordersWeight = 2.5;
    public static double obstacleWeight = 2.5;
    private static double voxelSize = setVoxelSize();
    public static double neighbourhoodRadius = 30.0;
    public static double separationRadius;
    public static double maxSpeed;
    public static double maxForce;

    private ArrayList<Obstacle> obstacles;
    private HashMap<Pair<Integer, Integer>, LinkedList<ActorRef>> voxels;
    private HashMap<ActorRef, BoidInfo> boidInfos = new HashMap<>();
    private boolean isTurningBackOnBordersEnabled;
    private BordersAvoidanceFunction bordersAvoidanceFunction;
    private int boidsCount;
    private ArrayList<ActorRef> boidActorRefs = new ArrayList<>();
    private Map<ActorRef, ActorRef> boidListenerRefs = new HashMap<>();
    private ActorSystem boidsActorSystem;
    private Timeout timeout;


    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(MessageReplyBoidInfo.class, o -> {
                    boidInfos.put(sender(), o.getBoidInfo());
                })
                .match(MessageGetDrawInfo.class, o -> {
                    sender().tell(new MessageReceiveDrawInfo(getBoidInfos(), getObstacles(), boidsCount), self());
                    askBoidsForPosition();
                })
                .match(MessageGenerateBoids.class, o -> {
                    generateBoids(o.getAmountToGenerate());
                })
                .match(MessageAddBoid.class, o -> {
                    System.out.println("---------------");
                    addBoid(o.getPosition(), o.getOpponent());

                })
                .match(MessageRemoveBoidsAndObstacles.class, o -> {
                    removeBoids();
                    removeObstacles();
                })
                .match(MessageSetAvoidance.class, o -> {
                    if (o.isAvoidanceFolding())
                        setAvoidBordersFunctionToFolding();
                    else
                        setAvoidBordersFunctionToTurningBack();
                })
                .match(MessageAddObstacle.class, o -> {
                    addObstacle(o.getPosition(), o.getRadius());
                })
                .match(MessageGetBoidsCount.class, o -> {
                    //TODO
                })
                .build();
    }

    public Model(ActorSystem actorSystem) {
        boidsActorSystem = actorSystem;
        timeout = Timeout.create(Duration.ofMillis(50));
        obstacles = new ArrayList<>();
        voxels = new HashMap<>();
        setAvoidBordersFunctionToTurningBack();
        boidsCount = 0;
    }

    static double setVoxelSize() {
        double radius = neighbourhoodRadius;
        if (radius == 0) {
            voxelSize = View.CANVAS_WIDTH;
        } else {
            voxelSize = neighbourhoodRadius;
        }
        return voxelSize;
    }

    private void askBoidsForPosition() {
        Iterable<Future<Object>> futureArray = new ArrayList<>();
        for (ActorRef boidRef : boidActorRefs) {
            ArrayList<ActorRef> neighbours = getBoidNeighbours(boidRef);
            MessageModelAskBoid message = new MessageModelAskBoid(
                    boidRef, neighbours, obstacles, separationWeight, cohesionWeight, alignmentWeight, opponentWeight, obstacleRadius,
                    obstacleWeight, separationRadius, bordersWeight, maxSpeed, maxForce, bordersAvoidanceFunction
            );

            Future<Object> future = ask(boidRef, message, timeout);
            ((ArrayList<Future<Object>>) futureArray).add(future);
            pipe(future, boidsActorSystem.getDispatcher());
        }

        Future<Iterable<Object>> futureListOfObjects = sequence(futureArray, boidsActorSystem.dispatcher());
        Future<HashMap<ActorRef, BoidInfo>> futureBoidsInfos =
                futureListOfObjects.map(
                        new Mapper<Iterable<Object>, HashMap<ActorRef, BoidInfo>>() {
                            public HashMap<ActorRef, BoidInfo> apply(Iterable<Object> objects) {
//                                pipe(future, actorSystem.getDispatcher());
                                for (Object o : objects) {
                                    boidInfos.put(((MessageBoidReplyModel) o).getSenderActorRef(), ((MessageBoidReplyModel) o).getBoidInfo());
                                }
                                return boidInfos;
                            }
                        },
                        boidsActorSystem.getDispatcher());

        resetVoxels();
    }

    private BoidInfo getBoidInfo(ActorRef actorRef) {
        return boidInfos.get(actorRef);
    }

    private ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    private void generateBoids(int count) {
        for (int i = 0; i < count; i++) {
            addBoid(null, false);
        }
    }

    private void addBoid(Vector2d pos, boolean isOpponent) {
        ActorRef boidActorRef;
        boidsCount++;
        if (pos == null) {
            boidActorRef = boidsActorSystem.actorOf(Props.create(Boid.class));
        } else {
            boidActorRef = boidsActorSystem.actorOf(Props.create(Boid.class, pos, isOpponent));
        }

        boidActorRef.tell(new MessageGetBoidInfo(), self());
        boidActorRefs.add(boidActorRef);
        //TODO nie mamy info o boidActor ref gdy jest losowo dodawany i potrzebujemy jego pozycji
        addToVoxel(boidActorRef, pos);
    }

    //    private synchronized void addToVoxel(Boid boid) {
    private synchronized void addToVoxel(ActorRef boidRef, Vector2d position) {
        Pair key = getVoxelKey(boidRef, position);
        LinkedList<ActorRef> list = voxels.get(key);
        if (list != null) {
            list.add(boidRef);
            return;
        }

        list = new LinkedList<>();
        list.add(boidRef);
        voxels.put(key, list);
    }

    private Pair<Integer, Integer> getVoxelKey(ActorRef actorRef, Vector2d position) {
        Vector2d pos;
        if (position != null) {
            pos = position;
        } else {
            System.out.println(boidInfos.toString());
            pos = boidInfos.get(actorRef).getPosition();
        }
        int x = (int) (pos.getX() / voxelSize);
        int y = (int) (pos.getY() / voxelSize);

        if (isTurningBackOnBordersEnabled) {
            if (pos.getX() < 0) {
                x--;
            }
            if (pos.getY() < 0) {
                y--;
            }
        }

        return new Pair<>(x, y);
    }

    private void resetVoxels() {
        clearVoxels();
        for (ActorRef b : boidActorRefs) {
            addToVoxel(b, null);
        }
    }

    private void clearVoxels() {
        voxels = new HashMap<>();
    }

    private void addObstacle(Vector2d pos, double radius) {
        obstacles.add(new Obstacle(pos, radius));
    }

    private void removeBoids() {
        boidsCount = 0;
        boidInfos = new HashMap<>();
        boidActorRefs = new ArrayList<>();
        boidListenerRefs = new HashMap<>();
        //TODO usun wszystkich agentow
        clearVoxels();
    }

    private void removeObstacles() {
        obstacles = new ArrayList<>();
    }

    private int getBoidsCount() {
        return boidsCount;
    }

    private ArrayList<ActorRef> getBoidNeighbours(ActorRef actorRef) {
        ArrayList<ActorRef> neighbours = new ArrayList<>();
        Pair<Integer, Integer> key = getVoxelKey(actorRef, null);
        int x = key.getKey();
        int y = key.getValue();
        int[] coordsX = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] coordsY = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        for (int i = 0; i < 9; i++) {
            LinkedList<ActorRef> voxelContent = voxels.get(new Pair<>(x + coordsX[i], y + coordsY[i]));
            if (voxelContent == null) continue;
            for (ActorRef otherRef : voxelContent) {
                BoidInfo checkedBoidInfo = boidInfos.get(actorRef);
                BoidInfo otherBoidInfo = boidInfos.get(otherRef);
                double dist = checkedBoidInfo.getDistance(otherBoidInfo);

                if (dist > 0 && dist < neighbourhoodRadius) {
                    neighbours.add(otherRef);
                }
            }
        }

        return neighbours;
    }

    private void setAvoidBordersFunctionToFolding() {
        isTurningBackOnBordersEnabled = false;
        bordersAvoidanceFunction = BordersAvoidanceFunction.FOLD_ON_BORDERS;
    }

    private void setAvoidBordersFunctionToTurningBack() {
        isTurningBackOnBordersEnabled = true;
        bordersAvoidanceFunction = BordersAvoidanceFunction.TURN_BACK_ON_BORDERS;
    }

    //TODO public?
    public static double getNeighbourhoodRadius() {
        return neighbourhoodRadius;
    }

    private ArrayList<BoidInfo> getBoidInfos() {
        //Niepotrzebna funkcja wystarczy zastąpić hashmap.values()
        ArrayList<BoidInfo> infos = new ArrayList<>();
        for (Map.Entry<ActorRef, BoidInfo> entry : boidInfos.entrySet()) {
            infos.add(entry.getValue());
        }
        return infos;
    }
}


