package boids.model;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import boids.model.messages.MessageBoidAskSelfBoidInfoListener;
import boids.model.messages.MessageBoidListenerReplyBoid;
import boids.model.messages.MessageBoidReplyModel;
import boids.model.messages.MessageBoidTellBoidListener;

import java.util.HashMap;

public class BoidInfoListener extends AbstractActor {
    HashMap<ActorRef, BoidInfo> boidInfoHashMap = new HashMap<>();
    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(MessageBoidTellBoidListener.class, o ->{
                    boidInfoHashMap.put(getSender(), o.getBoidInfo());
                })
                .match(MessageBoidAskSelfBoidInfoListener.class, o ->{
                    //TODO self() jak chcesz swojego refa podać?
                    getSender().tell(new MessageBoidListenerReplyBoid(boidInfoHashMap,self()), self());
                })
                .build();
    }
}
