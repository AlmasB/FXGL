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

package com.almasb.fxgl.entity.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;
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
        Entities.getPosition(entity).translate(velocity.multiply(60 * tpf));
    }
}
