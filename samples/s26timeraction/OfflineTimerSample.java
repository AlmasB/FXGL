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

package s26timeraction;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Shows how to use offline timers.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class OfflineTimerSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("OfflineTimerSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private LocalTimer timer;

    @Override
    protected void initInput() {

        // when you run this for the first time and press F
        // it will instantly print that 1 minute has passed
        // now close the application and re run immediately
        // this time when you press F it will not show the message
        // you can print again only after 1 minute has passed (in real time)
        getInput().addAction(new UserAction("Start") {
            @Override
            protected void onActionBegin() {
                if (timer.elapsed(Duration.minutes(1))) {
                    System.out.println("Elapsed 1 minute");
                    timer.capture();
                }
            }
        }, KeyCode.F);
    }

    @Override
    protected void initAssets() {}

    @Override
    protected void initGame() {
        timer = FXGL.newOfflineTimer("MOTDCheck");
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
