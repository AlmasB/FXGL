/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.net;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.gesturerecog.HandTrackingService;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class HandTrackingSample extends GameApplication {

    private GraphicsContext g;

    private boolean isDrawing = true;
    private double oldX = -1;
    private double oldY = -1;
    private Circle pointer;

    private List<Point2D> points;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.addEngineService(HandTrackingService.class);
    }

    @Override
    protected void initGame() {
        points = new ArrayList<>();

        getService(HandTrackingService.class).addInputHandler(hand -> {
            getExecutor().startAsyncFX(() -> {

                var dist = hand.getPoints().get(4).distance(hand.getPoints().get(8));

                isDrawing = dist < 0.06;

                var indexFingerTip = hand.getPoints().get(8);

                pointer.setTranslateX(Math.round((1 - indexFingerTip.getX()) * getAppWidth()));
                pointer.setTranslateY(Math.round(indexFingerTip.getY() * getAppHeight()));

                if (!isDrawing) {
                    oldX = -1;
                    oldY = -1;

                    if (points.size() >= 2) {
                        g.clearRect(0, 0, getAppWidth(), getAppHeight());

                        var v0 = points.get(0);
                        var v1 = points.getLast();

                        g.strokeLine(v0.getX(), v0.getY(), v1.getX(), v1.getY());
                    }

                    points.clear();
                    return;
                }

                if (oldX != -1 && oldY != -1) {
                    var v = new Vec2(pointer.getTranslateX(), pointer.getTranslateY())
                            .subLocal(oldX, oldY)
                            .mulLocal(0.85)
                            .addLocal(oldX, oldY);

                    g.strokeLine(oldX, oldY, v.x, v.y);
                    //g.strokeLine(oldX, oldY, pointer.getTranslateX(), pointer.getTranslateY());

                    points.add(v.toPoint2D());
                }

                oldX = pointer.getTranslateX();
                oldY = pointer.getTranslateY();
            });
        });

        getService(HandTrackingService.class).start();

        run(() -> {
            g.clearRect(0, 0, getAppWidth(), getAppHeight());
        }, Duration.seconds(50));
    }

    @Override
    protected void initUI() {
        pointer = new Circle(5, Color.RED);

        var canvas = new Canvas(getAppWidth(), getAppHeight());
        g = canvas.getGraphicsContext2D();
        g.setStroke(Color.RED);
        g.setLineWidth(6);

        addUINode(canvas);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
