package boids.model;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import boids.model.messages.*;

import java.util.HashMap;

public class BoidInfoListener extends AbstractActor {
    HashMap<ActorRef, BoidInfo> boidInfoHashMap = new HashMap<>();
    BoidInfo selfBoidInfo;
    ActorRef boidRef;
    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(MessageBoidTellBoidListener.class, o ->{
                    selfBoidInfo = o.getBoidInfo();
                })
                .match(MessageBoidAskSelfBoidInfoListener.class, o ->{
                    getSender().tell(new MessageBoidListenerReplyBoid(self(), selfBoidInfo), self());
                })
                .match(MessageBoidAskBoidListener.class, o ->{
                    getSender().tell(new MessageBoidListenerReplyBoid(boidRef, selfBoidInfo), self());
                })
                .build();
    }

    public BoidInfoListener(ActorRef boidRef) {
        this.boidRef = boidRef;
    }
}
