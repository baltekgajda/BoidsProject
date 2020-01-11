package boids.view.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;

public class CircleShape extends Shape {

    private static double radius;
    private static double diameter;

    public void drawShape(GraphicsContext gc, Vector2d pos, double angleInRadians, Color color) {
        setShapeFill(gc, color);
        gc.fillOval(pos.getX() - radius, pos.getY() - radius, diameter, diameter);
    }

    public void drawShape(GraphicsContext gc, Vector2d pos, Color color, double radius) {
        setShapeFill(gc, color);
        gc.fillOval(pos.getX() - radius, pos.getY() - radius, 2 * radius, 2 * radius);
    }

    public void resizeShape() {
        radius = Shape.shapeSize;
        diameter = 2 * radius;
    }
}
