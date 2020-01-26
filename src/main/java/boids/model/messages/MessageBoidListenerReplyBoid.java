package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.BoidInfo;

import java.util.HashMap;

public class MessageBoidListenerReplyBoid {
    ActorRef senderRef;
    BoidInfo boidInfo;

    public MessageBoidListenerReplyBoid(ActorRef senderRef, BoidInfo boidInfo) {
        this.senderRef = senderRef;
        this.boidInfo = boidInfo;
    }

    public BoidInfo getBoidInfo() {
        return boidInfo;
    }

    public ActorRef getSenderRef() {
        return senderRef;
    }
}
