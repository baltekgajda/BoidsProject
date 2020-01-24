package boids.model;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import manifold.ext.api.Jailbreak;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import javax.vecmath.Vector2d;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BoidTest {
    ActorSystem actorSystem;
    @BeforeAll
    public void setAll(){
        actorSystem = ActorSystem.create("actorSystem");
    }
    @Test
    public void communicationWithOtherBoidTest()
    {
        return;
//        ArrayList<ActorRef> a
//        @Jailbreak Boid boid1 = new Boid();
//        @Jailbreak Boid boid2 = new Boid();
////        boid1 = actorSystem.actorOf(Props.create(Boid.class), "boidActor");
////        boid2 = atorSystem.actorOf(Props.create(Boid.class), "boidActor");
//        boid1.position = new Vector2d(0, 0);
//        boid2.position = new Vector2d(1, 1);
//
//        boid2.otherRefs = ArrayList<Boid>;
//        boid1.tellOthersWhereAmI()
//        assertEquals(boid1, boid1);
//        assert (boid1.;
    }
}
