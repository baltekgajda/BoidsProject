package boids;

import javafx.application.Application;
import javafx.stage.Stage;

public class Start extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        View view = new View(primaryStage);
        Model model = new Model();
        MainController mainController = view.getLoader().getController();
        mainController.setView(view);
        mainController.setModel(model);
    }
}
