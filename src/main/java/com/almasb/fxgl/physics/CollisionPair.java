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

import com.almasb.ents.Entity;
import com.almasb.fxgl.entity.component.TypeComponent;
import com.almasb.gameutils.pool.Poolable;

final class CollisionPair extends Pair<Entity> implements Poolable {

    private CollisionHandler handler;
    private boolean valid = false;

    CollisionPair() {
        super(null, null);
    }

    void init(Entity a, Entity b, CollisionHandler handler) {
        this.handler = handler;
        valid = true;

        // we check the order here so that we won't have to do that every time
        // when triggering collision between A and B
        // this ensures that client gets back entities in the same order
        // he registered the handler with
        if (a.getComponentUnsafe(TypeComponent.class).getValue().equals(handler.getA())) {
            setA(a);
            setB(b);
        } else {
            setA(b);
            setB(a);
        }
    }

    /**
     * @return collision handler for this pair
     */
    CollisionHandler getHandler() {
        return handler;
    }

    /**
     * @return true if collision is still occurring
     */
    boolean isValid() {
        return valid && getA().isActive() && getB().isActive();
    }

    void setValid(boolean valid) {
        this.valid = valid;
    }

    void collisionBegin() {
        handler.onCollisionBegin(getA(), getB());
    }

    void collision() {
        handler.onCollision(getA(), getB());
    }

    void collisionEnd() {
        handler.onCollisionEnd(getA(), getB());
    }

    @Override
    public void reset() {
        handler = null;
        valid = false;
        setA(null);
        setB(null);
    }
}
