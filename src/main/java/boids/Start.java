package boids;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import boids.controller.MainController;
import boids.model.Model;
import boids.view.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class Start extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ActorSystem actorSystem = ActorSystem.create("boids-simulation");
        View view = new View(primaryStage);
        ActorRef modelActorRef = actorSystem.actorOf(Props.create(Model.class, actorSystem), "modelActor");
        MainController mainController = view.getLoader().getController();
        mainController.setView(view);
        mainController.setModel(modelActorRef);
        mainController.setActorSystem(actorSystem);

    }
}
