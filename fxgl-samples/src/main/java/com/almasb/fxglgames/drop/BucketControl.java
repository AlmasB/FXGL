/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.drop;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.component.PositionComponent;

/**
 * Controls the player bucket.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class BucketControl extends Control {

    private PositionComponent position;

    private double speed;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = tpf * 200;
    }

    public void left() {
        position.translateX(-speed);
    }

    public void right() {
        position.translateX(speed);
    }
}
