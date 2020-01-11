package boids.view;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class View {

    public static final int CANVAS_HEIGHT = 800;
    public static final int CANVAS_WIDTH = 1200;
    private static final int WINDOW_WIDTH = 1660;
    private static final int WINDOW_HEIGHT = 840;
    private FXMLLoader loader;

    public View(Stage primaryStage) {
        setDefaultStageSettings(primaryStage);
        this.loader = new FXMLLoader(this.getClass().getResource("/fxml/Main.fxml"));
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }

        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setDefaultStageSettings(Stage primaryStage) {
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/assets/images/boidsLogo.bmp")));
        primaryStage.setTitle("boids");
        primaryStage.centerOnScreen();
    }

    public void setImageView(ImageView imageView, String image) {
        imageView.setImage(new Image(this.getClass().getResourceAsStream("/assets/images/" + image)));
    }

    public FXMLLoader getLoader() {
        return this.loader;
    }
}

