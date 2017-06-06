/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.pool.Poolable;

/**
 * Data structure for holding info about collision result.
 * {@link CollisionResult#hasCollided()} will return true
 * if a collision has occurred, false otherwise.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class CollisionResult implements Poolable {

    /**
     * Constant for reporting no collision.
     * {@link CollisionResult#getBoxA()} and {@link CollisionResult#getBoxB()}
     * return null and {@link CollisionResult#hasCollided()} returns false.
     */
    public static final CollisionResult NO_COLLISION = new CollisionResult(false);

    /**
     * Constant for reporting collision.
     * Used when caller doesn't care about hit boxes.
     * {@link CollisionResult#getBoxA()} and {@link CollisionResult#getBoxB()}
     * return null and {@link CollisionResult#hasCollided()} returns true.
     */
    public static final CollisionResult COLLISION = new CollisionResult(true);

    private HitBox boxA;
    private HitBox boxB;

    private boolean collided;

    private CollisionResult(boolean collided) {
        this.collided = collided;
    }

    public CollisionResult() {}

    /**
     * Init CollisionResult with positive result, i.e.
     * {@link CollisionResult#hasCollided()} returns true.
     *
     * @param boxA hit box of first entity
     * @param boxB hit box of second entity
     */
    public void init(HitBox boxA, HitBox boxB) {
        this.boxA = boxA;
        this.boxB = boxB;
        collided = true;
    }

    /**
     * @return hit box of first entity
     */
    public HitBox getBoxA() {
        return boxA;
    }

    /**
     * @return hit box of second entity
     */
    public HitBox getBoxB() {
        return boxB;
    }

    /**
     * @return true if collision occurred, false otherwise
     */
    public boolean hasCollided() {
        return collided;
    }

    @Override
    public void reset() {
        // someone called "put" to pooler on NO_COLLISION object
        // so guard it
        if (!collided) {
            return;
        }

        boxA = null;
        boxB = null;
    }
}
