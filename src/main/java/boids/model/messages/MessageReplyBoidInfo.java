package boids.model.messages;

import boids.model.BoidInfo;

public class MessageReplyBoidInfo {
    BoidInfo boidInfo;

    public MessageReplyBoidInfo(BoidInfo boidInfo) {
        this.boidInfo = boidInfo;
    }

    public BoidInfo getBoidInfo() {
        return boidInfo;
    }
}
