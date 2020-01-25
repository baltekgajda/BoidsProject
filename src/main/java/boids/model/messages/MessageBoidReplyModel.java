package boids.model.messages;
//import boids.model.B

import akka.actor.ActorRef;
import boids.model.BoidInfo;

public class MessageBoidReplyModel {
    BoidInfo boidInfo;
    ActorRef senderActorRef;
    public MessageBoidReplyModel(ActorRef senderActorRef, BoidInfo boidInfo) {
        this.boidInfo = boidInfo;
        this.senderActorRef = senderActorRef;
    }

    public BoidInfo getBoidInfo() {
        return boidInfo;
    }

    public ActorRef getSenderActorRef() {
        return senderActorRef;
    }
}
