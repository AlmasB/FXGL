package com.almasb.fxgl.entity;

/**
 * A common interface for specifying behavior for an entity / entities
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
@FunctionalInterface
public interface Control {

    /**
     * Called as part of the main loop, a single update tick
     *
     * @param entity
     *              the entity to which this control was added
     * @param now
     *              current time in nanoseconds
     */
    public void onUpdate(Entity entity, long now);
}
