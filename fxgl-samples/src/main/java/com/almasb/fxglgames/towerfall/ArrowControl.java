/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.towerfall;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.Entities;
import javafx.geometry.Point2D;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ArrowControl extends Control {

    private Point2D velocity;

    public ArrowControl(Point2D direction) {
        this.velocity = direction.normalize().multiply(550);
    }

    /**
     * @return velocity
     */
    public Point2D getVelocity() {
        return velocity;
    }

    /**
     * @return direction vector (normalized)
     */
    public Point2D getDirection() {
        return velocity.normalize();
    }

    /**
     * @return current speed
     */
    public double getSpeed() {
        return velocity.magnitude();
    }

    @Override
    public void onAdded(Entity entity) {}

    @Override
    public void onUpdate(Entity entity, double tpf) {
        //velocity = velocity.multiply()

        velocity = velocity.add(0, 4.3);

        Entities.getRotation(entity).rotateToVector(velocity);
        Entities.getPosition(entity).translate(velocity.multiply(tpf));
    }
}
