/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import javafx.geometry.Point2D;

/**
 * This control updates position and rotation components of entities
 * based on the physics properties.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PhysicsComponent.class)
public class PhysicsControl extends Control {

    private Body body;

    private PhysicsWorld physicsWorld;

    private PositionComponent position;
    private RotationComponent rotation;
    private BoundingBoxComponent bbox;

    private double appHeight;

    PhysicsControl(double appHeight) {
        this.appHeight = appHeight;
        this.physicsWorld = FXGL.getApp().getPhysicsWorld();
    }

    @Override
    public void onAdded(Entity entity) {
        body = entity.getComponent(PhysicsComponent.class).body;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        // we round positions so that it's easy for the rest of the world to work with
        // snapped to pixel values
        position.setX(
                Math.round(toPixels(body.getPosition().x - toMeters(bbox.getWidth() / 2)))
        );

        position.setY(
                Math.round(toPixels(toMeters(appHeight) - body.getPosition().y - toMeters(bbox.getHeight() / 2)))
        );

        rotation.setValue(-Math.toDegrees(body.getAngle()));
    }

    /**
     * Repositions an entity that supports physics directly in the physics world.
     * Note: depending on how it is used, it may cause non-physical behavior.
     *
     * @param point point in game world coordinates (pixels)
     */
    public void reposition(Point2D point) {
        double w = bbox.getWidth();
        double h = bbox.getHeight();

        body.setTransform(new Vec2(
                toMeters(point.getX() + w / 2),
                toMeters(appHeight - (point.getY() + h / 2))),
                body.getAngle());
    }

    private float toMeters(double pixels) {
        return physicsWorld.toMeters(pixels);
    }

    private float toPixels(double meters) {
        return physicsWorld.toPixels(meters);
    }
}
