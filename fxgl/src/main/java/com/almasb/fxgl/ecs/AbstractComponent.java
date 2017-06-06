/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

/**
 * Base class for components.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class AbstractComponent implements Component {

    private Entity entity;

    /**
     * @return entity to which the component is attached, or null if component is not attached
     */
    public Entity getEntity() {
        return entity;
    }

    void setEntity(Entity entity) {
        if (entity == null && this.entity == null)
            throw new IllegalStateException("Attempt to clear entity but component is not attached to an entity");

        if (entity != null && this.entity != null)
            throw new IllegalStateException("Attempt to set entity but component is already attached to an entity");

        this.entity = entity;
    }

    @Override
    public void onAdded(Entity entity) {

    }

    @Override
    public void onRemoved(Entity entity) {

    }
}
