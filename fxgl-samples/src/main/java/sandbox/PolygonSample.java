/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.settings.GameSettings;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;

import java.util.*;
import java.util.stream.Collectors;

import static com.almasb.fxgl.core.math.FXGLMath.random;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PolygonSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1200);
        settings.setHeight(800);
        settings.setTitle("PolygonSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    @Override
    protected void initGame() {

        Circle circle = new Circle(200, 200, 200, Color.BLUE);
        circle.setTranslateX(150);
        circle.setTranslateY(80);

        Polygon polygon = makePolygon(200, 200, 200, 16);
        polygon.setTranslateX(150);
        polygon.setTranslateY(215);
        polygon.setFill(Color.TRANSPARENT);
        polygon.setStroke(Color.BLACK);

        Polygon polygon2 = makePolygon(200, 200, 200, 16);
        polygon2.setTranslateX(330);
        polygon2.setTranslateY(215);

        Polygon polygon3 = makePolygon(200, 200, 200, 16);
        polygon3.setTranslateX(225);
        polygon3.setTranslateY(340);

        Polygon polygon4 = makePolygon(100, 100, 100, 16);
        polygon4.setTranslateX(465);
        polygon4.setTranslateY(230);

        getGameScene().addUINodes(polygon, polygon2, polygon3, polygon4);

        Polygon2D a = convert(polygon);
        Polygon2D b = convert(polygon2);
        Polygon2D c = convert(polygon3);
        Polygon2D d = convert(polygon4);

        List<Region> regions = Arrays.asList(
                new Region(Arrays.asList(a), Arrays.asList(b, c, d)),
                new Region(Arrays.asList(b), Arrays.asList(a, c, d)),
                new Region(Arrays.asList(c), Arrays.asList(b, a, d)),
                new Region(Arrays.asList(a, b), Arrays.asList(c, d)),
                new Region(Arrays.asList(b, c), Arrays.asList(a, d)),
                new Region(Arrays.asList(a, c), Arrays.asList(b, d)),
                new Region(Arrays.asList(a, b, c), Arrays.asList(d)),

                new Region(Arrays.asList(d, a, b, c), Arrays.asList()),
                new Region(Arrays.asList(d, a, b), Arrays.asList(c)),
                new Region(Arrays.asList(d, b), Arrays.asList(a, c)),
                new Region(Arrays.asList(d, b, c), Arrays.asList(a))
        );

        regions.get(4).getShape().vertices().forEach(p -> {
            getGameScene().addUINode(pointView(p.x(), p.y()));
        });

        List<Polygon2D> polygons = regions.stream().map(Region::getShape).collect(Collectors.toList());

        for (int i = 0; i < polygons.size(); i++) {
            Polygon2D poly1 = polygons.get(i);
            for (int j = i + 1; j < polygons.size(); j++) {
                Polygon2D poly2 = polygons.get(j);

                out:
                for (LineSegment2D edge : poly1.edges()) {
                    for (LineSegment2D edge2 : poly2.edges()) {

                        if (edge.almostEquals(edge2, 15.1)) {
                            System.out.println(poly1 + " adjacent " + poly2);
                            break out;
                        }
                    }
                }
            }
        }

//        regions.stream().map(Region::getShape).forEach(poly -> {
//            math.geom2d.Point2D p = poly.centroid();
//
//            if (poly.contains(p)) {
//                getGameScene().addUINode(pointView(p.x(), p.y()));
//            }
//        });

        //Polygon2D poly = Polygons2D.intersection(convert(polygon), convert(polygon2));

//        Polygon2D poly = Polygons2D.difference(convert(polygon), convert(polygon2));
//        poly = Polygons2D.difference(poly, convert(polygon3));

        //
    }

    private static class Region {
        private List<Polygon2D> containing;
        private List<Polygon2D> exluding;

        public Region(List<Polygon2D> containing, List<Polygon2D> exluding) {
            this.containing = containing;
            this.exluding = exluding;
        }

        public Polygon2D getShape() {
            Polygon2D shape = new SimplePolygon2D(new math.geom2d.Point2D(0, 0),
                    new math.geom2d.Point2D(1200, 0),
                    new math.geom2d.Point2D(1200, 800),
                    new math.geom2d.Point2D(0, 800));

            for (Polygon2D contour : containing) {
                shape = Polygons2D.intersection(shape, contour);
            }

            for (Polygon2D contour : exluding) {
                shape = Polygons2D.difference(shape, contour);
            }

            return shape;
        }
    }

    private Polygon2D convert(Polygon polygon) {
        List<math.geom2d.Point2D> points = new ArrayList<>();

        for (int i = 0; i < polygon.getPoints().size(); i += 2) {
            double x = polygon.getPoints().get(i) + polygon.getTranslateX();
            double y = polygon.getPoints().get(i + 1) + polygon.getTranslateY();

            points.add(new math.geom2d.Point2D(x, y));
        }

        return new SimplePolygon2D(points);
    }

    private Node pointView(double x, double y) {
        Circle circle = new Circle(5, 5, 5, Color.RED);
        circle.setTranslateX(x - 5);
        circle.setTranslateY(y - 5);
        return circle;
    }

//    private const double DegToRad = Math.PI/180;
//
//    public static Vector Rotate(this Vector v, double degrees)
//    {
//        return v.RotateRadians(degrees * DegToRad);
//    }
//
//    public static Vector RotateRadians(this Vector v, double radians)
//    {
//        var ca = Math.Cos(radians);
//        var sa = Math.Sin(radians);
//        return new Vector(ca*v.X - sa*v.Y, sa*v.X + ca*v.Y);
//    }

    private Polygon makePolygon(int centreX, int centreY, int radius, int vertices) {

        double diameter = radius * 2;

        double side = diameter / (Math.cos(Math.PI / vertices) / Math.sin(Math.PI / vertices));

        double angle = (vertices - 2) * Math.PI / vertices;

        Point2D vector = new Point2D(Math.sin(angle), Math.cos(angle)).multiply(side);
//
//        x[1] = x[0] + (Math.cos((Math.PI - angle) / 2) * sideLength);
//        y[1] = y[0] + (Math.sin((Math.PI - angle) / 2) * sideLength);

        //System.out.println("ORIGINAL: " + vector);

        List<Point2D> pointsList = new ArrayList<>();
        pointsList.add(Point2D.ZERO);

        Point2D p0 = pointsList.get(0);

        Point2D p1 = p0.add(vector);

        pointsList.add(p1);

//        Point2D p1 = new Point2D(
//                p0.getX() + (Math.cos((Math.PI - angle) / 2) * side),
//                p0.getY() + (Math.sin((Math.PI - angle) / 2) * side)
//        );

        Point2D prevPoint = p1;





        for (int i = 2; i < vertices; i++) {

            vector = rotate(vector, angle);

            //System.out.println(vector);

            prevPoint = prevPoint.add(vector);

            pointsList.add(prevPoint);
        }

        double[] points = new double[2 * vertices];

        int i = 0;
        for (Point2D p : pointsList) {
            points[i++] = p.getX();
            points[i++] = p.getY();
        }

        Polygon polygon = new Polygon(points);

        polygon.setFill(Color.TRANSPARENT);
        polygon.setStroke(Color.BLACK);

        return polygon;
    }

    private Point2D rotate(Point2D vector, double angleRadians) {
        angleRadians = Math.PI - angleRadians;

        //System.out.println(Math.toDegrees(angleRadians));

        //System.out.println("cos: " + Math.cos(angleRadians));

        while (angleRadians > Math.PI) {
            angleRadians -= Math.PI;
        }

        while (angleRadians < -Math.PI) {
            angleRadians += Math.PI;
        }

        //System.out.println("later: " + Math.toDegrees(angleRadians));

        return new Point2D(vector.getX() * Math.cos(angleRadians) - vector.getY() * Math.sin(angleRadians),
                vector.getY() * Math.cos(angleRadians) + vector.getX() * Math.sin(angleRadians));
    }

    private Polygon makePolygon2(int centre_x, int centre_y, int outerRadius, int numOfCorners) {
        double[] x = new double[numOfCorners];
        double[] y = new double[numOfCorners];

        // centre is starting point of drawing, translation to the correct
        // position will happen in the end
        x[0] = centre_x;
        y[0] = centre_y;

        // length of one side
        double sideLength = 2 * outerRadius * Math.sin(Math.PI / numOfCorners);

        // outer angle
        double angle = (2 * Math.PI) / numOfCorners;

        // second corner
        x[1] = x[0] + (Math.cos((Math.PI - angle) / 2) * sideLength);
        y[1] = y[0] + (Math.sin((Math.PI - angle) / 2) * sideLength);

        // Direction vector
        double[] vec = new double[2];
        vec[0] = x[1] - x[0];
        vec[1] = y[1] - y[0];

        // helper variables
        double x_afterRot, y_afterRot;

        for (int i = 2; i < numOfCorners; i++) {

            // translation vector calculation
            x_afterRot = vec[0] * Math.cos(-angle) - vec[1] * Math.sin(-angle);
            y_afterRot = vec[1] * Math.cos(-angle) + vec[0] * Math.sin(-angle);
            vec[0] = x_afterRot;
            vec[1] = y_afterRot;

            // new corner = old corner + translation vector after rotation
            x[i] = x[i - 1] + vec[0];
            y[i] = y[i - 1] + vec[1];

            // new direction vector
            vec[0] = x[i] - x[i - 1];
            vec[1] = y[i] - y[i - 1];
        }

        // Double to integer to match Polygon constructor
        int[] final_x = new int[numOfCorners];
        int[] final_y = new int[numOfCorners];

        double[] points = new double[numOfCorners * 2];

        for (int i = 0; i < numOfCorners; i++) {
            // translation to centre of circle
            final_x[i] = (int) Math.round(x[i]) - outerRadius ;
            final_y[i] = (int) Math.round(y[i]);

            points[i] = final_x[i];
            points[i+1] = final_y[i];
        }

        return new Polygon(points);
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
