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

/**
 * An entity being managed by PhysicsManager and hence
 * is being affected by physics space and its forces
 *
 * {@link #translate(Point2D)} and {@link #setTranslateX(double)}
 * methods will NOT work. Use {@link #setLinearVelocity(Point2D)} to
 * move the object.
 *
 * BodyType.KINEMATIC will retain its velocity at all times unless manually changed
 * BodyType.DYNAMIC will lose its velocity over time based on external forces
 * BodyType.STATIC doesn't move even if you set its velocity to non-zero
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 * @version 1.0
 *
 */
public final class PhysicsEntity extends Entity {

    /*package-private*/ FixtureDef fixtureDef = new FixtureDef();
    /*package-private*/ BodyDef bodyDef = new BodyDef();

    /*package-private*/ Body body;
    /*package-private*/ Fixture fixture;

    private boolean raycastIgnored = false;

    /**
     * Constructs a PhysicsEntity with given type
     *
     * @param type
     */
    public PhysicsEntity(EntityType type) {
        super(type);
    }

    /*package-private*/ Runnable onInitPhysics;

    /*package-private*/ void onInitPhysics() {
        if (onInitPhysics != null) {
            onInitPhysics.run();
        }
    }

    public void setOnPhysicsInitialized(Runnable code) {
        onInitPhysics = code;
    }

    /**
     * Set custom fixture definition to describe a generated
     * fixture for this physics entity
     *
     * @param def
     * @return this object
     */
    public PhysicsEntity setFixtureDef(FixtureDef def) {
        fixtureDef = def;
        return this;
    }

    /**
     * Set custom body definition to describe a generated
     * body for this physics entity
     *
     * @param def
     * @return this entity
     */
    public PhysicsEntity setBodyDef(BodyDef def) {
        bodyDef = def;
        return this;
    }

    /**
     * A convenience method to avoid setting body definition
     * if only a change of body type is required
     *
     * @param type
     * @return this entity
     */
    public PhysicsEntity setBodyType(BodyType type) {
        bodyDef.type = type;
        return this;
    }

    /**
     * Set linear velocity for a physics entity
     *
     * Use this method to move a physics entity
     * Please note that the vector x and y are in pixels
     *
     * @param vector x and y in pixels
     * @return this entity
     */
    public PhysicsEntity setLinearVelocity(Point2D vector) {
        return setBodyLinearVelocity(PhysicsManager.toVector(vector).mulLocal(60));
    }

    /**
     * Set linear velocity for a physics entity
     *
     * Use this method to move a physics entity
     * Please note that x and y are in pixels
     *
     * @param x and y in pixels
     * @return this entity
     */
    public PhysicsEntity setLinearVelocity(double x, double y) {
        return setLinearVelocity(new Point2D(x, y));
    }

    /**
     * Set linear velocity for a physics entity
     *
     * Similar to {@link #setLinearVelocity(Point2D)} but
     * x and y of the argument are in meters
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

    /**
     *
     * @return linear velocity in pxels
     */
    public Point2D getLinearVelocity() {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity has not been added to the world yet! Call addEntities(entity) first");

        return PhysicsManager.toVector(body.getLinearVelocity().mul(1/60f));
    }

    /**
     * Set velocity (angle in deg) at which the entity will rotate per tick.
     *
     * @param velocity  value in ~ degrees per tick
     */
    public void setAngularVelocity(double velocity) {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity has not been added to the world yet! Call addEntities(entity) first");

        body.setAngularVelocity((float) -velocity);
    }

    /**
     * Set true to make raycast ignore this entity
     *
     * @param b
     */
    public void setRaycastIgnored(boolean b) {
        raycastIgnored = b;
    }

    /**
     *
     * @return true if raycast should ignore this entity,
     *          false otherwise
     */
    public boolean isRaycastIgnored() {
        return raycastIgnored;
    }
}
