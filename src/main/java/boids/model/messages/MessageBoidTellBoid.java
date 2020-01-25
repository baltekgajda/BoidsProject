package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.BoidInfo;

public class MessageBoidTellBoid {
    ActorRef senderActorRef;
    BoidInfo boidInfo;

    public MessageBoidTellBoid(ActorRef senderActorRef, BoidInfo boidInfo) {
        this.senderActorRef = senderActorRef;
        this.boidInfo = boidInfo;
    }

    public BoidInfo getBoidInfo() {
        return boidInfo;
    }
}
