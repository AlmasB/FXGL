/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CrusherControl extends FSMControl {

    private FSMState PREPARING = new FSMState() {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            entity.translateY(-tpf * crushSpeed / 3);

            if (entity.getY() < origin.getY()) {
                entity.setY(origin.getY());
                setState(READY);
                crushTimer.capture();
            }
        }
    };

    private FSMState CRUSHING = new FSMState() {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            entity.translateY(tpf * crushSpeed);

            if (entity.getBottomY() > destination.getY()) {
                entity.setY(destination.getY() - entity.getHeight());
                setState(PREPARING);
            }
        }
    };

    private FSMState READY = new FSMState() {
        @Override
        public void onUpdate(Entity entity, double tpf) {
            t += tpf;

            timerText.setText(String.valueOf((int)t));

            if (crushTimer.elapsed(Duration.seconds(3))) {
                setState(CRUSHING);

                t = 0;
                timerText.setText("!!!");
            }
        }
    };

    private double crushSpeed = 660;

    private Point2D origin;
    private Point2D destination = new Point2D(120, 70*7);
    private LocalTimer crushTimer = FXGL.newLocalTimer();

    private Text timerText;
    private double t = 0;

    @Override
    public void onAdded(Entity entity) {
        timerText = FXGL.getUIFactory().newText("", Color.AQUA, 28);
        timerText.setTranslateX(25);
        timerText.setTranslateY(35);

        entity.getView().addNode(timerText);

        entity.setOnActive(() -> origin = entity.getPosition());

        setState(READY);
    }
}
