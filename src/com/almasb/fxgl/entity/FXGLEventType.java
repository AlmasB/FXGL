package com.almasb.fxgl.entity;

/**
 * A type of event. For extra safety use enums.
 *
 * Example:
 * <pre>
 *     private enum Event implements FXGLEventType {
 *         ENEMY_SPAWN, ENEMY_DEATH, DOOR_OPEN, DOOR_CLOSE, BOSS_SPAWN
 *     }
 * </pre>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public interface FXGLEventType {
    default public String getUniqueType() {
        return getClass().getCanonicalName() + "." + toString();
    }
}
