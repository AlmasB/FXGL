/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

/**
 * Base class for controls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class AbstractControl implements Control {

    private Entity entity;

    /**
     * @return entity to which the control is attached, or null if control is not attached
     */
    public Entity getEntity() {
        return entity;
    }

    void setEntity(Entity entity) {
        if (entity == null && this.entity == null)
            throw new IllegalStateException("Attempt to clear entity but control is not attached to an entity");

        if (entity != null && this.entity != null)
            throw new IllegalStateException("Attempt to set entity but control is already attached to an entity");

        this.entity = entity;
    }

    @Override
    public void onAdded(Entity entity) {

    }

    @Override
    public void onRemoved(Entity entity) {

    }

    private boolean isPaused = false;

    @Override
    public final boolean isPaused() {
        return isPaused;
    }

    @Override
    public final void pause() {
        isPaused = true;
    }

    @Override
    public final void resume() {
        isPaused = false;
    }
}
