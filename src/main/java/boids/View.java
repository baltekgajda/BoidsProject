package boids;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class View {

    public static final int WINDOW_WIDTH = 1660;
    public static final int WINDOW_HEIGHT = 840;

    private static Pane mainPane;
    private FXMLLoader loader;

    public View(Stage primaryStage) {
        setDefaultStageSettings(primaryStage);
        this.loader = new FXMLLoader(this.getClass().getResource("/fxml/Main.fxml"));
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();            //TODO handle this exception
        }

        mainPane = pane;
        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setDefaultStageSettings(Stage primaryStage) {
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        //primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/assets/images/DrawieIcon.bmp"))); TODO
        primaryStage.setTitle("boids");
        primaryStage.centerOnScreen();
    }


    public void setImageView(ImageView imageView, String image) {
        imageView.setImage(new Image(this.getClass().getResourceAsStream("/main/resources/images/" + image)));
    }

    public FXMLLoader getLoader() {
        return this.loader;
    }
}

