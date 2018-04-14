/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * Moves the entity up and down.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LiftComponent extends Component {

    private LocalTimer timer;
    private Duration duration;
    private double distance;
    private boolean goingUp;
    private double speed;

    /**
     * Constructs lift component (moving vertically).
     *
     * @param duration duration going (one way)
     * @param distance the distance entity travels (one way)
     * @param goingUp if true, entity starts going up, otherwise down
     */
    public LiftComponent(Duration duration, double distance, boolean goingUp) {
        this.duration = duration;
        this.distance = distance;
        this.goingUp = goingUp;
    }

    @Override
    public void onAdded() {
        timer = FXGL.newLocalTimer();
        speed = distance / duration.toSeconds();
    }

    @Override
    public void onUpdate(double tpf) {
        if (timer.elapsed(duration)) {
            goingUp = !goingUp;
            timer.capture();
        }

        entity.translateY(goingUp ? -speed * tpf : speed * tpf);
    }
}
