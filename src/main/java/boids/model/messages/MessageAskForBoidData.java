package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.Boid;

public class MessageAskForBoidData {
    ActorRef actorRef;

    public MessageAskForBoidData(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }
}
