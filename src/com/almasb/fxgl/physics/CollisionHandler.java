/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;

/**
 * Handler for a collision that occurred between two entities
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public abstract class CollisionHandler extends Pair<EntityType> {

    public CollisionHandler(EntityType a, EntityType b) {
        super(a, b);
    }

    /**
     * Called when entities A and B have just collided and weren't colliding in the last tick.
     *
     * @param a
     * @param b
     */
    public abstract void onCollisionBegin(Entity a, Entity b);

    /**
     * Called if entities A and B are currently colliding.
     *
     * This is called one tick after {@link #onCollisionBegin(Entity, Entity)}
     * if the entities are still colliding
     *
     * @param a
     * @param b
     */
    public abstract void onCollision(Entity a, Entity b);

    /**
     * Called when entities A and B have just stopped colliding and were colliding in the last tick.
     *
     * @param a
     * @param b
     */
    public abstract void onCollisionEnd(Entity a, Entity b);
}
