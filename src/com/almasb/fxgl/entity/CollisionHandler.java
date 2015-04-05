package com.almasb.fxgl.entity;

/**
 * Handler for a collision that occurred between two entities
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
@FunctionalInterface
public interface CollisionHandler {

    /**
     * Called when there has been a collision between entity A and entity B
     *
     * @param a
     * @param b
     */
    public void onCollision(Entity a, Entity b);
}
