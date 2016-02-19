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
import com.almasb.ents.EntityWorld;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.component.*;
import com.almasb.fxgl.gameplay.GameWorld;
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

    public static PositionComponent getPosition(Entity e) {
        return e.getComponentUnsafe(PositionComponent.class);
    }

    public static RotationComponent getRotation(Entity e) {
        return e.getComponentUnsafe(RotationComponent.class);
    }

    public static BoundingBoxComponent getBBox(Entity e) {
        return e.getComponentUnsafe(BoundingBoxComponent.class);
    }

    public static PhysicsComponent getPhysics(Entity e) {
        return e.getComponentUnsafe(PhysicsComponent.class);
    }

    public static MainViewComponent getMainView(Entity e) {
        return e.getComponentUnsafe(MainViewComponent.class);
    }

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
        double w = FXGL.getDouble("settings.width");
        double h = FXGL.getDouble("settings.height");

        Entity bounds = new Entity();
        bounds.addComponent(new PositionComponent(0, 0));
        bounds.addComponent(new RotationComponent(0));

        bounds.addComponent(new BoundingBoxComponent(
                new HitBox("LEFT",  new BoundingBox(-thickness, 0, thickness, h)),
                new HitBox("RIGHT", new BoundingBox(w, 0, thickness, h)),
                new HitBox("TOP",   new BoundingBox(0, -thickness, w, thickness)),
                new HitBox("BOT",   new BoundingBox(0, h, w, thickness))
        ));

        bounds.addComponent(new PhysicsComponent());

        return bounds;
    }

    public static GameEntityBuilder builder() {
        return new GameEntityBuilder();
    }

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

        public GameEntityBuilder view(EntityView view) {
            entity.getMainViewComponent().setView(view);
            return this;
        }

        public GameEntityBuilder viewFromNode(Node graphics) {
            entity.getMainViewComponent().setGraphics(graphics);
            return this;
        }

        public GameEntityBuilder viewWithBBox(EntityView view) {
            entity.getMainViewComponent().setView(view, true);
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
