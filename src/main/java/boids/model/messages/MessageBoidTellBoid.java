package boids.model.messages;

import boids.model.BoidInfo;

public class MessageBoidTellBoid {
    BoidInfo boidInfo;

    public MessageBoidTellBoid(BoidInfo boidInfo) {
        this.boidInfo = boidInfo;
    }

    public BoidInfo getBoidInfo() {
        return boidInfo;
    }
}
