package boids.model;

import boids.view.View;
import boids.view.shapes.Shape;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Boid {

    private final static double EDGE_RADIUS = 30.0;        //distance to avoid borders
    public static double separationRadius = 10.0;
    public static double maxSpeed = 3.0;
    public static double maxForce = 0.1;
    private static double neighbourhoodRadius = 30.0;

    private Vector2d position;
    private Vector2d velocity;
    private Vector2d forces;
    private boolean isOpponent;

    Boid() {
        this.position = getRandomPosition();
        this.velocity = getRandomVelocity();
        this.forces = new Vector2d();
        this.isOpponent = false;
    }

    Boid(Vector2d position, boolean isOpponent) {
        this.position = position;
        this.velocity = getRandomVelocity();
        this.forces = new Vector2d();
        this.isOpponent = isOpponent;
    }

    public static double getNeighbourhoodRadius() {
        return neighbourhoodRadius;
    }

    public static void setNeighbourhoodRadius(double radius) {
        neighbourhoodRadius = radius;
        Model.setVoxelSize();
    }

    private Vector2d getRandomPosition() {
        double x = new Random().nextInt(View.CANVAS_WIDTH);
        double y = new Random().nextInt(View.CANVAS_HEIGHT);
        return new Vector2d(x, y);
    }

    private Vector2d getRandomVelocity() {
        double angle = Math.random() * Math.PI * 2;
        double x = Math.cos(angle);
        double y = Math.sin(angle);
        return new Vector2d(x, y);
    }

    public Vector2d getPosition() {
        return position;
    }

    void setPosition(Vector2d position) {
        this.position = position;
    }

    public Color getColor() {
        if (isOpponent) {
            return Color.RED;
        }

        return null;
    }

    double getDistance(Boid boid) {
        Vector2d dist = new Vector2d();
        dist.sub(this.position, boid.getPosition());
        return dist.length();
    }

    public double getAngle() {
        double angle = velocity.angle(Shape.rotationVector);
        if (velocity.getX() > 0) {
            return -angle;
        }

        return angle;
    }

    void moveToNewPosition() {
        this.velocity.add(forces);
        limitVelocity();
        this.position.add(this.velocity);
        this.forces.scale(0);
    }

    //function that provides separation between boids
    void separate(LinkedList<Boid> neighbours, double behaviourWeight) {
        Vector2d diff = new Vector2d();
        Vector2d sum = new Vector2d();
        double count = 0;

        for (Boid other : neighbours) {
            if (this.getDistance(other) >= Boid.separationRadius) {
                continue;
            }

            diff.sub(this.position, other.position);
            if (diff.equals(new Vector2d())) {
                diff = new Vector2d(1.0, 1.0);
            } else {
                diff.normalize();
                diff.scale(1 / (diff.length()));
            }
            sum.add(diff);
            count += 1;
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        if (!sum.equals(new Vector2d())) {
            sum.normalize();
        }
        sum.scale(maxSpeed);
        Vector2d steerForce = calculateSteerForce(sum, this.velocity);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    //function that provides cohesion between boids
    void provideCohesion(LinkedList<Boid> neighbours, double behaviourWeight) {
        Vector2d sum = new Vector2d();
        double count = 0;

        for (Boid other : neighbours) {
            sum.add(other.position);
            count += 1;
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        Vector2d steerForce = seek(sum);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    //function that provides alignment between boids
    void align(LinkedList<Boid> neighbours, double behaviourWeight) {
        Vector2d sum = new Vector2d();
        double count = 0;

        for (Boid other : neighbours) {
            sum.add(other.velocity);
            count += 1;
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        if (!sum.equals(new Vector2d())) {
            sum.normalize();
        }
        sum.scale(maxSpeed);
        Vector2d steerForce = calculateSteerForce(sum, this.velocity);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    //function that provides opponent avoidance
    void avoidOpponents(LinkedList<Boid> neighbours, double opponentWeight) {
        if (isOpponent) {
            return;
        }

        Vector2d sum = new Vector2d();
        double count = 0;

        for (Boid other : neighbours) {
            if (other.isOpponent) {
                sum.add(other.position);
                count += 1;
            }
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        Vector2d steerForce = seek(sum);
        steerForce.scale(-opponentWeight);
        applyForce(steerForce);
    }

    //function that provides turning back on borders
    void turnBackOnBorders() {
        Vector2d avoidVector, avoidVectorX, avoidVectorY;
        if (position.getX() < EDGE_RADIUS) {
            avoidVectorX = new Vector2d(Boid.maxSpeed, velocity.getY());
        } else if (position.getX() > (View.CANVAS_WIDTH - EDGE_RADIUS)) {
            avoidVectorX = new Vector2d(-Boid.maxSpeed, velocity.getY());
        } else {
            avoidVectorX = new Vector2d();
        }

        if (position.getY() < EDGE_RADIUS) {
            avoidVectorY = new Vector2d(velocity.getX(), Boid.maxSpeed);
        } else if (position.getY() > (View.CANVAS_HEIGHT - EDGE_RADIUS)) {
            avoidVectorY = new Vector2d(velocity.getX(), -Boid.maxSpeed);
        } else {
            avoidVectorY = new Vector2d();
        }

        avoidVector = new Vector2d();
        avoidVector.add(avoidVectorX, avoidVectorY);
        if (avoidVector.equals(new Vector2d())) {
            return;
        }

        avoidVector.normalize();
        avoidVector.scale(Boid.maxSpeed);
        Vector2d steerForce = calculateSteerForce(avoidVector, velocity);
        steerForce.scale(Model.bordersWeight);
        applyForce(steerForce);
    }

    //function that provides obstacle avoidance
    void avoidObstacles(ArrayList<Obstacle> obstacles, double edgeDistance, double behaviourWeight) {
        Vector2d avoidVector = new Vector2d();
        Vector2d diff = new Vector2d();
        double length;
        for (Obstacle o : obstacles) {
            diff.sub(this.position, o.getPosition());
            length = diff.length();
            if (length > o.getRadius() + edgeDistance) {
                continue;
            }
            diff.scale(1 / length);
            avoidVector.add(diff);
        }

        if (avoidVector.equals(new Vector2d())) {
            return;
        }

        avoidVector.normalize();
        avoidVector.scale(Boid.maxSpeed);
        Vector2d steerForce = calculateSteerForce(avoidVector, velocity);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    private Vector2d seek(Vector2d target) {
        Vector2d velocity = new Vector2d();
        velocity.sub(target, this.position);
        if (!velocity.equals(new Vector2d())) {
            velocity.normalize();
        }

        velocity.scale(maxSpeed);
        return calculateSteerForce(velocity, this.velocity);
    }

    private Vector2d calculateSteerForce(Vector2d first, Vector2d second) {
        Vector2d steerForce = new Vector2d();
        steerForce.sub(first, second);
        return limitForce(steerForce);
    }

    private Vector2d limitForce(Vector2d force) {
        if (force.length() <= maxForce) {
            return force;
        }

        force.normalize();
        force.scale(maxForce);
        return force;
    }

    private void limitVelocity() {
        if (this.velocity.length() <= maxSpeed) {
            return;
        }

        this.velocity.normalize();
        this.velocity.scale(maxSpeed);
    }

    private void applyForce(Vector2d force) {
        this.forces.add(force);
    }

    public String toString() {
        return "position: " + this.position.toString() + ", velocity: " + this.velocity.toString();
    }
}
