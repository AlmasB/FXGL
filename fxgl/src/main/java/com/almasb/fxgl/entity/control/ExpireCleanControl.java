/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;

/**
 * Removes an entity from the world after a certain duration.
 * Useful for special effects or temporary entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ExpireCleanControl extends AbstractControl {

    private Duration expire;

    /**
     * The expire duration timer starts when the entity is attached to the world,
     * so it does not start immediately when this control is created.
     *
     * @param expire the duration after entity is removed from the world
     */
    public ExpireCleanControl(Duration expire) {
        this.expire = expire;
    }

    private TimerAction timerAction;

    @Override
    public void onAdded(Entity entity) {
        entity.activeProperty().addListener((observable, oldValue, isActive) -> {
            if (isActive) {
                timerAction = FXGL.getMasterTimer().runOnceAfter(entity::removeFromWorld, expire);
            } else {
                timerAction.expire();
            }
        });
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (timerAction == null) {
            timerAction = FXGL.getMasterTimer().runOnceAfter(entity::removeFromWorld, expire);
        }
    }
}
