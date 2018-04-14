/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.pool.Poolable;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TypeComponent;

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
        // he registered the handler with
        if (a.getComponent(TypeComponent.class).getValue().equals(handler.getA())) {
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
