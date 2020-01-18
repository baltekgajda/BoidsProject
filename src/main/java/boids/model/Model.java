package boids.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import static akka.pattern.Patterns.ask;
import boids.model.enums.BordersAvoidanceFunction;
import boids.model.messages.MessageApplyAllRules;
import boids.model.messages.MessageAskForBoidData;
import boids.model.messages.ReplyAskForBoidData;
import boids.view.View;
import javafx.util.Pair;
import scala.concurrent.Future;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Model {

    class BoidInfo{
        Vector2d position;
        public Vector2d getPosition(){
            return position;
        }
    }
    public static double obstacleRadius = 15.0;
    public static double separationWeight = 2.0;
    public static double cohesionWeight = 1.0;
    public static double alignmentWeight = 1.0;
    public static double opponentWeight = 1.5;
    public static double bordersWeight = 2.5;
    public static double obstacleWeight = 2.5;
    private static double voxelSize = setVoxelSize();
    private static double neighbourhoodRadius = 30.0;
    private ArrayList<Boid> boids;
    private ArrayList<Obstacle> obstacles;
    private HashMap<Pair<Integer, Integer>, LinkedList<Boid>> voxels;
    private Map<ActorRef, BoidInfo> boidInfos;
    private boolean isTurningBackOnBordersEnabled;
    private BordersAvoidanceFunction bordersAvoidanceFunction;
    private int boidsCount;
    private ArrayList<ActorRef> boidActorRefs;
    private Map<ActorRef, ActorRef> boidListenerRefs;
    private ActorSystem boidsActorSystem;
    private ActorRef modelActorRef;


    public Model() {
        boidsActorSystem = ActorSystem.create("boids-simulation");
        modelActorRef = boidsActorSystem.actorOf(Props.create(Model.class), "modelActor");

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
        for (Boid boidRef : boids)
        {
            Future<Object> boidsInfoFuture = ask(boidListenerRefs.get(boidRef), MessageAskForBoidData.class, 100);
            boidsInfoFuture.
        }
    }
    //function where all rules are added to a boid
    public void findNewBoidsPositions() {

        for (Boid boidRef : boids) {

            LinkedList<Boid> neighbours = getBoidNeighbours(boidRef);
            modelActorRef.tell(new MessageApplyAllRules(neighbours, obstacles, separationWeight, cohesionWeight, alignmentWeight, opponentWeight,  obstacleRadius, obstacleWeight, bordersAvoidanceFunction), modelActorRef);
//            boid.applyAllRules(neighbours, obstacles, separationWeight, cohesionWeight, alignmentWeight, opponentWeight,  obstacleRadius, obstacleWeight, bordersAvoidanceFunction);
        }

        resetVoxels();
    }

    public ArrayList<Boid> getBoids() {
        return boids;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public void generateBoids(int count) {
        for (int i = 0; i < count; i++) {
            addBoid(null, false);
        }
    }

    public void addBoid(Vector2d pos, boolean isOpponent) {
        ActorRef boidActorRef;
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
        LinkedList<Boid> list = voxels.get(key);
        if (list != null) {
            list.add(boidRef);
            return;
        }

        list = new LinkedList<>();
        list.add(boidRef);
        voxels.put(key, list);
    }

//    private Pair<Integer, Integer> getVoxelKey(Boid boid) {
    private Pair<Integer, Integer> getVoxelKey(ActorRef boidRef) {
        Future<Object> future = ask(boidRef,
                MessageAskForBoidData.class, 100);
//        modelActorRef.a
        Vector2d pos = boidInfos.get(boidRef).getPosition();
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
        for (Boid b : boids) {
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
        boids = new ArrayList<>();
        clearVoxels();
    }

    public void removeObstacles() {
        obstacles = new ArrayList<>();
    }

    public int getBoidsCount() {
        return boidsCount;
    }

    private LinkedList<Boid> getBoidNeighbours(Boid boid) {
        LinkedList<Boid> neighbours = new LinkedList<>();
        Pair<Integer, Integer> key = getVoxelKey(boid);
        int x = key.getKey();
        int y = key.getValue();
        int[] coordsX = {-1, -1, -1, 0, 0, 0, 1, 1, 1};
        int[] coordsY = {-1, 0, 1, -1, 0, 1, -1, 0, 1};
        for (int i = 0; i < 9; i++) {
            LinkedList<Boid> voxelContent = voxels.get(new Pair<>(x + coordsX[i], y + coordsY[i]));
            if (voxelContent == null) continue;
            for (Boid other : voxelContent) {
                double dist = boid.getDistance(other);

                if (dist > 0 && dist < neighbourhoodRadius) {
                    neighbours.add(other);
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
}


