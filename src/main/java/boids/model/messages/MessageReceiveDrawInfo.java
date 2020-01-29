package boids.model.messages;

import akka.actor.ActorRef;
import boids.model.BoidInfo;
import boids.model.Obstacle;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageReceiveDrawInfo {
    HashMap<ActorRef, BoidInfo> boidsInfo;
    ArrayList<Obstacle> obstacles;
    int boidsCount;

    public MessageReceiveDrawInfo(HashMap<ActorRef, BoidInfo> boidsInfo, ArrayList<Obstacle> obstacles, int boidsCount) {
        this.boidsInfo = boidsInfo;
        this.obstacles = obstacles;
        this.boidsCount = boidsCount;
    }

    public HashMap<ActorRef, BoidInfo> getBoidsInfo() {
        return boidsInfo;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public int getBoidsCount() {
        return boidsCount;
    }
}
