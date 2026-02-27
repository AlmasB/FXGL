/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

import com.almasb.fxgl.entity.Entity;

/**
 * A single entity action.
 * The action is finished after its execution.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class Action {

    /**
     * The entity performing this action.
     */
    protected Entity entity;

    private boolean isCompleted = false;

    private boolean isCancelled = false;

    /**
     * @return entity to which the action is attached, or null if action is not attached
     */
    public final Entity getEntity() {
        return entity;
    }

    public final void setEntity(Entity entity) {
        if (this.entity != null && this.entity != entity)
            throw new IllegalArgumentException("Attempting to set a different entity to action");

        this.entity = entity;
    }

    /**
     * @return true if this action is complete and does not require further execution
     */
    public final boolean isComplete() {
        return isCompleted;
    }

    public final void setComplete() {
        isCompleted = true;
    }

    public final boolean isCancelled() {
        return isCancelled;
    }

    public final void cancel() {
        if (isCancelled)
            return;

        isCancelled = true;
        onCancelled();
    }

    /**
     * Called after this action was placed in a queue for execution.
     */
    protected void onQueued() { }

    /**
     * Called after this action was started.
     * This is called at least 1 frame after onQueued was called.
     */
    protected void onStarted() { }

    /**
     * Called on entity world update tick.
     *
     * @param tpf time per frame
     */
    protected abstract void onUpdate(double tpf);

    /**
     * Called after this action completed.
     */
    protected void onCompleted() { }

    /**
     * Called after this action was cancelled.
     */
    protected void onCancelled() { }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
