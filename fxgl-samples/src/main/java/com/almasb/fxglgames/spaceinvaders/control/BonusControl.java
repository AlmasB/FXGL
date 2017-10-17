/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.spaceinvaders.Config;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BonusControl extends Control {
    @Override
    public void onUpdate(Entity entity, double tpf) {
        getEntity().getComponent(PositionComponent.class).translateY(tpf * Config.BONUS_MOVE_SPEED);
    }
}
