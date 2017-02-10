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
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;
import org.delaunay.TriangulationDemo;
import org.delaunay.algorithm.Triangulation;
import org.delaunay.algorithm.Triangulations;

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

    private Triangulation t = new Triangulation();

    private void test() {
        try {
            t.setDebugLogger(new Triangulation.DebugLogger() {
                @Override
                public void debug(String str) {
                    System.out.println(str);
                }
            });

            //t.addAllVertices(Triangulations.randomVertices(1000, 400, 400));
            t.triangulate();
            TriangulationDemo.drawTriangulation(t, 800, 800, "triangulation.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void test2() {
        SimplePolygon2D d;
    }

    @Override
    protected void initGame() {

        Circle circle = new Circle(200, 200, 200, Color.BLUE);
        circle.setTranslateX(150);
        circle.setTranslateY(80);

        Polygon polygon = makePolygon(200, 200, 200, 12);
        polygon.setTranslateX(150);
        polygon.setTranslateY(80);

        Circle circle2 = new Circle(200, 200, 200, Color.YELLOWGREEN);
        circle2.setTranslateX(330);
        circle2.setTranslateY(215);

        Polygon polygon2 = makePolygon(200, 200, 200, 12);
        polygon2.setTranslateX(330);
        polygon2.setTranslateY(215);

        Circle circle3 = new Circle(200, 200, 200, Color.ROSYBROWN);
        circle3.setTranslateX(225);
        circle3.setTranslateY(340);

        Polygon polygon3 = makePolygon(200, 200, 200, 16);
        polygon3.setTranslateX(225);
        polygon3.setTranslateY(340);

        Circle circle4 = new Circle(100, 100, 100, Color.DARKGOLDENROD);
        circle4.setTranslateX(465);
        circle4.setTranslateY(230);

        Polygon polygon4 = makePolygon(100, 100, 100, 8);
        polygon4.setTranslateX(465);
        polygon4.setTranslateY(230);

        getGameScene().addUINodes(circle, circle2, circle3, circle4, polygon, polygon2, polygon3, polygon4);

        Polygon2D a = convert(polygon);
        Polygon2D b = convert(polygon2);
        Polygon2D c = convert(polygon3);
        Polygon2D d = convert(polygon4);

        List<Region> regions = Arrays.asList(
                new Region("a", Arrays.asList(a), Arrays.asList(b, c, d)), //0
                new Region("b",Arrays.asList(b), Arrays.asList(a, c, d)),
                new Region("c",Arrays.asList(c), Arrays.asList(b, a, d)), // 2
                new Region("ab",Arrays.asList(a, b), Arrays.asList(c, d)), //3
                new Region("bc",Arrays.asList(b, c), Arrays.asList(a, d)), //4
                new Region("ac",Arrays.asList(a, c), Arrays.asList(b, d)),
                new Region("abc",Arrays.asList(a, b, c), Arrays.asList(d)),

                new Region("dabc",Arrays.asList(d, a, b, c), Arrays.asList()), //7
                new Region("dab",Arrays.asList(d, a, b), Arrays.asList(c)),
                new Region("db",Arrays.asList(d, b), Arrays.asList(a, c)),
                new Region("dbc",Arrays.asList(d, b, c), Arrays.asList(a))
        );

        regions.stream().map(Region::getShape).forEach(poly -> {
            math.geom2d.Point2D p = poly.centroid();

            getGameScene().addUINode(pointView(p.x(), p.y()));

            Point2D p0 = Polylabel.findCenter(poly);

            Circle c0 = pointView(p0.getX(), p0.getY());
            c0.setFill(Color.YELLOW);

            getGameScene().addUINode(c0);
        });





//
//        regions.get(4).getShape().vertices().forEach(p -> {
//            getGameScene().addUINode(pointView(p.x(), p.y()));
//        });
//
//        regions.get(6).getShape().vertices().forEach(p -> {
//            getGameScene().addUINode(pointView(p.x(), p.y()));
//        });
//
//
//
//
//        List<Polygon2D> polygons = new ArrayList<>();
//
//        Map<Polygon2D, String> names = new HashMap<>();
//
//        regions.forEach(r -> {
//            Polygon2D p = r.getShape();
//            names.put(p, r.name);
//            polygons.add(p);
//        });
//
//        // TOP ADJ
//        for (int i = 0; i < polygons.size(); i++) {
//            Polygon2D poly1 = polygons.get(i);
//            for (int j = i + 1; j < polygons.size(); j++) {
//                Polygon2D poly2 = polygons.get(j);
//
//                String name1 = names.get(poly1);
//                String name2 = names.get(poly2);
//
//                if (Math.abs(name1.length() - name2.length()) == 1) {
//                    System.out.println(name1 + " checking " + name2);
//
//                    out:
//                    for (math.geom2d.Point2D v1 : poly1.vertices()) {
//                        for (math.geom2d.Point2D v2 : poly2.vertices()) {
//                            if ((int) v1.x() == (int) v2.x() && (int) v1.y() == (int) v2.y()) {
//                                System.out.println(v1 + " " + v2);
//                                System.out.println(name1 + " adjacent " + name2);
//                                break out;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        List<math.geom2d.Point2D> centers = new ArrayList<>();
//        centers.add(new math.geom2d.Point2D(590, 100));
//

//
//        centers.forEach(p -> getGameScene().addUINode(pointView(p.x(), p.y())));
//
////        for (int i = 0; i < centers.size(); i++) {
////            math.geom2d.Point2D c1 = centers.get(i);
////
////            for (int j = i + 1; j < centers.size(); j++) {
////                math.geom2d.Point2D c2 = centers.get(j);
////
////                getGameScene().addUINode(new Line(c1.x(), c1.y(), c2.x(), c2.y()));
////            }
////        }
//
//
//
//
//
//        b = regions.get(1).getShape();
//
//
//        Box2D bounds = b.boundingBox();
//        System.out.println(bounds.getMinX() + " " + bounds.getMinY() + " " + bounds.getMaxX() + " " + bounds.getMaxY());
//
//        Point2D center = new Point2D(bounds.getMinX() + bounds.getWidth() / 2, bounds.getMinY() + bounds.getHeight() / 2);
//
//        System.out.println(center);
//
//        //////////////////////////////////////////// ROUTE EDGES
//
//        List<Point2D> midPoints = new ArrayList<>();
//
//        for (int angle = 0; angle < 360; angle += 15) {
//            Point2D vector = new Point2D(FXGLMath.cosDeg(angle), FXGLMath.sinDeg(angle)).normalize();
//
//            Point2D currentPoint = center.add(vector);
//
//            Point2D startPoint = null;
//            Point2D endPoint = null;
//
//            while (bounds.contains(p(currentPoint))) {
//
//                if (b.contains(p(currentPoint))) {
//                    if (startPoint == null) {
//                        startPoint = new Point2D(currentPoint.getX(), currentPoint.getY());
//
//                        Circle vertex = new Circle(5, Color.RED);
//                        vertex.setTranslateX(currentPoint.getX());
//                        vertex.setTranslateY(currentPoint.getY());
//                        //getGameScene().addUINode(vertex);
//                    }
//                } else {
//                    if (startPoint != null && endPoint == null) {
//                        endPoint = new Point2D(currentPoint.getX(), currentPoint.getY());
//
//                        Circle vertex = new Circle(5, Color.RED);
//                        vertex.setTranslateX(currentPoint.getX());
//                        vertex.setTranslateY(currentPoint.getY());
//                        //getGameScene().addUINode(vertex);
//                    }
//                }
//
//                currentPoint = currentPoint.add(vector);
//            }
//
//            if (startPoint != null && endPoint != null) {
//                Point2D mid = startPoint.midpoint(endPoint);
//
//                midPoints.add(mid);
//
//                Circle vertex = new Circle(5, Color.BLUE);
//                vertex.setTranslateX(mid.getX());
//                vertex.setTranslateY(mid.getY());
//                getGameScene().addUINode(vertex);
//            }
//        }
//
//        Polygon2D poly = new SimplePolygon2D(midPoints.stream().map(this::p).collect(Collectors.toList()));
    }

    private math.geom2d.Point2D p(Point2D point) {
        return new math.geom2d.Point2D(point.getX(), point.getY());
    }

    private static class Region {
        private List<Polygon2D> containing;
        private List<Polygon2D> exluding;
        private String name;

        public Region(String name, List<Polygon2D> containing, List<Polygon2D> exluding) {
            this.name = name;
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

        Polygon2D d;


        return new SimplePolygon2D(points);
    }

    private Circle pointView(double x, double y) {
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

        double inAngle = (vertices - 2) * Math.PI / vertices;

        Point2D vector = new Point2D(1, 0).multiply(side);

        List<Point2D> pointsList = new ArrayList<>();

        double R = side / (2 * Math.sin(Math.PI / vertices));
        pointsList.add(new Point2D(centreX - R, centreY));

        Point2D p0 = pointsList.get(0);

        vector = rotate(vector, -inAngle / 2);
        Point2D p1 = p0.add(vector);

        pointsList.add(p1);

        Point2D prevPoint = p1;

        for (int i = 2; i < vertices; i++) {

            // the other bit of the rotation
            vector = rotate(vector, Math.PI - inAngle);

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
        //angleRadians = (Math.PI - angleRadians);


        System.out.println("ORIG: " + vector);
        System.out.println("Rotated: " + new Point2D(vector.getX() * Math.cos(angleRadians) - vector.getY() * Math.sin(angleRadians),
                vector.getY() * Math.cos(angleRadians) + vector.getX() * Math.sin(angleRadians)));

        //angleRadians = Math.PI - angleRadians;

        //angleRadians -= Math.toRadians(40);

        System.out.println(Math.toDegrees(angleRadians));

//        while (angleRadians > Math.PI) {
//            angleRadians -= Math.PI;
//        }
//
//        while (angleRadians < -Math.PI) {
//            angleRadians += Math.PI;
//        }

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

        for (int i = 2; i < numOfCorners; i++) {

            // translation vector calculation
            vec[0] = vec[0] * Math.cos(-angle) - vec[1] * Math.sin(-angle);
            vec[1] = vec[1] * Math.cos(-angle) + vec[0] * Math.sin(-angle);

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
