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

package s10miscellaneous;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.BezierSpline;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

/**
 * Shows how to use viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ViewportSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(600);
        settings.setTitle("ViewportSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Move Viewport Up") {
            @Override
            protected void onAction() {
                getGameScene().getViewport().setY(getGameScene().getViewport().getY() - 5);
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Move Viewport Down") {
            @Override
            protected void onAction() {
                getGameScene().getViewport().setY(getGameScene().getViewport().getY() + 5);
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Print Coords") {
            @Override
            protected void onActionBegin() {
                //System.out.println(getInput().getMousePositionWorld());
                //cinematicViewport();
                cinematicViewportBezier();
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        TextLevelParser parser = new TextLevelParser('0', 40, 40);
        parser.addEntityProducer('1', data -> Entities.builder()
                .from(data)
                .viewFromNode(new Rectangle(40, 40, Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random())))
                .build()
        );

        getGameWorld().setLevel(parser.parse("level_viewport.txt"));
    }

    @Override
    protected void initUI() {
        Circle circle = new Circle(15, Color.RED);
        circle.setTranslateX(getWidth() / 2);
        circle.setTranslateY(getHeight() / 2);

        getGameScene().addUINode(circle);
    }

    private void cinematicViewport() {
        // 1. get viewport
        Viewport viewport = getGameScene().getViewport();
        viewport.setX(-getWidth() / 2);
        viewport.setY(-getHeight() / 2);

        // 2. define "waypoints"
        Point2D[] points = {
                new Point2D(98, 80),
                new Point2D(1520, 90),
                new Point2D(1470, 272),
                new Point2D(171, 293),
                new Point2D(154, 485),
                new Point2D(1500, 483),
                new Point2D(1474, 683),
                new Point2D(144, 698),
                new Point2D(161, 892),
                new Point2D(1475, 888),
                new Point2D(1477, 1064),
                new Point2D(108, 1066)
        };

        Timeline timeline = new Timeline();

        int i = 0;
        for (Point2D p : points) {
            // bind viewport property values to pre-defined points
            KeyValue kv = new KeyValue(viewport.xProperty(), p.getX() - getWidth() / 2);
            KeyValue kv2 = new KeyValue(viewport.yProperty(), p.getY() - getHeight() / 2);

            // create frame
            KeyFrame frame = new KeyFrame(Duration.seconds(2 * ++i), kv, kv2);
            timeline.getKeyFrames().add(frame);
        }

        // 3. animate
        timeline.play();
    }

    private void cinematicViewportBezier() {
        // 1. get viewport
        Viewport viewport = getGameScene().getViewport();
        viewport.setX(-getWidth() / 2);
        viewport.setY(-getHeight() / 2);

        // 2. define "waypoints"
        Vec2[] points = {
                new Vec2(98, 80),
                new Vec2(1520, 90),
                new Vec2(1470, 272),
                new Vec2(171, 293),
                new Vec2(154, 485),
                new Vec2(1500, 483),
                new Vec2(1474, 683),
                new Vec2(144, 698),
                new Vec2(161, 892),
                new Vec2(1475, 888),
                new Vec2(1477, 1064),
                new Vec2(108, 1066)
        };

        BezierSpline spline = FXGLMath.closedBezierSpline(points);

        Path path = new Path();
        path.getElements().add(new MoveTo(98, 80));

        for (BezierSpline.BezierCurve c : spline.getCurves()) {
            path.getElements().add(new CubicCurveTo(
                    c.getControl1().x, c.getControl1().y,
                    c.getControl2().x, c.getControl2().y,
                    c.getEnd().x, c.getEnd().y
            ));
        }

        // if open bezier is needed
        path.getElements().remove(path.getElements().size()-1);

        GameEntity camera = Entities.builder()
                .build();

        viewport.xProperty().bind(camera.getPositionComponent().xProperty().subtract(getWidth() / 2));
        viewport.yProperty().bind(camera.getPositionComponent().yProperty().subtract(getHeight() / 2));

        Entities.animationBuilder()
                .duration(Duration.seconds(24))
                .translate(camera)
                //.alongPath(path)
                .buildAndPlay();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
