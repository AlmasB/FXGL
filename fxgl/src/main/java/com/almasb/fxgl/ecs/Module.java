/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Module {

    private Entity entity;

    public final Entity getEntity() {
        return entity;
    }

    final void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void onAdded(Entity entity) {

    }

    public void onRemoved(Entity entity) {

    }
}
