package boids.model;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import boids.model.enums.BoidMethod;
import boids.model.enums.BordersAvoidanceFunction;
import boids.model.messages.*;
import boids.view.View;
import boids.view.shapes.Shape;
import com.typesafe.config.ConfigException;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import scala.concurrent.Future;
//import akka.pattern.
import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class Boid extends AbstractActor {

    private final double EDGE_RADIUS = 30.0;        //distance to avoid borders
    private double separationRadius = 10.0;
    private double maxSpeed = 3.0;
    private double maxForce = 0.1;

    private Vector2d position;
    private Vector2d velocity;
    private Vector2d forces;
    private boolean isOpponent;
    private HashMap<ActorRef, BoidInfo> otherBoidsInfo; //TODO implement getting this data
    private ActorRef selfRef;

//    private
//    ActorRef target;
//    @Override
//    public Receive createReceive() {
//        return null;
//    }

    @Override
    public Receive createReceive() {
//        return new ReceiveBuilder()
//                .match(MessageApplyAllRules.class, mes -> mes.toString())
//
//                .build();
        return receiveBuilder()
                .match(MessageAskForBoidData.class, o -> {
                    ReplyAskForBoidData reply = new ReplyAskForBoidData(o.getActorRef(), createBoidInfo());
                    sender().tell(reply, ActorRef.noSender());
                })
                .match(MessageBoidData.class, o ->{
                    otherBoidsInfo.put(getSender(), o.getBoidInfo());
                })
                .match(MessageAllBoidData.class, o -> {
                    //TODO implement getting data for all positions
                })
                .build();
    }

    private LinkedList<BoidInfo> findNeighbours(){
        //TODO
        return null;
    }

//    private Object selectAction(BoidMethod boidMethod, ) {
//        switch (boidMethod)
//        {
//            case APPLY_ALL_RULES:
//                applyAllRules();
//        }
//    }

    Boid() {
        this.position = getRandomPosition();
        this.velocity = getRandomVelocity();
        this.forces = new Vector2d();
        this.isOpponent = false;
    }

    Boid(Vector2d position, boolean isOpponent) {
        this.position = position;
        this.velocity = getRandomVelocity();
        this.forces = new Vector2d();
        this.isOpponent = isOpponent;
    }

    private void tellOthersWhereAmI() {
        for (ActorRef actorRef: otherBoidsInfo.keySet())
            actorRef.tell(new MessageBoidData(createBoidInfo()), selfRef);
    }
    private BoidInfo createBoidInfo()
    {
        return new BoidInfo(position, velocity, forces, getAngle(), isOpponent);
    }

    private Vector2d getRandomPosition() {
        double x = new Random().nextInt(View.CANVAS_WIDTH);
        double y = new Random().nextInt(View.CANVAS_HEIGHT);
        return new Vector2d(x, y);
    }

    private Vector2d getRandomVelocity() {
        double angle = Math.random() * Math.PI * 2;
        double x = Math.cos(angle);
        double y = Math.sin(angle);
        return new Vector2d(x, y);
    }

    private Vector2d getPosition() {
        return position;
    }

    private void setPosition(Vector2d position) {
        this.position = position;
    }

    private Color getColor() {
        if (isOpponent) {
            return Color.RED;
        }

        return null;
    }

    private double getDistance(Boid boid) {
        Vector2d dist = new Vector2d();
        dist.sub(this.position, boid.getPosition());
        return dist.length();
    }

    private double getAngle() {
        double angle = velocity.angle(Shape.rotationVector);
        if (velocity.getX() > 0) {
            return -angle;
        }

        return angle;
    }

    private void applyAllRules(/*LinkedList<Boid> neighbours, */ArrayList<Obstacle> obstacles, double separationWeight, double cohesionWeight, double alignmentWeight, double opponentWeight, double obstacleRadius, double obstacleWeight, BordersAvoidanceFunction bordersAvoidanceFunction) {
        LinkedList<BoidInfo> neighbours = findNeighbours();
        this.separate(neighbours, separationWeight);
        this.provideCohesion(neighbours, cohesionWeight);
        this.align(neighbours, alignmentWeight);
        this.avoidOpponents(neighbours, opponentWeight);
        this.avoidObstacles(obstacles, obstacleRadius, obstacleWeight);
        this.moveToNewPosition();
        this.avoidBorders(bordersAvoidanceFunction);
//        System.out.println(bordersAvoidanceFunction.toString());
    }

    private void moveToNewPosition() {
        this.velocity.add(forces);
        limitVelocity();
        this.position.add(this.velocity);
        this.forces.scale(0);
    }

    //function that provides separation between boids
    private void separate(LinkedList<BoidInfo> neighbours, double behaviourWeight) {
        Vector2d diff = new Vector2d();
        Vector2d sum = new Vector2d();
        double count = 0;

        for (BoidInfo other : neighbours) {
            if (this.createBoidInfo().getDistance(other) >= separationRadius) {
                continue;
            }

            diff.sub(this.position, other.position);
            if (diff.equals(new Vector2d())) {
                diff = new Vector2d(1.0, 1.0);
            } else {
                diff.normalize();
                diff.scale(1 / (diff.length()));
            }
            sum.add(diff);
            count += 1;
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        if (!sum.equals(new Vector2d())) {
            sum.normalize();
        }
        sum.scale(maxSpeed);
        Vector2d steerForce = calculateSteerForce(sum, this.velocity);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    //function that provides cohesion between boids
    private void provideCohesion(LinkedList<BoidInfo> neighbours, double behaviourWeight) {
        Vector2d sum = new Vector2d();
        double count = 0;

        for (BoidInfo other : neighbours) {
            sum.add(other.position);
            count += 1;
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        Vector2d steerForce = seek(sum);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    //function that provides alignment between boids
    private void align(LinkedList<BoidInfo> neighbours, double behaviourWeight) {
        Vector2d sum = new Vector2d();
        double count = 0;

        for (BoidInfo other : neighbours) {
            sum.add(other.velocity);
            count += 1;
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        if (!sum.equals(new Vector2d())) {
            sum.normalize();
        }
        sum.scale(maxSpeed);
        Vector2d steerForce = calculateSteerForce(sum, this.velocity);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    //function that provides opponent avoidance
    private void avoidOpponents(LinkedList<BoidInfo> neighbours, double opponentWeight) {
        if (isOpponent) {
            return;
        }

        Vector2d sum = new Vector2d();
        double count = 0;

        for (BoidInfo other : neighbours) {
            if (other.isOpponent) {
                sum.add(other.position);
                count += 1;
            }
        }

        if (count == 0) {
            return;
        }

        sum.scale(1 / count);
        Vector2d steerForce = seek(sum);
        steerForce.scale(-opponentWeight);
        applyForce(steerForce);
    }


    //function that provides obstacle avoidance
    private void avoidObstacles(ArrayList<Obstacle> obstacles, double edgeDistance, double behaviourWeight) {
        Vector2d avoidVector = new Vector2d();
        Vector2d diff = new Vector2d();
        double length;
        for (Obstacle o : obstacles) {
            diff.sub(this.position, o.getPosition());
            length = diff.length();
            if (length > o.getRadius() + edgeDistance) {
                continue;
            }
            diff.scale(1 / length);
            avoidVector.add(diff);
        }

        if (avoidVector.equals(new Vector2d())) {
            return;
        }

        avoidVector.normalize();
        avoidVector.scale(maxSpeed);
        Vector2d steerForce = calculateSteerForce(avoidVector, velocity);
        steerForce.scale(behaviourWeight);
        applyForce(steerForce);
    }

    private void avoidBorders(BordersAvoidanceFunction bordersAvoidanceFunction) {
        switch (bordersAvoidanceFunction) {
            case FOLD_ON_BORDERS: {
//                System.out.println("folding");
                foldOnBorders();
                break;
            }
            case TURN_BACK_ON_BORDERS: {
//                System.out.println("turning");
                turnBackOnBorders();
                break;
            }
        }
    }

    private void foldOnBorders() {
        Vector2d pos = getPosition();
        if (pos.getX() < 0) {
            pos.x += View.CANVAS_WIDTH;
        }

        if (pos.getX() > View.CANVAS_WIDTH) {
            pos.x -= View.CANVAS_WIDTH;
        }

        if (pos.getY() < 0) {
            pos.y += View.CANVAS_HEIGHT;
        }

        if (pos.getY() > View.CANVAS_HEIGHT) {
            pos.y -= View.CANVAS_HEIGHT;
        }

        setPosition(pos);
    }

    //function that provides turning back on borders
    private void turnBackOnBorders() {
        Vector2d avoidVector, avoidVectorX, avoidVectorY;
        if (position.getX() < EDGE_RADIUS) {
            avoidVectorX = new Vector2d(maxSpeed, velocity.getY());
        } else if (position.getX() > (View.CANVAS_WIDTH - EDGE_RADIUS)) {
            avoidVectorX = new Vector2d(-maxSpeed, velocity.getY());
        } else {
            avoidVectorX = new Vector2d();
        }

        if (position.getY() < EDGE_RADIUS) {
            avoidVectorY = new Vector2d(velocity.getX(), maxSpeed);
        } else if (position.getY() > (View.CANVAS_HEIGHT - EDGE_RADIUS)) {
            avoidVectorY = new Vector2d(velocity.getX(), -maxSpeed);
        } else {
            avoidVectorY = new Vector2d();
        }

        avoidVector = new Vector2d();
        avoidVector.add(avoidVectorX, avoidVectorY);
        if (avoidVector.equals(new Vector2d())) {
            return;
        }

        avoidVector.normalize();
        avoidVector.scale(maxSpeed);
        Vector2d steerForce = calculateSteerForce(avoidVector, velocity);
        steerForce.scale(Model.bordersWeight);
        applyForce(steerForce);
    }

    private Vector2d seek(Vector2d target) {
        Vector2d velocity = new Vector2d();
        velocity.sub(target, this.position);
        if (!velocity.equals(new Vector2d())) {
            velocity.normalize();
        }

        velocity.scale(maxSpeed);
        return calculateSteerForce(velocity, this.velocity);
    }

    private Vector2d calculateSteerForce(Vector2d first, Vector2d second) {
        Vector2d steerForce = new Vector2d();
        steerForce.sub(first, second);
        return limitForce(steerForce);
    }

    private Vector2d limitForce(Vector2d force) {
        if (force.length() <= maxForce) {
            return force;
        }

        force.normalize();
        force.scale(maxForce);
        return force;
    }

    private void limitVelocity() {
        if (this.velocity.length() <= maxSpeed) {
            return;
        }

        this.velocity.normalize();
        this.velocity.scale(maxSpeed);
    }

    private void applyForce(Vector2d force) {
        this.forces.add(force);
    }

    public String toString() {
        return "position: " + this.position.toString() + ", velocity: " + this.velocity.toString();
    }


}

//class BoidActor extends Boid {
//    BoidActor(){
//
//    }
//}
