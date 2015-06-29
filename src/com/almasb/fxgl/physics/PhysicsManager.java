/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
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

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.entity.Entity;

public final class PhysicsManager {

    private static final float TIME_STEP = 1 / 60.0f;

    private GameApplication app;

    private World world = new World(new Vec2(0, -10));

    public PhysicsManager(GameApplication app) {
        this.app = app;
    }

    public void setGravity(double x, double y) {
        world.setGravity(new Vec2().addLocal((float)x,(float)y));
    }

    public void onUpdate(long now) {
        world.step(TIME_STEP, 8, 3);

        for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
            Entity e = (Entity) body.getUserData();
            e.setTranslateX(
                    toPixels(
                            body.getPosition().x
                                    - toMeters(e.getLayoutBounds().getWidth() / 2)));
            e.setTranslateY(
                    toPixels(
                            toMeters(app.getHeight()) - body.getPosition().y
                                    - toMeters(e.getLayoutBounds().getHeight() / 2)));
            e.setRotate(-Math.toDegrees(body.getAngle()));
        }

    }

    /**
     * Do NOT call manually. This is called by FXGL Application
     * to create a physics body in physics space (world)
     *
     * @param e
     */
    public void createBody(PhysicsEntity e) {
        double x = e.getTranslateX(),
                y = e.getTranslateY(),
                w = e.getLayoutBounds().getWidth(),
                h = e.getLayoutBounds().getHeight();

        if (e.fixtureDef.shape == null) {
            PolygonShape rectShape = new PolygonShape();
            rectShape.setAsBox(toMeters(w / 2), toMeters(h / 2));
            e.fixtureDef.shape = rectShape;
        }

        e.bodyDef.position.set(toMeters(x + w / 2), toMeters(app.getHeight() - (y + h / 2)));
        e.body = world.createBody(e.bodyDef);
        e.fixture = e.body.createFixture(e.fixtureDef);
        e.body.setUserData(e);
    }

    /**
     * Do NOT call manually. This is called by FXGL Application
     * to destroy a physics body in physics space (world)
     *
     * @param e
     */
    public void destroyBody(PhysicsEntity e) {
        world.destroyBody(e.body);
    }

    public static float toMeters(double pixels) {
        return (float)pixels * 0.05f;
    }

    public static float toPixels(double meters) {
        return (float)meters * 20f;
    }
}
