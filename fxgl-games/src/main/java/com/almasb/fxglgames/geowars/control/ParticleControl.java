/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.control.ProjectileControl;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleControl extends AbstractControl {
    @Override
    public void onUpdate(Entity entity, double tpf) {
        ProjectileControl control = entity.getControlUnsafe(ProjectileControl.class);
        control.setSpeed(control.getSpeed() * (1 - 3*tpf));
    }
}
