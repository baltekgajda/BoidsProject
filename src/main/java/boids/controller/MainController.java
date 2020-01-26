package boids.controller;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import boids.model.Boid;
import boids.model.BoidInfo;
import boids.model.Model;
import boids.model.Obstacle;
import boids.model.messages.*;
import boids.view.View;
import boids.view.shapes.CircleShape;
import boids.view.shapes.Shape;
import boids.view.shapes.TriangleShape;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javax.vecmath.Vector2d;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class MainController {

    private final static long animationRate = 20;
    private static long animationRateStep = 10;
    private ActorRef modelRef;
    private View view;
    private Shape shape = new TriangleShape();
    private ActorSystem actorSystem;
    @FXML
    private Canvas canvas;
    @FXML
    private Label boidsCount;
    @FXML
    private ToggleButton shapeChanger;
    @FXML
    private ToggleButton pauseButton;
    @FXML
    private Slider boidsCountSlider;
    @FXML
    private Slider separationSlider;
    @FXML
    private Slider cohesionSlider;
    @FXML
    private Slider alignmentSlider;
    @FXML
    private Slider neighbourhoodRadiusSlider;
    @FXML
    private Slider separationRadiusSlider;
    @FXML
    private Slider shapeSizeSlider;
    @FXML
    private Slider maxSpeedSlider;
    @FXML
    private Slider maxForceSlider;
    @FXML
    private Slider opponentSlider;
    @FXML
    private Slider obstacleSizeSlider;
    @FXML
    private Slider obstacleWeightSlider;
    @FXML
    private Slider bordersWeightSlider;
    @FXML
    private Label boidsCountSliderInfo;
    @FXML
    private Label separationSliderInfo;
    @FXML
    private Label cohesionSliderInfo;
    @FXML
    private Label alignmentSliderInfo;
    @FXML
    private Label neighbourhoodRadiusSliderInfo;
    @FXML
    private Label separationRadiusSliderInfo;
    @FXML
    private Label shapeSizeSliderInfo;
    @FXML
    private Label maxSpeedSliderInfo;
    @FXML
    private Label maxForceSliderInfo;
    @FXML
    private Label opponentSliderInfo;
    @FXML
    private Label obstacleSizeSliderInfo;
    @FXML
    private Label obstacleWeightSliderInfo;
    @FXML
    private Label bordersWeightSliderInfo;
    @FXML
    private ToggleButton enableNeighbourhoodRadiusButton;
    @FXML
    private ImageView enableNeighbourhoodRadiusImage;
    @FXML
    private ToggleButton enableSeparationRadiusButton;
    private Timeline animationTimeline = setTimeline(animationRate);
    @FXML
    private ImageView enableSeparationRadiusImage;
    @FXML
    private ImageView shapeChangerImage;
    @FXML
    private ToggleButton edgeFoldingButton;
    @FXML
    private ToggleButton edgeTurningBackButton;
    @FXML
    private ToggleButton enableOpponentsButton;
    @FXML
    private ToggleButton enableObstaclesButton;

    @FXML
    private void changeShape() {
        //TODO dodac aska na drawBoids samo ale chyba to usunę
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        if (shapeChanger.isSelected()) {
//            view.setImageView(shapeChangerImage, "circleShape.png");
//            shape = new CircleShape();
//            drawBoids(gc);
//            return;
//        }
//
//        view.setImageView(shapeChangerImage, "triangleShape.png");
//        shape = new TriangleShape();
//        drawBoids(gc);
    }

    @FXML
    private void startBoids() {
        //TODO ask na rysowanie obstacles?
        stopAnimation();
        modelRef.tell(new MessageGenerateBoids((int) boidsCountSlider.getValue()), null);
        if (pauseButton.isSelected()) {
            //TODO drawObstacles(canvas.getGraphicsContext2D());
            return;
        }

        runAnimation();
    }


    private void runAnimation() {
        animationTimeline.play();
    }

    @FXML
    private void pauseAnimation() {
        if (pauseButton.isSelected()) {
            animationTimeline.pause();
            return;
        }

        animationTimeline.play();
    }

    @FXML
    private void stopAnimation() {
        animationTimeline.stop();
        modelRef.tell(new MessageRemoveBoidsAndObstacles(), null);
        setBoidsCount();
        clearCanvas(canvas.getGraphicsContext2D());
    }

    @FXML
    private void changeEnableNeighbourhoodRadiusButtonImage() {
        if (enableNeighbourhoodRadiusButton.isSelected()) {
            view.setImageView(enableNeighbourhoodRadiusImage, "tickIcon.png");
            return;
        }

        view.setImageView(enableNeighbourhoodRadiusImage, "noIcon.png");
    }

    @FXML
    private void changeEnableSeparationRadiusButtonImage() {
        if (enableSeparationRadiusButton.isSelected()) {
            view.setImageView(enableSeparationRadiusImage, "tickIcon.png");
            return;
        }

        view.setImageView(enableSeparationRadiusImage, "noIcon.png");
    }

    @FXML
    private void changeEdgeHandlingToFolding() {
        if (edgeFoldingButton.isSelected()) {
            edgeTurningBackButton.setSelected(false);
            modelRef.tell(new MessageSetAvoidance(false, true), null);
            return;
        }

        edgeTurningBackButton.setSelected(true);
        modelRef.tell(new MessageSetAvoidance(true, false), null);
    }

    @FXML
    private void changeEdgeHandlingToTurningBack() {
        if (edgeTurningBackButton.isSelected()) {
            edgeFoldingButton.setSelected(false);
            modelRef.tell(new MessageSetAvoidance(true, false), null);
            return;
        }

        edgeFoldingButton.setSelected(true);
        modelRef.tell(new MessageSetAvoidance(false, true), null);
    }

    @FXML
    private void disableObstaclesButton() {
        if (enableOpponentsButton.isSelected()) {
            enableObstaclesButton.setSelected(false);
        }
    }

    @FXML
    void disableOpponentsButton() {
        if (enableObstaclesButton.isSelected()) {
            enableOpponentsButton.setSelected(false);
        }
    }

    @FXML
    public void initialize() {
        initializeSliders();
    }

    private void initializeSliders() {
        DecimalFormat df = new DecimalFormat("#0.0", new DecimalFormatSymbols(Locale.ENGLISH));

        boidsCountSlider.valueProperty().addListener(arg -> {
            double value = boidsCountSlider.getValue();
            String formattedValue = df.format(value);
            boidsCountSliderInfo.setText(formattedValue);
        });

        separationSlider.valueProperty().addListener(arg -> {
            double value = separationSlider.getValue();
            String formattedValue = df.format(value);
            separationSliderInfo.setText(formattedValue);
            Model.separationWeight = Double.parseDouble(formattedValue);
        });

        cohesionSlider.valueProperty().addListener(arg -> {
            double value = cohesionSlider.getValue();
            String formattedValue = df.format(value);
            cohesionSliderInfo.setText(formattedValue);
            Model.cohesionWeight = Double.parseDouble(formattedValue);
        });

        alignmentSlider.valueProperty().addListener(arg -> {
            double value = alignmentSlider.getValue();
            String formattedValue = df.format(value);
            alignmentSliderInfo.setText(formattedValue);
            Model.alignmentWeight = Double.parseDouble(formattedValue);
        });

        shapeSizeSlider.valueProperty().addListener(arg -> {
            double value = shapeSizeSlider.getValue();
            String formattedValue = df.format(value);
            shapeSizeSliderInfo.setText(formattedValue);
            Shape.shapeSize = Double.parseDouble(formattedValue);
            shape.resizeShape();
        });

        neighbourhoodRadiusSlider.valueProperty().addListener(arg -> {
            double value = neighbourhoodRadiusSlider.getValue();
            String formattedValue = df.format(value);
            neighbourhoodRadiusSliderInfo.setText(formattedValue);

            Model.neighbourhoodRadius = Double.parseDouble(formattedValue);
        });

        separationRadiusSlider.valueProperty().addListener(arg -> {
            double value = separationRadiusSlider.getValue();
            String formattedValue = df.format(value);
            separationRadiusSliderInfo.setText(formattedValue);
            Model.separationRadius = Double.parseDouble(formattedValue);
        });

        maxSpeedSlider.valueProperty().addListener(arg -> {
            double value = maxSpeedSlider.getValue();
            String formattedValue = df.format(value);
            maxSpeedSliderInfo.setText(formattedValue);
            Model.maxSpeed = Double.parseDouble(formattedValue);
        });

        maxForceSlider.valueProperty().addListener(arg -> {
            double value = maxForceSlider.getValue();
            String formattedValue = df.format(value);
            maxForceSliderInfo.setText(formattedValue);
            Model.maxForce = Double.parseDouble(formattedValue);
        });

        opponentSlider.valueProperty().addListener(arg -> {
            double value = opponentSlider.getValue();
            String formattedValue = df.format(value);
            opponentSliderInfo.setText(formattedValue);
            Model.opponentWeight = Double.parseDouble(formattedValue);
        });

        obstacleSizeSlider.valueProperty().addListener(arg -> {
            double value = obstacleSizeSlider.getValue();
            String formattedValue = df.format(value);
            obstacleSizeSliderInfo.setText(formattedValue);
            Model.obstacleRadius = Double.parseDouble(formattedValue);
        });

        obstacleWeightSlider.valueProperty().addListener(arg -> {
            double value = obstacleWeightSlider.getValue();
            String formattedValue = df.format(value);
            obstacleWeightSliderInfo.setText(formattedValue);
            Model.obstacleWeight = Double.parseDouble(formattedValue);
        });

        bordersWeightSlider.valueProperty().addListener(arg -> {
            double value = bordersWeightSlider.getValue();
            String formattedValue = df.format(value);
            bordersWeightSliderInfo.setText(formattedValue);
            Model.bordersWeight = Double.parseDouble(formattedValue);
        });

        boidsCountSliderInfo.setText(Double.toString(boidsCountSlider.getValue()));
        separationSlider.setValue(Model.separationWeight);
        cohesionSlider.setValue(Model.cohesionWeight);
        alignmentSlider.setValue(Model.alignmentWeight);
        neighbourhoodRadiusSlider.setValue(Model.getNeighbourhoodRadius());
        separationRadiusSlider.setValue(Model.separationRadius);
        shapeSizeSlider.setValue(Shape.shapeSize);
        maxSpeedSlider.setValue(Model.maxSpeed);
        maxForceSlider.setValue(Model.maxForce);
        opponentSlider.setValue(Model.opponentWeight);
        obstacleSizeSlider.setValue(Model.obstacleRadius);
        obstacleWeightSlider.setValue(Model.obstacleWeight);
        bordersWeightSlider.setValue(Model.bordersWeight);
    }

    private Timeline setTimeline(long rate) {

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        event -> performAnimationStep(canvas.getGraphicsContext2D())

                ),
                new KeyFrame(Duration.millis(rate))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        animationTimeline = timeline;
        return timeline;
    }

    private void performAnimationStep(GraphicsContext gc) {
        long startTime = System.currentTimeMillis();
        draw(gc);
        long estimatedTime = System.currentTimeMillis() - startTime;
        long animationCurrentRate = (long) animationTimeline.getKeyFrames().get(1).getTime().toMillis();
        long modulus = estimatedTime % animationRateStep;
        long newRate = estimatedTime - modulus + 2 * animationRateStep;
        if (newRate < animationRate)
            newRate = animationRate;
        if (newRate == animationCurrentRate) return;
        animationTimeline.stop();
        setTimeline(newRate);
        runAnimation();
    }

    private void drawBoids(GraphicsContext gc, ArrayList<BoidInfo> boidsInfos) {
        for (BoidInfo boidInfo : boidsInfos) {
            shape.drawShape(gc, boidInfo.getPosition(), boidInfo.getAngle(), boidInfo.createColor());
            if (enableNeighbourhoodRadiusButton.isSelected()) {
                shape.drawNeighbourhoodRadius(gc, boidInfo.getPosition());
            }
            if (enableSeparationRadiusButton.isSelected()) {
                shape.drawSeparationRadius(gc, boidInfo.getPosition());
            }
        }
    }

    private void drawObstacles(GraphicsContext gc, ArrayList<Obstacle> obstacles) {
        CircleShape circle = new CircleShape();
        for (Obstacle o : obstacles) {
            circle.drawShape(gc, o.getPosition(), Color.GAINSBORO, o.getRadius());
        }
    }

    private void draw(GraphicsContext gc) {
        //TODO dodać ask do modelu i handler tam -> zwracający ArrayList<BoidInfo>, ArrayList<Obstacle>, boidsCount
        clearCanvas(gc);
        setBoidsCount();
//        drawBoids(gc);
//        drawObstacles(gc);
    }

    private void clearCanvas(GraphicsContext gc) {
        gc.clearRect(0.0, 0.0, View.CANVAS_WIDTH, View.CANVAS_HEIGHT);
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setModel(ActorRef ref) {
        this.modelRef = ref;
        setCanvasEventHandlers();
    }

    private void setCanvasEventHandlers() {
        canvas.setOnMousePressed(event -> {
            if (addObstacleEventHandler(event)) return;

            if (addBoidEventHandler(event)) return;

            if (animationTimeline.getStatus() == Animation.Status.STOPPED) {
                runAnimation();
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (addObstacleEventHandler(event)) return;

            addBoidEventHandler(event);
        });
    }

    private boolean addBoidEventHandler(MouseEvent event) {
        modelRef.tell(new MessageAddBoid(new Vector2d(event.getX(), event.getY()), enableOpponentsButton.isSelected()), null);
        if (!pauseButton.isSelected()) return false;
        draw(canvas.getGraphicsContext2D());
        return true;
    }

    private boolean addObstacleEventHandler(MouseEvent event) {
        if (!enableObstaclesButton.isSelected()) {
            return false;
        }

        modelRef.tell(new MessageAddObstacle(new Vector2d(event.getX(), event.getY()), obstacleSizeSlider.getValue()),null);
        draw(canvas.getGraphicsContext2D());
        return true;
    }

    private void setBoidsCount() {
        //TODO dodac message get boids cont ask
        //boidsCount.setText(Integer.toString(model.getBoidsCount()));
    }

    private void setBoidsCount(int count) {
        boidsCount.setText(Integer.toString(count));
    }

    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }
}
