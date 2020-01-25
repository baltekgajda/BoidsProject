package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.Boid;
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
    double obstacleRadius;
    double obstacleWeight;
    BordersAvoidanceFunction bordersAvoidanceFunction;

    public MessageModelAskBoid(ActorRef senderActorRef, ArrayList<ActorRef> neighbours, ArrayList<Obstacle> obstacles, double separationWeight, double cohesionWeight, double alignmentWeight, double opponentWeight, double obstacleRadius, double obstacleWeight, BordersAvoidanceFunction bordersAvoidanceFunction) {
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
    }
}
