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

package com.almasb.fxgl.entity;

import com.almasb.ents.Component;
import com.almasb.ents.Control;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.*;
import com.almasb.fxgl.gameplay.GameWorld;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Node;

/**
 * Helper class with static convenience methods.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class Entities {

    /**
     * Convenient way to obtain position component.
     *
     * @param e entity
     * @return position component
     */
    public static PositionComponent getPosition(Entity e) {
        return e.getComponentUnsafe(PositionComponent.class);
    }

    /**
     * Convenient way to obtain rotation component.
     *
     * @param e entity
     * @return rotation component
     */
    public static RotationComponent getRotation(Entity e) {
        return e.getComponentUnsafe(RotationComponent.class);
    }

    /**
     * Convenient way to obtain bbox component.
     *
     * @param e entity
     * @return bbox component
     */
    public static BoundingBoxComponent getBBox(Entity e) {
        return e.getComponentUnsafe(BoundingBoxComponent.class);
    }

    /**
     * Convenient way to obtain physics component.
     *
     * @param e entity
     * @return physics component
     */
    public static PhysicsComponent getPhysics(Entity e) {
        return e.getComponentUnsafe(PhysicsComponent.class);
    }

    /**
     * Convenient way to obtain main view component.
     *
     * @param e entity
     * @return main view component
     */
    public static MainViewComponent getMainView(Entity e) {
        return e.getComponentUnsafe(MainViewComponent.class);
    }

    /**
     * Convenient way to obtain type component.
     *
     * @param e entity
     * @return type component
     */
    public static TypeComponent getType(Entity e) {
        return e.getComponentUnsafe(TypeComponent.class);
    }

    /**
     * Create an entity with bouding box around the screen with given thickness.
     *
     * @param thickness thickness of hit boxes around the screen
     * @return entity with screen bounds
     */
    public static Entity makeScreenBounds(double thickness) {
        double w = FXGL.getSettings().getWidth();
        double h = FXGL.getSettings().getHeight();

        Entity bounds = new Entity();
        bounds.addComponent(new PositionComponent(0, 0));
        bounds.addComponent(new RotationComponent(0));

        bounds.addComponent(new BoundingBoxComponent(
                new HitBox("LEFT",  new Point2D(-thickness, 0), BoundingShape.box(thickness, h)),
                new HitBox("RIGHT", new Point2D(w, 0), BoundingShape.box(thickness, h)),
                new HitBox("TOP",   new Point2D(0, -thickness), BoundingShape.box(w, thickness)),
                new HitBox("BOT",   new Point2D(0, h), BoundingShape.box(w, thickness))
        ));

        bounds.addComponent(new PhysicsComponent());

        return bounds;
    }

    /**
     * Creates new entity builder.
     *
     * @return entity builder
     */
    public static GameEntityBuilder builder() {
        return new GameEntityBuilder();
    }

    /**
     * Provides fluent API for building entities.
     */
    public static class GameEntityBuilder {
        private GameEntity entity = new GameEntity();

        public GameEntityBuilder type(Enum<?> type) {
            entity.getTypeComponent().setValue(type);
            return this;
        }

        public GameEntityBuilder at(double x, double y) {
            entity.getPositionComponent().setValue(x, y);
            return this;
        }

        public GameEntityBuilder at(Point2D position) {
            return at(position.getX(), position.getY());
        }

        public GameEntityBuilder rotate(double angle) {
            entity.getRotationComponent().setValue(angle);
            return this;
        }

        public GameEntityBuilder bbox(HitBox box) {
            entity.getBoundingBoxComponent().addHitBox(box);
            return this;
        }

        public GameEntityBuilder viewFromNode(Node view) {
            entity.getMainViewComponent().setView(view);
            return this;
        }

        public GameEntityBuilder viewFromNodeWithBBox(Node view) {
            entity.getMainViewComponent().setView(view, true);
            return this;
        }

        public GameEntityBuilder viewFromTexture(String textureName) {
            entity.getMainViewComponent().setTexture(textureName);
            return this;
        }

        public GameEntityBuilder viewFromTextureWithBBox(String textureName) {
            entity.getMainViewComponent().setTexture(textureName, true);
            return this;
        }

        public GameEntityBuilder with(Component... components) {
            for (Component c : components)
                entity.addComponent(c);
            return this;
        }

        public GameEntityBuilder with(Control... controls) {
            for (Control c : controls)
                entity.addControl(c);
            return this;
        }

        public GameEntity build() {
            return entity;
        }

        public GameEntity buildAndAttach(GameWorld world) {
            world.addEntity(entity);
            return entity;
        }
    }
}
