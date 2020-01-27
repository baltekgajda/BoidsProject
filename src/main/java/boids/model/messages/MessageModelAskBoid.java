package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.Obstacle;
import boids.model.enums.BordersAvoidanceFunction;

import java.util.ArrayList;

public class MessageModelAskBoid {
    ActorRef senderActorRef;
    ArrayList<ActorRef> neighbours;
    ArrayList<Obstacle> obstacles;
    double separationWeight;
    double cohesionWeight;
    double alignmentWeight;
    double opponentWeight;
    double bordersWeight;
    double obstacleRadius;
    double separationRadius;
    double obstacleWeight;
    double maxSpeed;
    double maxForce;
    BordersAvoidanceFunction bordersAvoidanceFunction;

    public MessageModelAskBoid(ActorRef senderActorRef, ArrayList<ActorRef> neighbours, ArrayList<Obstacle> obstacles, double separationWeight,
                               double cohesionWeight, double alignmentWeight, double opponentWeight, double obstacleRadius, double obstacleWeight,
                               double separationRadius, double bordersWeight, double maxSpeed, double maxForce,
                               BordersAvoidanceFunction bordersAvoidanceFunction
    ) {
        this.senderActorRef = senderActorRef;
        this.neighbours = neighbours;
        this.obstacles = obstacles;
        this.separationWeight = separationWeight;
        this.cohesionWeight = cohesionWeight;
        this.alignmentWeight = alignmentWeight;
        this.opponentWeight = opponentWeight;
        this.obstacleRadius = obstacleRadius;
        this.obstacleWeight = obstacleWeight;
        this.bordersAvoidanceFunction = bordersAvoidanceFunction;
        this.separationRadius = separationRadius;
        this.bordersWeight = bordersWeight;
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
    }

    public MessageModelAskBoid(ActorRef boidRef) {
    }

    public ActorRef getSenderActorRef() {
        return senderActorRef;
    }

    public ArrayList<ActorRef> getNeighbours() {
        return neighbours;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public double getSeparationWeight() {
        return separationWeight;
    }

    public double getCohesionWeight() {
        return cohesionWeight;
    }

    public double getAlignmentWeight() {
        return alignmentWeight;
    }

    public double getOpponentWeight() {
        return opponentWeight;
    }

    public double getObstacleRadius() {
        return obstacleRadius;
    }

    public double getObstacleWeight() {
        return obstacleWeight;
    }

    public BordersAvoidanceFunction getBordersAvoidanceFunction() {
        return bordersAvoidanceFunction;
    }

    public double getBordersWeight() {
        return bordersWeight;
    }

    public double getSeparationRadius() {
        return separationRadius;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMaxForce() {
        return maxForce;
    }
}
