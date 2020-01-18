package boids.model.messages;
//import boids.model.B

import akka.actor.ActorRef;
import boids.model.BoidInfo;

public class ReplyAskForBoidData {
    BoidInfo boidInfo;
    ActorRef actorRef;
    public ReplyAskForBoidData(ActorRef actorRef, BoidInfo boidInfo) {
        this.boidInfo = boidInfo;
        this.actorRef = actorRef;
    }

    public BoidInfo getBoidInfo() {
        return boidInfo;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }
}
