package boids.model.messages;

import boids.model.BoidInfo;

public class MessageBoidData {
    BoidInfo boidInfo;

    public MessageBoidData(BoidInfo boidInfo) {
        this.boidInfo = boidInfo;
    }

    public BoidInfo getBoidInfo() {
        return boidInfo;
    }
}
