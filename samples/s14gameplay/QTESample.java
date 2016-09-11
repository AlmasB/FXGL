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
package s14gameplay;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.qte.QTE;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Shows how to use QTE (Quick Time Events).
 */
public class QTESample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("QTESample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {

        // when app runs, after 2 seconds a QTE event will occur
        getMasterTimer().runOnceAfter(() -> {

            // 1. get QTE service
            QTE qte = getQTE();

            // 2. start event with duration and keys to be pressed
            qte.start(yes -> {

                System.out.println("Successful? " + yes);

            }, Duration.seconds(3), KeyCode.F, KeyCode.X, KeyCode.G, KeyCode.L);

        }, Duration.seconds(2));
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    public void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
