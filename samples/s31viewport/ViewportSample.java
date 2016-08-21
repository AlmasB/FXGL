/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package s31viewport;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.settings.GameSettings;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Shows how to use viewport.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ViewportSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ViewportSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        Entities.builder()
                .at(0, 0)
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(800, 0)
                .viewFromNode(new Rectangle(40, 40, Color.RED))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(600, 560)
                .viewFromNode(new Rectangle(40, 40, Color.GREEN))
                .buildAndAttach(getGameWorld());

        cinematicViewport();
    }

    private void cinematicViewport() {
        // 1. get viewport
        Viewport viewport = getGameScene().getViewport();
        viewport.setX(-150);

        // 2. define "waypoints"
        Point2D[] points = {
                new Point2D(800, 0),
                new Point2D(300, 300),
                new Point2D(0, 0)
        };

        Timeline timeline = new Timeline();

        int i = 0;
        for (Point2D p : points) {
            // bind viewport property values to pre-defined points
            KeyValue kv = new KeyValue(viewport.xProperty(), p.getX());
            KeyValue kv2 = new KeyValue(viewport.yProperty(), p.getY());

            // create frame
            KeyFrame frame = new KeyFrame(Duration.seconds(3 * ++i), kv, kv2);
            timeline.getKeyFrames().add(frame);
        }

        // 3. animate
        timeline.play();
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
