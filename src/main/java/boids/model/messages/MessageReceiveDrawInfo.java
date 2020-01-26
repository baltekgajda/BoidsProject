package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.BoidInfo;
import boids.model.Obstacle;

import java.util.ArrayList;

public class MessageReceiveDrawInfo {
    ArrayList<BoidInfo> boidsInfo;
    ArrayList<Obstacle> obstacles;
    int boidsCount;

    public MessageReceiveDrawInfo(ArrayList<BoidInfo> boidsInfo, ArrayList<Obstacle> obstacles, int boidsCount) {
        this.boidsInfo = boidsInfo;
        this.obstacles = obstacles;
        this.boidsCount = boidsCount;
    }

    public ArrayList<BoidInfo> getBoidsInfo() {
        return boidsInfo;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public int getBoidsCount() {
        return boidsCount;
    }
}
