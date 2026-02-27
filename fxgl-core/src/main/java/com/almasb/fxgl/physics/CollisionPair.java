/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.pool.Poolable;
import com.almasb.fxgl.entity.Entity;

final class CollisionPair extends Pair<Entity> implements Poolable {

    private CollisionHandler handler;

    CollisionPair() {
        super(null, null);
    }

    void init(Entity a, Entity b, CollisionHandler handler) {
        this.handler = handler;

        // we check the order here so that we won't have to do that every time
        // when triggering collision between A and B
        // this ensures that client gets back entities in the same order
        // they registered the handler with

        if (a.isType(handler.getA())) {
            setA(a);
            setB(b);
        } else {
            setA(b);
            setB(a);
        }
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
        setA(null);
        setB(null);
    }
}
