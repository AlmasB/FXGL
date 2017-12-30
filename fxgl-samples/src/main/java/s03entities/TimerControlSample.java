/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * This samples shows how to create timer based controls for entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TimerControlSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("TimerControlSample");
        settings.setVersion("0.2");





    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new LiftControl())
                .buildAndAttach(getGameWorld());
    }

    private class LiftControl extends Control {

        private LocalTimer timer = FXGL.newLocalTimer();
        private boolean goingUp = false;

        @Override
        public void onUpdate(Entity entity, double tpf) {
            // 1. check if timer elapsed
            if (timer.elapsed(Duration.seconds(2))) {
                // 2. perform logic
                goingUp = !goingUp;

                // 3. capture time so that timer is reset
                timer.capture();
            }

            double speed = tpf * 60;

            entity.translateY(goingUp ? -speed : speed);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
