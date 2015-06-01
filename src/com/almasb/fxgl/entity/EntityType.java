package com.almasb.fxgl.entity;

/**
 * A type of entity. For extra safety use enums.
 *
 * Example:
 * <pre>
 *     private enum Type implements EntityType {
 *         PLAYER, ENEMY, BULLET, POWERUP
 *     }
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public interface EntityType {
    default public String getUniqueType() {
        return getClass().getCanonicalName() + "." + toString();
    }
}
