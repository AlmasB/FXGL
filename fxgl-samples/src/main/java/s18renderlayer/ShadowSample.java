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

package s18renderlayer;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.scene.lighting.LightingSystem;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * API INCOMPLETE
 *
 * This example shows lighting and shadows.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ShadowSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(640);
        settings.setHeight(640);
        settings.setTitle("ShadowSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {}

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {
//        getGameScene().setLightingSystem(new LightingSystem());
//
//        getGameScene().getLightingSystem().addObstacle(new Point2D( 100.0, 150.0 ), new Point2D( 120.0, 50.0 ), new Point2D( 200.0, 80.0 ), new Point2D( 140.0, 210.0 ));
//        getGameScene().getLightingSystem().addObstacle(new Point2D( 100.0, 200.0 ), new Point2D( 120.0, 250.0 ), new Point2D( 60.0, 300.0 ));
//        getGameScene().getLightingSystem().addObstacle(new Point2D( 400.0, 350.0 ), new Point2D( 420.0, 250.0 ), new Point2D( 550.0, 280.0 ), new Point2D( 440.0, 410.0 ));


        Entities.builder()
                .at(200, 350)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(450, 550)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.YELLOW))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(100, 250)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.DARKRED))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(120, 350)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.GREEN))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(45, 250)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.DARKBLUE))
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .at(550, 260)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
