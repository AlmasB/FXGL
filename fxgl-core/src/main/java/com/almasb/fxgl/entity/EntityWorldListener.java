/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

/**
 * Listener for world events.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface EntityWorldListener {

    /**
     * Called after entity was added to the world.
     *
     * @param entity the entity
     */
    void onEntityAdded(Entity entity);

    /**
     * Called after entity was removed from the world
     * but before entity has been cleaned.
     * This allows other parties to free resources before
     * entity clean.
     *
     * @param entity the entity
     */
    void onEntityRemoved(Entity entity);
}
