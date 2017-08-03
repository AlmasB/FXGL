/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.drop;

import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.component.PositionComponent;

/**
 * Controls each droplet.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
public class DropletControl extends Control {

    private PositionComponent position;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        double speed = tpf * 200;

        position.translateY(speed);
    }
}
