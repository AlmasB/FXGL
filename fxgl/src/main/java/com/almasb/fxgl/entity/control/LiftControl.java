/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * Lift control.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LiftControl extends Control {

    private LocalTimer timer;
    private Duration duration;
    private double distance;
    private boolean goingUp;
    private double speed;

    /**
     * Constructs lift control (moving vertically).
     *
     * @param duration duration going (one way)
     * @param distance the distance entity travels (one way)
     * @param goingUp if true, entity starts going up, otherwise down
     */
    public LiftControl(Duration duration, double distance, boolean goingUp) {
        this.duration = duration;
        this.distance = distance;
        this.goingUp = goingUp;
    }

    @Override
    public void onAdded(Entity entity) {
        timer = FXGL.newLocalTimer();
        speed = distance / duration.toSeconds();
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (timer.elapsed(duration)) {
            goingUp = !goingUp;
            timer.capture();
        }

        entity.translateY(goingUp ? -speed * tpf : speed * tpf);
    }
}
