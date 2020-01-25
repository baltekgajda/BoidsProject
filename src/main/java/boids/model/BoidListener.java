package boids.model;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import boids.model.messages.MessageBoidReplyModel;

public class BoidListener extends AbstractActor {
    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(MessageBoidReplyModel.class, d ->{
                    System.out.println(d.toString());
                })
                .build();
    }
}
