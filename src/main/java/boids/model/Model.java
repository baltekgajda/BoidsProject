package boids.model;

import boids.view.View;
import javafx.util.Pair;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

@FunctionalInterface
interface BordersAvoidance {
    void avoidBorders(Boid boid);
}

public class Model {

    public static double obstacleRadius = 15.0;
    public static double separationWeight = 2.0;
    public static double cohesionWeight = 1.0;
    public static double alignmentWeight = 1.0;
    public static double opponentWeight = 1.5;
    public static double bordersWeight = 2.5;
    public static double obstacleWeight = 2.5;
    private static double voxelSize = setVoxelSize();
    private ArrayList<Boid> boids;
    private ArrayList<Obstacle> obstacles;
    private HashMap<Pair<Integer, Integer>, LinkedList<Boid>> voxels;
    private BordersAvoidance avoidBordersFunction;
    private boolean isTurningBackOnBordersEnabled;
    private int boidsCount;

    public Model() {
        boids = new ArrayList<>();
        obstacles = new ArrayList<>();
        voxels = new HashMap<>();
        setAvoidBordersFunctionToTurningBack();
        boidsCount = 0;
    }

    static double setVoxelSize() {
        double radius = Boid.getNeighbourhoodRadius();
        if (radius == 0) {
            voxelSize = View.CANVAS_WIDTH;
        } else {
            voxelSize = Boid.getNeighbourhoodRadius();
        }
        return voxelSize;
    }

    private static void foldOnBorders(Boid boid) {
        Vector2d pos = boid.getPosition();
        if (pos.getX() < 0) {
            pos.x += View.CANVAS_WIDTH;
        }

        if (pos.getX() > View.CANVAS_WIDTH) {
            pos.x -= View.CANVAS_WIDTH;
        }

        if (pos.getY() < 0) {
            pos.y += View.CANVAS_HEIGHT;
        }

        if (pos.getY() > View.CANVAS_HEIGHT) {
            pos.y -= View.CANVAS_HEIGHT;
        }

        boid.setPosition(pos);
    }

    private static void turnBackOnBorders(Boid boid) {
        boid.turnBackOnBorders();
    }

    //function where all rules are added to a boid
    public void findNewBoidsPositions() {
        for (Boid boid : boids) {
            LinkedList<Boid> neighbours = getBoidNeighbours(boid);
            boid.separate(neighbours, separationWeight);
            boid.provideCohesion(neighbours, cohesionWeight);
            boid.align(neighbours, alignmentWeight);
            boid.avoidOpponents(neighbours, opponentWeight);
            boid.avoidObstacles(obstacles, obstacleRadius, obstacleWeight);
            boid.moveToNewPosition();
            avoidBordersFunction.avoidBorders(boid);
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
        boidsCount++;
        Boid boid;
        if (pos == null) {
            boid = new Boid();
        } else {
            boid = new Boid(pos, isOpponent);
        }

        boids.add(boid);
        addToVoxel(boid);
    }

    private synchronized void addToVoxel(Boid boid) {
        Pair key = getVoxelKey(boid);
        LinkedList<Boid> list = voxels.get(key);
        if (list != null) {
            list.add(boid);
            return;
        }

        list = new LinkedList<>();
        list.add(boid);
        voxels.put(key, list);
    }

    private Pair<Integer, Integer> getVoxelKey(Boid boid) {
        Vector2d pos = boid.getPosition();
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
                if (dist > 0 && dist < Boid.getNeighbourhoodRadius()) {
                    neighbours.add(other);
                }
            }
        }

        return neighbours;
    }

    public void setAvoidBordersFunctionToFolding() {
        this.avoidBordersFunction = Model::foldOnBorders;
        isTurningBackOnBordersEnabled = false;
    }

    public void setAvoidBordersFunctionToTurningBack() {
        this.avoidBordersFunction = Model::turnBackOnBorders;
        isTurningBackOnBordersEnabled = true;
    }
}


