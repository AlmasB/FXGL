/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
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
    protected void initGame() {
        timer = FXGL.newOfflineTimer("MOTDCheck");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
