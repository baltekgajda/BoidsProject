package boids.view.shapes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import javax.vecmath.Vector2d;
import java.awt.geom.AffineTransform;

public class TriangleShape extends Shape {

    private static double[] shape;

    public void drawShape(GraphicsContext gc, Vector2d pos, double angleInRadians, Color color) {
        setShapeFill(gc, color);
        double[] coords = rotateTriangle(angleInRadians);
        coords = translateTriangle(coords, pos);

        double[] xCoords = {coords[0], coords[2], coords[4]};
        double[] yCoords = {coords[1], coords[3], coords[5]};
        gc.fillPolygon(xCoords, yCoords, 3);
    }

    private double[] rotateTriangle(double angleInRadians) {
        AffineTransform rotation = new AffineTransform();
        rotation.rotate(angleInRadians, 0, 0);
        double[] rotatedShape = new double[6];
        rotation.transform(shape, 0, rotatedShape, 0, 3);
        return rotatedShape;
    }

    private double[] translateTriangle(double[] coords, Vector2d pos) {
        for (int i = 0; i < 6; i++) {
            coords[i] += pos.getX();
            coords[++i] += pos.getY();
        }
        return coords;
    }

    public void resizeShape() {
        //always pointing down when not rotated
        shape = new double[]{
                (-1.0) * Shape.shapeSize, (-2.0 / 3.0) * Shape.shapeSize,
                Shape.shapeSize, (-2.0 / 3.0) * Shape.shapeSize,
                0.0, (5.0 / 3.0) * Shape.shapeSize,
        };
    }
}
