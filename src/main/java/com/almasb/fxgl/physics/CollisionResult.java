/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.physics;

import com.almasb.gameutils.pool.Poolable;

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
