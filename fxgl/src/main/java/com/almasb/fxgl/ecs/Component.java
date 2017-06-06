/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs;

/**
 * A component in the ECS pattern.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public interface Component {

    /**
     * Called when component is added to an entity.
     *
     * @param entity the entity to which this component was added
     */
    void onAdded(Entity entity);

    /**
     * Called when component is removed from an entity.
     *
     * @param entity the entity from which this component is removed
     */
    void onRemoved(Entity entity);
}
