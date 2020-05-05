/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adds physics properties to an entity.
 *
 * This component updates position and rotation components of entities
 * based on the physics properties.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class PhysicsComponent extends Component {

    FixtureDef fixtureDef = new FixtureDef();
    BodyDef bodyDef = new BodyDef();

    Body body;

    private List<Entity> groundedList = new ArrayList<>();

    private ReadOnlyBooleanWrapper onGroundProperty = new ReadOnlyBooleanWrapper(false);

    private boolean raycastIgnored = false;

    private Runnable onInitPhysics = EmptyRunnable.INSTANCE;

    private PhysicsWorld physicsWorld;

    void setWorld(PhysicsWorld world) {
        physicsWorld = world;
    }

    private PhysicsWorld getPhysicsWorld() {
        if (physicsWorld == null)
            throw new IllegalStateException("Physics not initialized yet! Use setOnPhysicsInitialized() instead");
        return physicsWorld;
    }

    void onInitPhysics() {
        onInitPhysics.run();
    }

    /**
     * Note: for this to work you need to add ground sensor.
     *
     * @return true if entity is standing on top of another entity with physics component
     */
    public boolean isOnGround() {
        return !groundedList.isEmpty();
    }

    /**
     * Note: for this to work you need to add ground sensor.
     *
     * @return true if entity is standing on top of another entity with physics component
     */
    public ReadOnlyBooleanProperty onGroundProperty() {
        return onGroundProperty.getReadOnlyProperty();
    }

    public Body getBody() {
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

    private Map<HitBox, SensorCollisionHandler> sensorHandlers = new HashMap<>();

    public Map<HitBox, SensorCollisionHandler> getSensorHandlers() {
        return sensorHandlers;
    }

    public void addGroundSensor(HitBox box) {
        sensorHandlers.put(box, new SensorCollisionHandler() {
            @Override
            protected void onCollisionBegin(Entity other) {
                groundedList.add(other);
                onGroundProperty.setValue(isOnGround());
            }

            @Override
            protected void onCollision(Entity other) {

            }

            @Override
            protected void onCollisionEnd(Entity other) {
                groundedList.remove(other);
                onGroundProperty.setValue(isOnGround());
            }
        });
    }

    public void addSensor(HitBox box, SensorCollisionHandler handler) {
        sensorHandlers.put(box, handler);
    }

    public void removeSensor(HitBox box) {
        box.unbind();
        getPhysicsWorld().destroyFixture(body, box);
        sensorHandlers.remove(box);
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
     * @return true if moving in X axis
     */
    public boolean isMovingX() {
        return FXGLMath.abs(getVelocityX()) > 0;
    }

    /**
     * @return true if moving in Y axis
     */
    public boolean isMovingY() {
        return FXGLMath.abs(getVelocityY()) > 0;
    }

    /**
     * @return true if moving in X or Y axis
     */
    public boolean isMoving() {
        return isMovingX() || isMovingY();
    }

    /**
     * Set linear velocity for a physics entity.
     * <p>
     * Use this method to move a physics entity.
     * Please note that the vector x and y are in pixels per second.
     *
     * @param vector x and y in pixels
     */
    public void setLinearVelocity(Point2D vector) {
        setBodyLinearVelocity(getPhysicsWorld().toVector(vector));
    }

    /**
     * Set linear velocity for a physics entity.
     * <p>
     * Use this method to move a physics entity.
     * Please note that x and y are in pixels per second.
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
     * x and y of the argument are in meters per second.
     *
     * @param vector x and y in meters
     */
    public void setBodyLinearVelocity(Vec2 vector) {
        getBody().setLinearVelocity(vector);
    }

    /**
     * @return linear velocity in pixels per second.
     */
    public Point2D getLinearVelocity() {
        return getPhysicsWorld().toVector(getBody().getLinearVelocity());
    }

    /**
     * Set velocity (angle in deg) at which the entity will rotate per second.
     *
     * @param velocity value in ~ degrees per second
     */
    public void setAngularVelocity(double velocity) {
        getBody().setAngularVelocity((float) -FXGLMath.toRadians(velocity));
    }

    /**
     * Apply an impulse at a point. This immediately modifies the velocity. It also modifies the angular velocity if the point of application is not at the center of mass. This wakes up the body.
     *
     * @param impulse the world impulse vector (in pixel/sec)
     * @param point the world position of the point of application (in pixel)
     * @param wake if this impulse should wake up the body
     */
    public void applyLinearImpulse(Point2D impulse, Point2D point, boolean wake) {
        applyBodyLinearImpulse(getPhysicsWorld().toVector(impulse), getPhysicsWorld().toPoint(point), wake);
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
        applyBodyForce(getPhysicsWorld().toVector(force), getPhysicsWorld().toPoint(point));
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
        applyBodyForceToCenter(getPhysicsWorld().toVector(force));
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

    private Vec2 minMeters = Pools.obtain(Vec2.class);

    @Override
    public void onUpdate(double tpf) {
        if (body == null)
            return;

        // these give us min world coordinates of the overall bbox
        // but they are not coordinates of the entity

        minMeters.set(
                getBody().getPosition().x - getPhysicsWorld().toMetersF(entity.getWidth() / 2),
                getBody().getPosition().y + getPhysicsWorld().toMetersF(entity.getHeight() / 2)
        );

        Point2D minWorld = getPhysicsWorld().toPoint(minMeters);

        // hence we do the following, as entity.x = minXWorld - minXLocal

        // we round positions so that it's easy for the rest of the world to work with
        // snapped to pixel values
        entity.setX(
                Math.round(minWorld.getX() - entity.getBoundingBoxComponent().getMinXLocal())
        );

        entity.setY(
                Math.round(minWorld.getY() - entity.getBoundingBoxComponent().getMinYLocal())
        );

        entity.setRotation(-Math.toDegrees(getBody().getAngle()));
    }

    @Override
    public void onRemoved() {
        Pools.free(minMeters);
    }

    /**
     * Repositions an entity that supports physics directly in the physics world.
     * Note: depending on how it is used, it may cause non-physical behavior.
     *
     * @param point point in game world coordinates (pixels)
     */
    public void overwritePosition(Point2D point) {
        double w = getEntity().getWidth();
        double h = getEntity().getHeight();

        Vec2 positionMeters = getPhysicsWorld().toPoint(new Point2D(
                point.getX() + w / 2,
                point.getY() + h / 2
        ));

        getBody().setTransform(positionMeters, getBody().getAngle());
    }

    /**
     * Overwrite entity's angle in the physics world.
     * May cause non-physical behavior.
     *
     * @param angDegrees angle in degrees
     */
    public void overwriteAngle(double angDegrees) {
        getBody().setTransform(getBody().getPosition(), (float) -FXGLMath.toRadians(angDegrees));
    }
}
