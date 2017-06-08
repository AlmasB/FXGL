/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.Component;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;

/**
 * Adds physics properties to an entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
@Required(RotationComponent.class)
@Required(BoundingBoxComponent.class)
public class PhysicsComponent extends Component {

    FixtureDef fixtureDef = new FixtureDef();
    BodyDef bodyDef = new BodyDef();

    Body body;

    private boolean raycastIgnored = false;

    private Runnable onInitPhysics;

    private final PhysicsWorld physicsWorld;

    public PhysicsComponent() {
        physicsWorld = FXGL.getApp().getPhysicsWorld();
    }

    void onInitPhysics() {
        if (onInitPhysics != null) {
            onInitPhysics.run();
        }
    }

    public final Body getBody() {
        if (body == null)
            throw new IllegalStateException("Physics not initialized yet! Use setOnPhysicsInitialized() instead");
        return body;
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
     * fixture for this physics entity.
     *
     * @param def fixture definition
     */
    public void setFixtureDef(FixtureDef def) {
        fixtureDef = def;
    }

    /**
     * Set custom body definition to describe a generated
     * body for this physics entity.
     *
     * @param def body definition
     */
    public void setBodyDef(BodyDef def) {
        bodyDef = def;
    }

    /**
     * A convenience method to avoid setting body definition
     * if only a change of body type is required.
     *
     * @param type body type
     */
    public void setBodyType(BodyType type) {
        bodyDef.setType(type);
    }

    /**
     * Set linear velocity for a physics entity.
     * <p>
     * Use this method to move a physics entity.
     * Please note that the vector x and y are in pixels.
     *
     * @param vector x and y in pixels
     */
    public void setLinearVelocity(Point2D vector) {
        setBodyLinearVelocity(physicsWorld.toVector(vector));
    }

    /**
     * Set linear velocity for a physics entity.
     * <p>
     * Use this method to move a physics entity.
     * Please note that x and y are in pixels.
     *
     * @param x and y in pixels
     */
    public void setLinearVelocity(double x, double y) {
        setLinearVelocity(new Point2D(x, y));
    }

    public void setVelocityX(double x) {
        setLinearVelocity(x, getVelocityY());
    }

    public void setVelocityY(double y) {
        setLinearVelocity(getVelocityX(), y);
    }

    public double getVelocityX() {
        return getLinearVelocity().getX();
    }

    public double getVelocityY() {
        return getLinearVelocity().getY();
    }

    /**
     * Set linear velocity for a physics entity.
     * <p>
     * Similar to {@link #setLinearVelocity(Point2D)} but
     * x and y of the argument are in meters.
     *
     * @param vector x and y in meters
     */
    public void setBodyLinearVelocity(Vec2 vector) {
        getBody().setLinearVelocity(vector);
    }

    /**
     * @return linear velocity in pixels
     */
    public Point2D getLinearVelocity() {
        return physicsWorld.toVector(getBody().getLinearVelocity());
    }

    /**
     * Set velocity (angle in deg) at which the entity will rotate per tick.
     *
     * @param velocity value in ~ degrees per tick
     */
    public void setAngularVelocity(double velocity) {
        getBody().setAngularVelocity((float) -velocity);
    }

    /**
     * Apply an impulse at a point. This immediately modifies the velocity. It also modifies the angular velocity if the point of application is not at the center of mass. This wakes up the body.
     *
     * @param impulse the world impulse vector (in pixel/sec)
     * @param point the world position of the point of application (in pixel)
     * @param wake if this impulse should wake up the body
     */
    public void applyLinearImpulse(Point2D impulse, Point2D point, boolean wake) {
        applyBodyLinearImpulse(physicsWorld.toVector(impulse), physicsWorld.toPoint(point), wake);
    }

    /**
     * Apply an impulse at a point. This immediately modifies the velocity. It also modifies the angular velocity if the point of application is not at the center of mass. This wakes up the body.
     *
     * @param impulse the world impulse vector (in meter/sec)
     * @param point the world position of the point of application (in meter)
     * @param wake if this impulse should wake up the body
     */
    public void applyBodyLinearImpulse(Vec2 impulse, Vec2 point, boolean wake) {
        getBody().applyLinearImpulse(impulse, point, wake);
    }

    /**
     * Apply a force at a world point. If the force is not applied at the center of mass, it will generate a torque and affect the angular velocity. This wakes up the body.
     *
     * @param force the world force vector (in pixel/sec)
     * @param point the world position of the point of application (in pixel)
     */
    public void applyForce(Point2D force, Point2D point) {
        applyBodyForce(physicsWorld.toVector(force), physicsWorld.toPoint(point));
    }

    /**
     * Apply a force at a world point. If the force is not applied at the center of mass, it will generate a torque and affect the angular velocity. This wakes up the body.
     *
     * @param force the world force vector (in meter/sec)
     * @param point the world position of the point of application (in meter)
     */
    public void applyBodyForce(Vec2 force, Vec2 point) {
        getBody().applyForce(force, point);
    }

    /**
     * Apply a force to the center of mass. This wakes up the body.
     *
     * @param force the world force vector (in pixel/sec)
     */
    public void applyForceToCenter(Point2D force) {
        applyBodyForceToCenter(physicsWorld.toVector(force));
    }

    /**
     * Apply a force to the center of mass. This wakes up the body.
     *
     * @param force the world force vector (in meter/sec)
     */
    public void applyBodyForceToCenter(Vec2 force) {
        getBody().applyForceToCenter(force);
    }

    /**
     * Apply an angular impulse.
     *
     * @param impulse the angular impulse (in pixel/sec)
     */
    public void applyAngularImpulse(float impulse) {
        applyBodyAngularImpulse(physicsWorld.toMeters(impulse));
    }

    /**
     * Apply an angular impulse.
     *
     * @param impulse the angular impulse (in meter/sec)
     */
    public void applyBodyAngularImpulse(float impulse) {
        getBody().applyAngularImpulse(impulse);
    }

    /**
     * Apply a torque. This affects the angular velocity without affecting the linear velocity of the center of mass. This wakes up the body.
     *
     * @param torque the force (in pixel)
     */
    public void applyTorque(float torque) {
        applyBodyTorque(physicsWorld.toMeters(torque));
    }

    /**
     * Apply a torque. This affects the angular velocity without affecting the linear velocity of the center of mass. This wakes up the body.
     *
     * @param torque the force (in meter)
     */
    public void applyBodyTorque(float torque) {
        getBody().applyTorque(torque);
    }

    /**
     * Set true to make raycast ignore this entity.
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
