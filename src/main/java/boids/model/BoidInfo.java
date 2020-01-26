package boids.model;

import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;

public class BoidInfo {
    private Vector2d position;
    private Vector2d velocity;
    private Vector2d forces;
    private double angle;
    private boolean isOpponent;

    public Vector2d getPosition() {
        return position;
    }

    public Vector2d getVelocity() {
        return velocity;
    }

    public Vector2d getForces() {
        return forces;
    }

    public double getAngle() {
        return angle;
    }

    public boolean getIsOpponent() { return isOpponent;}

    public BoidInfo(Vector2d position, Vector2d velocity, Vector2d forces, double angle, boolean isOpponent) {
        this.position = position;
        this.velocity = velocity;
        this.forces = forces;
        this.angle = angle;
        this.isOpponent = isOpponent;
    }


    public double getDistance(BoidInfo boidInfo) {
        Vector2d dist = new Vector2d();
        dist.sub(this.position, boidInfo.getPosition());
        return dist.length();
    }

    public Color createColor() {
        if (isOpponent) {
            return Color.RED;
        }

        return null;
    }
}