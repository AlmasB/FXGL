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

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;

import javafx.geometry.Point2D;

public final class PhysicsEntity extends Entity {

    /*package-private*/ FixtureDef fixtureDef = new FixtureDef();
    /*package-private*/ BodyDef bodyDef = new BodyDef();

    /*package-private*/ Body body;
    /*package-private*/ Fixture fixture;

    public PhysicsEntity(EntityType type) {
        super(type);
    }

    public PhysicsEntity setFixtureDef(FixtureDef def) {
        fixtureDef = def;
        return this;
    }

    public PhysicsEntity setBodyDef(BodyDef def) {
        bodyDef = def;
        return this;
    }

    public PhysicsEntity setBodyType(BodyType type) {
        bodyDef.type = type;
        return this;
    }

    public Vec2 getBodyPosition() {
        return body.getPosition();
    }

    /**
     *
     * @param vector x and y in pixels
     * @return
     */
    public PhysicsEntity setLinearVelocity(Point2D vector) {
        return setBodyLinearVelocity(new Vec2(PhysicsManager.toMeters(vector.getX()), PhysicsManager.toMeters(-vector.getY())).mulLocal(60));
    }

    /**
     *
     * @param vector x and y in meters
     * @return
     */
    public PhysicsEntity setBodyLinearVelocity(Vec2 vector) {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity has not been added to the world yet! Call addEntities(entity) first");

        body.setLinearVelocity(vector);
        return this;
    }
}
