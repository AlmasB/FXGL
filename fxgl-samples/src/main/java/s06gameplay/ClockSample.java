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

package s06gameplay;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.rpg.InGameClock;
import com.almasb.fxgl.settings.GameSettings;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use an in-game clock.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ClockSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ClockSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(false);
    }

    private InGameClock clock;

    @Override
    protected void initGame() {
        clock = getGameplay().getClock();
        clock.start();

        Rectangle rect = new Rectangle(50, 50);
        rect.fillProperty().bind(
                Bindings.when(clock.dayProperty()).then(Color.YELLOW).otherwise(Color.RED)
        );

        getGameScene().addUINode(rect);

        clock.runAt(() -> {
            System.out.println("It's 02:30");
        }, 2, 30);

        clock.runAtHour(() -> {
            System.out.println("It's 06:00");
        }, 6);

        clock.gameHourProperty().addListener((obs, o, newValue) -> {
            System.out.println(newValue);
        });
    }

    @Override
    protected void initUI() {
        getUIFactory().centerText(clock.textView());

        getGameScene().addUINode(clock.textView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
