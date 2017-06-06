/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

/**
 * Defines behavior of an entity. Unlike the "System" in the ECS model,
 * control is attached directly to an entity and is stateful (i.e. it
 * knows about entity to which it is attached).
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface Control {

    /**
     * Called when this control is added to entity.
     * This is called before any control related listeners are notified.
     * This allows the control to initiliaze properly.
     *
     * @param entity the entity to which this control was added
     */
    void onAdded(Entity entity);

    /**
     * Called on entity world update tick.
     *
     * @param entity the entity to which this control is attached
     * @param tpf time per frame
     */
    void onUpdate(Entity entity, double tpf);

    /**
     * Called when this control is removed from entity.
     * This is called after any control related listeners are notified.
     * This allows the control to clean up properly.
     *
     * @param entity the entity from which the control was removed
     */
    void onRemoved(Entity entity);

    /**
     * @return if execution of this control is paused
     */
    boolean isPaused();

    /**
     * Pauses execution of this control.
     */
    void pause();

    /**
     * Resumes execution of this control.
     */
    void resume();
}
