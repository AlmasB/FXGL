/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Shows how to use timer actions.
 * Press F to pause / resume the action.
 * Press E to make the action expire.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TimerActionSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        FXGL.onKeyDown(KeyCode.F, "Pause Timer", () -> {
            if (timerAction.isPaused())
                timerAction.resume();
            else
                timerAction.pause();
        });

        FXGL.onKeyDown(KeyCode.E, "Expire Timer", () -> {
            timerAction.expire();
        });
    }

    private TimerAction timerAction;

    @Override
    protected void initGame() {
        timerAction = FXGL.getGameTimer().runAtInterval(() -> System.out.println("Now: " + FXGL.getGameTimer().getNow()), Duration.seconds(0.5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
