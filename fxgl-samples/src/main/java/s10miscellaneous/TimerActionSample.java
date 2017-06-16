/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
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
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("TimerActionSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Pause") {
            @Override
            protected void onActionBegin() {
                if (timerAction.isPaused())
                    timerAction.resume();
                else
                    timerAction.pause();
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Expire") {
            @Override
            protected void onActionBegin() {
                timerAction.expire();
            }
        }, KeyCode.E);
    }

    private TimerAction timerAction;

    @Override
    protected void initGame() {
        timerAction = getMasterTimer().runAtInterval(() -> System.out.println("Tick: " + getTick()), Duration.seconds(0.5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
