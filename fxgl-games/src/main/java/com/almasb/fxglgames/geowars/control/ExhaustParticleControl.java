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

package com.almasb.fxglgames.geowars.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.collection.ObjectMap;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.core.pool.Pools;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import static java.lang.Math.min;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ExhaustParticleControl extends AbstractControl {

    private static final Image PARTICLE_IMAGE;
    public static final ObjectMap<Color, Image> coloredImages = new ObjectMap<>();

    static {
        PARTICLE_IMAGE = FXGL.getAssetLoader().loadTexture("Glow.png").getImage();
    }

    private Vec2 velocity;
    private float lifespan;
    private long spawnTime;
    private Color color;

    public ExhaustParticleControl(Vec2 velocity, float lifespan, Color color) {
        this.velocity = Pools.obtain(Vec2.class);
        this.velocity.set(velocity);

        this.lifespan = lifespan;
        spawnTime = System.currentTimeMillis();
        this.color = color;

        if (!coloredImages.containsKey(color)) {
            colorImage(color);
        }
    }

    public static void colorImage(Color color) {
        coloredImages.put(color, new Texture(PARTICLE_IMAGE).toColor(color).getImage());
    }

    @Override
    public void onAdded(Entity e) {
        GameEntity entity = (GameEntity) e;
        entity.setView(new Texture(coloredImages.get(color)));
    }

    @Override
    public void onRemoved(Entity entity) {
        Pools.free(velocity);
    }

    @Override
    public void onUpdate(Entity e, double tpf) {
        GameEntity entity = (GameEntity) e;

        // movement
        entity.translateX(velocity.x * 3 * tpf);
        entity.translateY(velocity.y * 3 * tpf);

        velocity.mulLocal(1 - 3f * (float) tpf);
        if (Math.abs(velocity.x) + Math.abs(velocity.y) < 0.001f) {
            velocity.setZero();
        }

        // rotation and scale
        if (velocity.x != 0 && velocity.y != 0) {
            entity.setRotation(velocity.angle());
        }

        // alpha
        double speed = velocity.length();
        long difTime = System.currentTimeMillis() - spawnTime;
        float percentLife = 1 - difTime / lifespan;

        double opacity = min(1.5f, min(percentLife * 2, speed));
        opacity *= opacity;

        entity.getView().setOpacity(opacity);

        // is particle expired?
        if (difTime > lifespan) {
            entity.removeFromWorld();
        }
    }

//
//    public void applyGravity(Vector3f gravity, float distance) {
//        Vector3f additionalVelocity = gravity
//                .mult(1000f / (distance * distance + 10000f));
//        velocity.addLocal(additionalVelocity);
//
//        if (distance < 400) {
//            additionalVelocity = new Vector3f(gravity.y, -gravity.x, 0)
//                    .mult(3f / (distance + 100));
//            velocity.addLocal(additionalVelocity);
//        }
//    }
}