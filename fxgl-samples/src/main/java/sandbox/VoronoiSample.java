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

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.settings.GameSettings;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import org.jbox2d.particle.VoronoiDiagram;

import java.util.*;
import java.util.stream.IntStream;

import static com.almasb.fxgl.core.math.FXGLMath.degRad;
import static com.almasb.fxgl.core.math.FXGLMath.random;

/**
 * This is an example of a minimalistic FXGL game application.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class VoronoiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("VoronoiSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
    }

    private List<Point2D> seeds = new ArrayList<>();
    private Map<Point2D, Color> colors = new HashMap<>();

    @Override
    protected void initGame() {

        Circle c1 = new Circle(200, 200, 200, Color.TRANSPARENT);
        c1.setStroke(Color.BLUE);
        c1.setTranslateX(100);
        c1.setTranslateY(100);

        Circle c2 = new Circle(200, 200, 200, Color.TRANSPARENT);
        c2.setStroke(Color.DARKCYAN);
        c2.setTranslateX(300);
        c2.setTranslateY(100);

        Circle c3 = new Circle(100, 100, 100, Color.TRANSPARENT);
        c3.setStroke(Color.GREEN);
        c3.setTranslateX(450);
        c3.setTranslateY(250);

        Circle c4 = new Circle(5, 5, 5, Color.BLACK);
        c4.setTranslateX(340);
        c4.setTranslateY(290);

        Circle c5 = new Circle(5, 5, 5, Color.BLACK);
        c5.setTranslateX(610);
        c5.setTranslateY(320);

        getGameScene().addUINodes(c1, c2, c3,
                c4, c5);

        /////////////////////////////////////////////////////////////// REGIONS

        Shape region1 = Shape.subtract(c2, c1);
        region1 = Shape.subtract(region1, c3);
        region1.setFill(Color.color(0.1, 0.1, 0.9, 0.5));

        getGameScene().addUINode(region1);

        Bounds bounds = region1.getLayoutBounds();
        Point2D center = new Point2D((bounds.getMaxX() - bounds.getMinX()) / 2, (bounds.getMaxY() - bounds.getMinY()) / 2);

        // offset
        center = center.add(350, 100);


//        c5.setTranslateX(center.getX());
//        c5.setTranslateY(center.getY());

        // degrees
        for (int angle = 0; angle < 360; angle++) {

            // TODO: add vector from point to FXGLMath
            Point2D vector = new Point2D(FXGLMath.cosDeg(angle), FXGLMath.sinDeg(angle)).normalize();

            Point2D currentPoint = center.add(vector);

            Point2D startPoint = null;
            Point2D endPoint = null;

            while (bounds.contains(currentPoint)) {

                if (region1.contains(currentPoint)) {
                    if (startPoint == null) {
                        startPoint = new Point2D(currentPoint.getX(), currentPoint.getY());

                        Circle vertex = new Circle(5, Color.RED);
                        vertex.setTranslateX(currentPoint.getX());
                        vertex.setTranslateY(currentPoint.getY());
                        //getGameScene().addUINode(vertex);
                    }
                } else {
                    if (startPoint != null && endPoint == null) {
                        endPoint = new Point2D(currentPoint.getX(), currentPoint.getY());

                        Circle vertex = new Circle(5, Color.RED);
                        vertex.setTranslateX(currentPoint.getX());
                        vertex.setTranslateY(currentPoint.getY());
                        //getGameScene().addUINode(vertex);
                    }
                }

                currentPoint = currentPoint.add(vector);
            }

            if (startPoint != null && endPoint != null) {
                Point2D mid = startPoint.midpoint(endPoint);

                Circle vertex = new Circle(5, Color.RED);
                vertex.setTranslateX(mid.getX());
                vertex.setTranslateY(mid.getY());
                getGameScene().addUINode(vertex);
            }
        }

//        for (int y = (int) bounds.getMinY(); y < bounds.getMaxY(); y++) {
//            for (int x = (int) bounds.getMinX(); x < bounds.getMaxX(); x++) {
//                Point2D p = new Point2D(x, y);
//
//                if (region1.contains(p)) {
//                    Circle vertex = new Circle(5, Color.RED);
//                    vertex.setTranslateX(x);
//                    vertex.setTranslateY(y);
//                    getGameScene().addUINode(vertex);
//                }
//            }
//        }


    }

    private void voronoi() {
        //        IntStream.range(0, 20)
//                .mapToObj(i -> new Point2D(random(0, (int) getWidth()), random(0, (int) getHeight())))
//                .forEach(seeds::add);
//

        Polygon polygon = new Polygon(
                100, 100,
                250, 100,
                250, 335,
                335, 335,
                335, 115,
                470, 115,
                470, 190,
                650, 190,
                650, 490,
                150, 490
        );

        for (int i = 0; i < polygon.getPoints().size(); i += 2) {
            double px = polygon.getPoints().get(i);
            double py = polygon.getPoints().get(i + 1);

            seeds.add(new Point2D(px, py));
        }

        polygon.setFill(Color.TRANSPARENT);
        polygon.setStroke(Color.BLACK);

        getGameScene().addUINode(polygon);

        seeds.forEach(p -> {
            colors.put(p, Color.color(random(), random(), random()));
        });

        GraphicsContext g = getGameScene().getGraphicsContext();

        for (int y = 0; y < 600; y++) {
            for (int x = 0; x < 800; x++) {
                if (!polygon.contains(x, y))
                    continue;

                Color color = colors.get(getClosest(new Point2D(x, y)));

                g.setFill(color);
                g.fillOval(x, y, 1, 1);
            }
        }

        seeds.forEach(s -> {
            g.setFill(Color.BLACK);
            g.fillOval(s.getX(), s.getY(), 4, 4);
        });

        Platform.runLater(() -> {
            Image image = g.getCanvas().snapshot(null, null);

            getGameScene().addUINode(new ImageView(image));
        });
    }

    private Point2D getClosest(Point2D point) {
        return seeds.stream().sorted(Comparator.comparingInt(p -> (int) p.distance(point))).findFirst().get();
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
