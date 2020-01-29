package boids.model.messages;

public class MessageSetAvoidance {
    Boolean turningBack;
    Boolean folding;

    //arguments cannot be true or false at the same time
    public MessageSetAvoidance(Boolean turningBack, Boolean folding) {
        this.turningBack = turningBack;
        this.folding = folding;
    }

    public Boolean isAvoidanceFolding() {
        return folding;
    }
}
