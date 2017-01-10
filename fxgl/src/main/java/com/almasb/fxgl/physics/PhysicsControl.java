/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.ents.component.Required;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
import com.almasb.fxgl.core.math.Vec2;
import javafx.geometry.Point2D;
import org.jbox2d.dynamics.Body;

/**
 * This control updates position and rotation components of entities
 * based on the physics properties.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Required(PhysicsComponent.class)
public class PhysicsControl extends AbstractControl {

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
        position = Entities.getPosition(entity);
        rotation = Entities.getRotation(entity);
        bbox = Entities.getBBox(entity);

        body = entity.getComponentUnsafe(PhysicsComponent.class).body;
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
