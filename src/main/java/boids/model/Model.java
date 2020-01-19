package boids.model;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import static akka.actor.Nobody.tell;
import static akka.dispatch.Futures.sequence;
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import akka.dispatch.Mapper;
import akka.dispatch.OnComplete;
import akka.japi.pf.ReceiveBuilder;
import akka.pattern.Patterns;
import akka.util.Timeout;
import boids.model.enums.BordersAvoidanceFunction;
import boids.model.messages.MessageApplyAllRules;
import boids.model.messages.MessageAskForBoidData;
import boids.model.messages.ReplyAskForBoidData;
import boids.view.View;
import javafx.util.Pair;
import scala.concurrent.Future;

import javax.vecmath.Vector2d;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
//    private double neighbourhoodRadius;

    //    private ArrayList<Boid> boids;
    private ArrayList<Obstacle> obstacles;
    private HashMap<Pair<Integer, Integer>, LinkedList<ActorRef>> voxels;
    private HashMap<ActorRef, BoidInfo> boidInfos;
    private boolean isTurningBackOnBordersEnabled;
    private BordersAvoidanceFunction bordersAvoidanceFunction;
    private int boidsCount;
    private ArrayList<ActorRef> boidActorRefs;
    private Map<ActorRef, ActorRef> boidListenerRefs;
    private ActorSystem boidsActorSystem;
    private ActorRef modelActorRef;
    private Timeout timeout;


    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .matchAny(o -> {
                    System.out.println(o.toString());
                })
                .build();
    }

    public Model() {
        boidsActorSystem = ActorSystem.create("boids-simulation");
        modelActorRef = boidsActorSystem.actorOf(Props.create(Model.class), "modelActor");
        timeout = Timeout.create(Duration.ofMillis(50));
//        boids = new ArrayList<>();
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

    public void askBoidsForPosition()
    {

        Iterable<Future<Object>> futureArray = new ArrayList<>();
        for (ActorRef boidRef : boidActorRefs)
        {
//            ask(boidRef, new MessageAskForBoidData(boidRef), 100);
//            ask(boidRef, "ads", 100);
            Future<Object> future = ask(boidRef, new MessageAskForBoidData(boidRef), timeout);
            ((ArrayList<Future<Object>>) futureArray).add(future);
            pipe(future, boidsActorSystem.getDispatcher());
        }

        Future<Iterable<Object>> futureListOfObjects = sequence(futureArray, boidsActorSystem.dispatcher());
        Future<HashMap<ActorRef, BoidInfo>> futureBoidsInfos =
                futureListOfObjects.map(
                        new Mapper<Iterable<Object>, HashMap<ActorRef, BoidInfo>>() {
                            public HashMap<ActorRef, BoidInfo> apply(Iterable<Object> objects) {
//                                pipe(future, actorSystem.getDispatcher());
                                for (Object o : objects)
                                {
                                    boidInfos.put(((ReplyAskForBoidData) o).getActorRef(), ((ReplyAskForBoidData) o).getBoidInfo());
                                }
                                return boidInfos;
                            }
                        },
                        boidsActorSystem.getDispatcher());

//        for (Boid boidRef : boids)
//        {
//            Future<Object> boidsInfoFuture = ask(boidListenerRefs.get(boidRef), MessageAskForBoidData.class, 100);
//            pipe(boidsInfoFuture, boidsActorSystem.getDispatcher());
//            boidsInfoFuture.onComplete(new OnComplete<Object>() {
//                @Override
//                public void onComplete(Throwable failure, Object success) throws Throwable {
//                    ReplyAskForBoidData replyAskForBoidData = (ReplyAskForBoidData) success;
//                    boidInfos.put(boidRef, replyAskForBoidData.getBoidInfo());
////                    System.out.println("printing: " + mess.toString());
//                }
//            }, boidsActorSystem.getDispatcher());
//
//
//
////            CompletableFuture<Object> fut =
////                    ask(boidListenerRefs.get(boidRef), "some message", 50).toCompletableFuture();
//            Patterns.pipe(boidsInfoFuture, boidsActorSystem.getDispatcher());
//            boidsInfoFuture.value();
//        }
    }
    //function where all rules are added to a boid
    public void findNewBoidsPositions() {
        askBoidsForPosition();
        for (ActorRef boidRef : boidActorRefs) {
            LinkedList<BoidInfo> neighbours = getBoidNeighbours(boidRef);
            ActorRef actorRef;
            modelActorRef.tell(new MessageApplyAllRules(neighbours, obstacles, separationWeight, cohesionWeight, alignmentWeight, opponentWeight,  obstacleRadius, obstacleWeight, bordersAvoidanceFunction), modelActorRef);

//            boid.applyAllRules(neighbours, obstacles, separationWeight, cohesionWeight, alignmentWeight, opponentWeight,  obstacleRadius, obstacleWeight, bordersAvoidanceFunction);
        }

        resetVoxels();
    }

    public BoidInfo getBoidInfo(ActorRef actorRef){
        return boidInfos.get(actorRef);
    }

    public HashMap<ActorRef, BoidInfo> getBoidInfos() {
        return boidInfos;
    }

    //    public ArrayList<Boid> getBoids() {
//        return boids;
//    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void generateBoids(int count) {
        for (int i = 0; i < count; i++) {
            addBoid(null, false);
        }
    }

    public void addBoid(Vector2d pos, boolean isOpponent) {
        ActorRef boidActorRef; // = boidsActorSystem.actorOf(Props.create(Boid.class), "boidActor");;
        boidsCount++;
//        Boid boid;
        if (pos == null) {
            boidActorRef = boidsActorSystem.actorOf(Props.create(Boid.class), "Boid-" + boidsCount);
//            boid = new Boid();
        } else {
            boidActorRef = boidsActorSystem.actorOf(Props.create(Boid.class, pos, isOpponent), "Boid-" + boidsCount);
//            boid = new Boid(pos, isOpponent);
        }

        boidActorRefs.add(boidActorRef);
//        boids.add(boid);
        addToVoxel(boidActorRef);
    }

//    private synchronized void addToVoxel(Boid boid) {
    private synchronized void addToVoxel(ActorRef boidRef) {
        Pair key = getVoxelKey(boidRef);
        LinkedList<ActorRef> list = voxels.get(key);
        if (list != null) {
            list.add(boidRef);
            return;
        }

        list = new LinkedList<>();
        list.add(boidRef);
        voxels.put(key, list);
    }

//    private Pair<Integer, Integer> getVoxelKey(Boid boid) {
    private Pair<Integer, Integer> getVoxelKey(ActorRef actorRef) {
//        Future<Object> future = ask(boidRef,
//                MessageAskForBoidData.class, 100);
//        modelActorRef.a
        Vector2d pos = boidInfos.get(actorRef).getPosition();
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
            addToVoxel(b);
        }
    }

    private void clearVoxels() {
        voxels = new HashMap<>();
    }

    public void addObstacle(Vector2d pos, double radius) {
        obstacles.add(new Obstacle(pos, radius));
    }

    public void removeBoids() {
        boidsCount = 0;
        boidInfos = new HashMap<>();
        clearVoxels();
    }

    public void removeObstacles() {
        obstacles = new ArrayList<>();
    }

    public int getBoidsCount() {
        return boidsCount;
    }

    private LinkedList<BoidInfo> getBoidNeighbours(ActorRef actorRef) {
        LinkedList<BoidInfo> neighbours = new LinkedList<>();
        Pair<Integer, Integer> key = getVoxelKey(actorRef);
        int x = key.getKey();
        int y = key.getValue();
        int[] coordsX = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] coordsY = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        for (int i = 0; i < 9; i++) {
            LinkedList<ActorRef> voxelContent = voxels.get(new Pair<>(x + coordsX[i], y + coordsY[i]));
            if (voxelContent == null) continue;
            for (ActorRef otherRef : voxelContent) {
                BoidInfo checkedBotInfo = boidInfos.get(key);
                BoidInfo otherBoidInfo = boidInfos.get(otherRef);
                double dist = checkedBotInfo.getDistance(otherBoidInfo);

                if (dist > 0 && dist < neighbourhoodRadius) {
                    neighbours.add(otherBoidInfo);
                }
            }
        }

        return neighbours;
    }

    public void setAvoidBordersFunctionToFolding() {
        isTurningBackOnBordersEnabled = false;
        bordersAvoidanceFunction = BordersAvoidanceFunction.FOLD_ON_BORDERS;
    }

    public void setAvoidBordersFunctionToTurningBack() {
        isTurningBackOnBordersEnabled = true;
        bordersAvoidanceFunction = BordersAvoidanceFunction.TURN_BACK_ON_BORDERS;
    }

    public static double getNeighbourhoodRadius() {
        return neighbourhoodRadius;
    }

    public ArrayList<ActorRef> getBoidActorRefs() {
        return boidActorRefs;
    }
}


