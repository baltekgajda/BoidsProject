package boids.model;

import javax.vecmath.Vector2d;

public class BoidInfo {
    Vector2d position;

    public Vector2d getPosition() {
        return position;
    }

    public double getDistance(BoidInfo boidInfo) {
        Vector2d dist = new Vector2d();
        dist.sub(this.position, boidInfo.getPosition());
        return dist.length();
    }
}