/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics;

import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;

import java.util.Optional;

/**
 * Result of a raycast.
 * Contains optional entity and point
 * which represent first non ignored physics entity
 * and its point of collision in the ray's path.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class RaycastResult {

    public static final RaycastResult NONE = new RaycastResult(null, null);

    private Entity entity;
    private Point2D point;

    RaycastResult(Entity entity, Point2D point) {
        this.entity = entity;
        this.point = point;
    }

    /**
     * @return the first physics entity that collided with the ray
     * whose raycastIgnored flag is false
     */
    public Optional<Entity> getEntity() {
        return Optional.ofNullable(entity);
    }

    /**
     * @return the collision point in world coordinates
     */
    public Optional<Point2D> getPoint() {
        return Optional.ofNullable(point);
    }
}
