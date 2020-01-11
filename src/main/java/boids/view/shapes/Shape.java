package boids.view.shapes;

import boids.model.Boid;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;

public abstract class Shape {

    public static final Vector2d rotationVector = new Vector2d(0.0, 1.0);
    private final static double LINE_WIDTH = 1.0;
    private final static double SEPARATION_RADIUS_DASHES = 10.0;
    public static double shapeSize = 3.0;
    private static Color fillColor = Color.rgb(255, 255, 255, 0.7);
    private static Color strokeColor = Color.rgb(255, 255, 255, 0.1);

    Shape() {
        resizeShape();
    }

    public abstract void drawShape(GraphicsContext gc, Vector2d pos, double angleInRadians, Color color);

    public abstract void resizeShape();

    void setShapeFill(GraphicsContext gc, Color color) {
        if (color != null) {
            gc.setFill(color);
            return;
        }

        gc.setFill(fillColor);
    }

    private void setStroke(GraphicsContext gc, double... dashes) {
        gc.setStroke(strokeColor);
        gc.setLineWidth(LINE_WIDTH);
        gc.setLineDashes(dashes);
    }

    public void drawNeighbourhoodRadius(GraphicsContext gc, Vector2d pos) {
        double radius = Boid.getNeighbourhoodRadius();
        double diameter = 2 * radius;
        setStroke(gc);
        gc.strokeOval(pos.getX() - radius, pos.getY() - radius, diameter, diameter);
    }

    public void drawSeparationRadius(GraphicsContext gc, Vector2d pos) {
        double diameter = Boid.separationRadius;
        double radius = diameter / 2;           //for visual purposes separation radius displayed is divided by 2 to better see separation itself
        setStroke(gc, SEPARATION_RADIUS_DASHES);
        gc.strokeOval(pos.getX() - radius, pos.getY() - radius, diameter, diameter);
    }
}
