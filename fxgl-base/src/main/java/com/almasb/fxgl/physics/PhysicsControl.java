/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Required;
import com.almasb.fxgl.physics.box2d.dynamics.Body;
import javafx.geometry.Point2D;

/**
 * This control updates position and rotation components of entities
 * based on the physics properties.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PhysicsComponent.class)
public class PhysicsControl extends Component {

    private Body body;

    private PhysicsWorld physicsWorld;

    private Vec2 minMeters = Pools.obtain(Vec2.class);

    PhysicsControl(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    @Override
    public void onAdded() {
        body = entity.getComponent(PhysicsComponent.class).body;
    }

    @Override
    public void onUpdate(double tpf) {

        // these give us min world coordinates of the overall bbox
        // but they are not coordinates of the entity

        minMeters.set(
                body.getPosition().x - physicsWorld.toMetersF(entity.getWidth() / 2),
                body.getPosition().y + physicsWorld.toMetersF(entity.getHeight() / 2)
        );

        Point2D minWorld = physicsWorld.toPoint(minMeters);

        // hence we do the following, as entity.x = minXWorld - minXLocal

        // we round positions so that it's easy for the rest of the world to work with
        // snapped to pixel values
        entity.setX(
                Math.round(minWorld.getX() - entity.getBoundingBoxComponent().getMinXLocal())
        );

        entity.setY(
                Math.round(minWorld.getY() - entity.getBoundingBoxComponent().getMinYLocal())
        );

        entity.setRotation(-Math.toDegrees(body.getAngle()));
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
    public void reposition(Point2D point) {
        double w = getEntity().getWidth();
        double h = getEntity().getHeight();

        Vec2 positionMeters = physicsWorld.toPoint(new Point2D(
                point.getX() + w / 2,
                point.getY() + h / 2
        ));

        body.setTransform(positionMeters, body.getAngle());
    }
}
