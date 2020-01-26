package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.BoidInfo;

import java.util.HashMap;

public class MessageBoidListenerReplyBoid {
    HashMap<ActorRef, BoidInfo> boidInfoHashMap;
    ActorRef senderRef;

    public MessageBoidListenerReplyBoid(HashMap<ActorRef, BoidInfo> boidInfoHashMap, ActorRef senderRef) {
        this.boidInfoHashMap = boidInfoHashMap;
        this.senderRef = senderRef;
    }

    public HashMap<ActorRef, BoidInfo> getBoidInfoHashMap() {
        return boidInfoHashMap;
    }

    public ActorRef getSenderRef() {
        return senderRef;
    }
}
