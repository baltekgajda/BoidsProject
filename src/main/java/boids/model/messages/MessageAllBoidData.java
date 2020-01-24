package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.BoidInfo;

import java.util.HashMap;

public class MessageAllBoidData {
    HashMap <ActorRef, BoidInfo> boidInfos;

    public MessageAllBoidData(HashMap <ActorRef, BoidInfo> boidInfos) {
        this.boidInfos = boidInfos;
    }

    public HashMap <ActorRef, BoidInfo> getBoidInfos() {
        return boidInfos;
    }
}
