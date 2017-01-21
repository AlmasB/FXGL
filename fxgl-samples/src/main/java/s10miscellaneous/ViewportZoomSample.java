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
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use viewport to fit entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ViewportZoomSample extends GameApplication {

    private GameEntity e1, e2, e3;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ViewportZoomSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Move Left") {
            @Override
            protected void onAction() {
                e1.translateX(-5);
            }
        }, KeyCode.A);

        input.addAction(new UserAction("Move Right") {
            @Override
            protected void onAction() {
                e1.translateX(5);
            }
        }, KeyCode.D);

        input.addAction(new UserAction("Move Up") {
            @Override
            protected void onAction() {
                e1.translateY(-5);
            }
        }, KeyCode.W);

        input.addAction(new UserAction("Move Down") {
            @Override
            protected void onAction() {
                e1.translateY(5);
            }
        }, KeyCode.S);

        input.addAction(new UserAction("Move Left2") {
            @Override
            protected void onAction() {
                e2.translateX(-5);
            }
        }, KeyCode.LEFT);

        input.addAction(new UserAction("Move Right2") {
            @Override
            protected void onAction() {
                e2.translateX(5);
            }
        }, KeyCode.RIGHT);

        input.addAction(new UserAction("Move Up2") {
            @Override
            protected void onAction() {
                e2.translateY(-5);
            }
        }, KeyCode.UP);

        input.addAction(new UserAction("Move Down2") {
            @Override
            protected void onAction() {
                e2.translateY(5);
            }
        }, KeyCode.DOWN);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        e1 = Entities.builder()
                .at(0, 0)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach(getGameWorld());

        e2 = Entities.builder()
                .at(800, 0)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .buildAndAttach(getGameWorld());

        e3 = Entities.builder()
                .at(600, 560)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.GREEN))
                .buildAndAttach(getGameWorld());

        // 1. bind viewport so it can fit those entities at any time
        getGameScene().getViewport().bindToFit(40, 100, e1, e2, e3);
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
