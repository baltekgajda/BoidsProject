package boids.model.messages;

import javax.vecmath.Vector2d;

public class MessageAddObstacle {
    Vector2d position;
    double radius;

    public MessageAddObstacle(Vector2d position, double radius) {
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
