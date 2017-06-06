/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.ecs.component.Required;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
import javafx.geometry.Point2D;

/**
 * Generic projectile control.
 * Automatically rotates the entity based on velocity direction.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PositionComponent.class)
@Required(RotationComponent.class)
public class ProjectileControl extends AbstractControl {

    private Point2D velocity;
    private double speed;

    public ProjectileControl(Point2D direction, double speed) {
        this.velocity = direction.normalize().multiply(speed);
        this.speed = speed;
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
     * Set direction in which projectile is moving.
     *
     * @param direction the vector
     */
    public void setDirection(Point2D direction) {
        this.velocity = direction.normalize().multiply(speed);
        Entities.getRotation(getEntity()).rotateToVector(velocity);
    }

    /**
     * @return current speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed value
     */
    public void setSpeed(double speed) {
        this.speed = speed;
        this.velocity = velocity.normalize().multiply(speed);
        Entities.getRotation(getEntity()).rotateToVector(velocity);
    }

    @Override
    public void onAdded(Entity entity) {
        Entities.getRotation(entity).rotateToVector(velocity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        Entities.getPosition(entity).translate(velocity.multiply(tpf));
    }
}
