package boids.model;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import boids.model.messages.ReplyAskForBoidData;

public class BoidListener extends AbstractActor {
    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(ReplyAskForBoidData.class, d ->{
                    System.out.println(d.toString());
                })
                .build();
    }
}
