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

import com.almasb.ents.AbstractComponent;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
import javafx.geometry.Point2D;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Adds physics properties to an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
@Required(RotationComponent.class)
@Required(BoundingBoxComponent.class)
public class PhysicsComponent extends AbstractComponent {
    FixtureDef fixtureDef = new FixtureDef();
    BodyDef bodyDef = new BodyDef();

    Body body;
    Fixture fixture;

    private boolean raycastIgnored = false;

    Runnable onInitPhysics;

    void onInitPhysics() {
        if (onInitPhysics != null) {
            onInitPhysics.run();
        }
    }

    /**
     * Set a callback to run when this entity has been added to physics world.
     *
     * @param code the code to run
     */
    public void setOnPhysicsInitialized(Runnable code) {
        onInitPhysics = code;
    }

    /**
     * Set custom fixture definition to describe a generated
     * fixture for this physics entity
     *
     * @param def fixture definition
     */
    public void setFixtureDef(FixtureDef def) {
        fixtureDef = def;
    }

    /**
     * Set custom body definition to describe a generated
     * body for this physics entity
     *
     * @param def body definition
     */
    public void setBodyDef(BodyDef def) {
        bodyDef = def;
    }

    /**
     * A convenience method to avoid setting body definition
     * if only a change of body type is required
     *
     * @param type body type
     */
    public void setBodyType(BodyType type) {
        bodyDef.setType(type);
    }

    /**
     * Set linear velocity for a physics entity
     * <p>
     * Use this method to move a physics entity
     * Please note that the vector x and y are in pixels
     *
     * @param vector x and y in pixels
     */
    public void setLinearVelocity(Point2D vector) {
        setBodyLinearVelocity(PhysicsWorld.toVector(vector).mulLocal(60));
    }

    /**
     * Set linear velocity for a physics entity
     * <p>
     * Use this method to move a physics entity
     * Please note that x and y are in pixels
     *
     * @param x and y in pixels
     */
    public void setLinearVelocity(double x, double y) {
        setLinearVelocity(new Point2D(x, y));
    }

    /**
     * Set linear velocity for a physics entity
     * <p>
     * Similar to {@link #setLinearVelocity(Point2D)} but
     * x and y of the argument are in meters
     *
     * @param vector x and y in meters
     */
    public void setBodyLinearVelocity(Vec2 vector) {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity not initialized yet! Use setOnPhysicsInitialized() instead");

        body.setLinearVelocity(vector);
    }

    /**
     * @return linear velocity in pixels
     */
    public Point2D getLinearVelocity() {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity not initialized yet! Use setOnPhysicsInitialized() instead");

        return PhysicsWorld.toVector(body.getLinearVelocity().mul(1 / 60f));
    }

    /**
     * Set velocity (angle in deg) at which the entity will rotate per tick.
     *
     * @param velocity value in ~ degrees per tick
     */
    public void setAngularVelocity(double velocity) {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity not initialized yet! Use setOnPhysicsInitialized() instead");

        body.setAngularVelocity((float) -velocity);
    }

    public void setBodyAngularVelocity(float velocity) {
        if (body == null)
            throw new IllegalStateException("PhysicsEntity not initialized yet! Use setOnPhysicsInitialized() instead");

        body.setAngularVelocity(velocity);
    }

    /**
     * Set true to make raycast ignore this entity
     *
     * @param b raycast flag
     */
    public void setRaycastIgnored(boolean b) {
        raycastIgnored = b;
    }

    /**
     * @return true if raycast should ignore this entity,
     * false otherwise
     */
    public boolean isRaycastIgnored() {
        return raycastIgnored;
    }
}
