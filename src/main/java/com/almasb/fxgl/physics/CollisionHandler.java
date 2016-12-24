/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

import com.almasb.ents.Entity;

/**
 * Handler for a collision that occurred between two entities.
 * Subclasses should override only those callbacks they are
 * interested in.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class CollisionHandler extends Pair<Object> {

    /**
     * The order of types determines the order of entities in callbacks.
     *
     * @param a entity type of the first entity
     * @param b entity type of the second entity
     */
    public CollisionHandler(Object a, Object b) {
        super(a, b);
    }

    /**
     * Called once per collision during the same tick when collision occurred.
     * Only the first hit box in the collision is passed.
     *
     * @param a first entity
     * @param b second entity
     * @param boxA hit box of first entity
     * @param boxB hit box of second entity
     */
    protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
        // no default implementation
    }

    /**
     * Called when entities A and B have just collided and weren't colliding in the last tick.
     *
     * @param a first entity
     * @param b second entity
     */
    protected void onCollisionBegin(Entity a, Entity b) {
        // no default implementation
    }

    /**
     * Called if entities A and B are currently colliding.
     *
     * @param a first entity
     * @param b second entity
     */
    protected void onCollision(Entity a, Entity b) {
        // no default implementation
    }

    /**
     * Called when entities A and B have just stopped colliding and were colliding in the last tick.
     *
     * @param a first entity
     * @param b second entity
     */
    protected void onCollisionEnd(Entity a, Entity b) {
        // no default implementation
    }

    /**
     * Returns a copy of the collision handler with different entity types.
     * This allows convenient use of the same handler code for
     * multiple entity types.
     *
     * @param a entity type A
     * @param b entity type B
     * @return copy of collision handler
     */
    public final CollisionHandler copyFor(Object a, Object b) {
        CollisionHandler copy = this;

        return new CollisionHandler(a, b) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                copy.onHitBoxTrigger(a, b, boxA, boxB);
            }

            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                copy.onCollisionBegin(a, b);
            }

            @Override
            protected void onCollision(Entity a, Entity b) {
                copy.onCollision(a, b);
            }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {
                copy.onCollisionEnd(a, b);
            }
        };
    }
}
