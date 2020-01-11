package boids.model;

import javax.vecmath.Vector2d;

public class Obstacle {

    private Vector2d position;
    private double radius;

    public Obstacle(Vector2d position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    public Vector2d getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }
}
