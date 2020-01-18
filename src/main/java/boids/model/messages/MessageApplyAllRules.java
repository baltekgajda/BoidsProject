package boids.model.messages;

import boids.model.Boid;
import boids.model.BoidInfo;
import boids.model.Obstacle;
import boids.model.enums.BordersAvoidanceFunction;

import java.util.ArrayList;
import java.util.LinkedList;

public class MessageApplyAllRules {
    LinkedList<BoidInfo> neighbours;
    ArrayList<Obstacle> obstacles;
    double separationWeight;
    double cohesionWeight;
    double alignmentWeight;
    double opponentWeight;
    double obstacleRadius;
    double obstacleWeight;
    BordersAvoidanceFunction bordersAvoidanceFunction;

    public MessageApplyAllRules(LinkedList<BoidInfo> neighbours, ArrayList<Obstacle> obstacles, double separationWeight, double cohesionWeight, double alignmentWeight, double opponentWeight, double obstacleRadius, double obstacleWeight, BordersAvoidanceFunction bordersAvoidanceFunction) {
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

    @Override
    public String toString() {
        return "MessageApplyAllRules{" +
                "neighbours=" + neighbours +
                ", obstacles=" + obstacles +
                ", separationWeight=" + separationWeight +
                ", cohesionWeight=" + cohesionWeight +
                ", alignmentWeight=" + alignmentWeight +
                ", opponentWeight=" + opponentWeight +
                ", obstacleRadius=" + obstacleRadius +
                ", obstacleWeight=" + obstacleWeight +
                ", bordersAvoidanceFunction=" + bordersAvoidanceFunction +
                '}';
    }
}
