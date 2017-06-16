/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs.action;

import com.almasb.fxgl.ecs.Control;
import com.almasb.fxgl.ecs.Entity;

/**
 * A single entity action.
 * Unlike a {@link Control} the action is finished after its execution,
 * a control is active during the entity lifetime (or until removed).
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Action<T extends Entity> {

    private T entity;

    /**
     * @return entity to which the action is attached, or null if action is not attached
     */
    public final T getEntity() {
        return entity;
    }

    void setEntity(T entity) {
        if (entity == null && this.entity == null)
            throw new IllegalStateException("Attempt to clear entity but action is not attached to an entity");

        if (entity != null && this.entity != null)
            throw new IllegalStateException("Attempt to set entity but action is already attached to an entity");

        if (entity != null)
            onAdded(entity);
        else
            onRemoved(this.entity);

        this.entity = entity;
    }

    protected void onAdded(T entity) {
        // no-op
    }

    protected void onRemoved(T entity) {
        // no-op
    }

    /**
     * @return true if this action is complete and does not require further execution
     */
    public abstract boolean isComplete();

    /**
     * Called on entity world update tick.
     *
     * @param entity the entity to which this action is attached
     * @param tpf time per frame
     */
    protected abstract void onUpdate(T entity, double tpf);
}
