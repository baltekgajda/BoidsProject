package boids.model;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import boids.model.messages.MessageGenerateBoids;

public class MainControllerActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
                .match(MessageGenerateBoids.class, o -> {
                })
                .build();
    }
}
