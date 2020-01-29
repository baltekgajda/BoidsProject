package boids.model.messages;

import javax.vecmath.Vector2d;

public class MessageAddBoid {
    Vector2d position;
    Boolean isOpponent;

    public MessageAddBoid(Vector2d position, Boolean isOpponent) {
        this.position = position;
        this.isOpponent = isOpponent;
    }

    public Vector2d getPosition() {
        return position;
    }

    public Boolean getOpponent() {
        return isOpponent;
    }
}
