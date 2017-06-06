/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.spaceinvaders.Config;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BonusControl extends AbstractControl {
    @Override
    public void onUpdate(Entity entity, double tpf) {
        getEntity().getComponentUnsafe(PositionComponent.class).translateY(tpf * Config.BONUS_MOVE_SPEED);
    }
}
