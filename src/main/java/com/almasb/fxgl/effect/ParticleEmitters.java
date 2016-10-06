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

package com.almasb.fxgl.effect;

import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Random;

/**
 * Holds configuration of predefined particle emitters.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class ParticleEmitters {

    private static final Random random = new Random();

    /**
     * Returns a value in [0..1).
     *
     * @return random value between 0 (incl) and 1 (excl)
     */
    private static double rand() {
        return random.nextDouble();
    }

    /**
     * Returns a value in [min..max).
     *
     * @param min min bounds
     * @param max max bounds
     * @return a random value between min (incl) and max (excl)
     */
    private static double rand(double min, double max) {
        return rand() * (max - min) + min;
    }

    /**
     * @return new emitter with fire configuration
     */
    public static ParticleEmitter newFireEmitter() {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(15);
        emitter.setEmissionRate(0.5);
        emitter.setColorFunction(() -> Color.rgb(230, 75, 40));
        emitter.setSize(9, 12);
        emitter.setVelocityFunction((i, x, y) -> new Point2D(rand(-0.5, 0.5) * 0.25, rand() * -1));
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(x, y).add(new Point2D(i * (rand() - 0.5), (rand() - 1))));
        emitter.setScaleFunction((i, x, y) -> new Point2D(rand(-0.01, 0.01) * 10, rand() * -0.1));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(1));
        emitter.setBlendFunction((i, x, y) -> i < emitter.getNumParticles() / 2 ? BlendMode.ADD : BlendMode.COLOR_DODGE);

        return emitter;
    }

    /**
     * @return new emitter with explosion configuration
     */
    public static ParticleEmitter newExplosionEmitter() {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(100);
        emitter.setEmissionRate(0.0166);
        emitter.setSize(5, 20);
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(x, y));
        emitter.setVelocityFunction((i, x, y) -> new Point2D(Math.cos(i), Math.sin(i)).multiply(0.75));
        emitter.setScaleFunction((i, x, y) -> new Point2D(rand() * -0.1, rand() * -0.1));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(0.5));
        emitter.setColorFunction(() -> Color.rgb((int) rand(200, 255), 30, 20));
        emitter.setBlendFunction((i, x, y) -> i < emitter.getNumParticles() / 2 ? BlendMode.ADD : BlendMode.COLOR_BURN);

        return emitter;
    }

    /**
     * @return new emitter with implosion configuration
     */
    public static ParticleEmitter newImplosionEmitter() {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(100);
        emitter.setEmissionRate(0.0166);
        emitter.setSize(5, 20);

        emitter.setSpawnPointFunction((i, x, y) -> {
            Point2D vector = new Point2D(Math.cos(i), Math.sin(i));
            return new Point2D(x, y).add(vector.multiply(25));
        });
        emitter.setVelocityFunction((i, x, y) -> {
            Point2D vector = new Point2D(Math.cos(i), Math.sin(i));
            Point2D newPos = new Point2D(x, y).add(vector.multiply(25));
            return newPos.subtract(new Point2D(x, y)).multiply(-0.05);
        });
        emitter.setScaleFunction((i, x, y) -> new Point2D(rand() * -0.1, rand() * -0.1));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(0.5));
        emitter.setColorFunction(() -> Color.rgb((int) rand(200, 255), 30, 20));
        emitter.setBlendFunction((i, x, y) -> i < emitter.getNumParticles() / 2 ? BlendMode.ADD : BlendMode.COLOR_DODGE);

        return emitter;
    }

    /**
     * @return new emitter with sparks configuration
     */
    public static ParticleEmitter newSparkEmitter() {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(30);
        emitter.setEmissionRate(0.0166 / 2);
        emitter.setSize(1, 2);
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(x, y));
        emitter.setVelocityFunction((i, x, y) -> new Point2D(rand(-1, 1), rand(-6, -5)).multiply(0.1));
        emitter.setGravityFunction(() -> new Point2D(0, rand(0.01, 0.015)));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(2));
        emitter.setColorFunction(() -> Color.rgb(30, 35, (int) rand(200, 255)));

        return emitter;
    }

    /**
     * @return new emitter with smoke configuration
     */
    public static ParticleEmitter newSmokeEmitter() {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(5);
        emitter.setEmissionRate(1);
        emitter.setSize(9, 10);
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(x, y).add(rand(-1, 1), 0));
        emitter.setVelocityFunction((i, x, y) -> new Point2D((rand() * 0.1), 0));
        emitter.setGravityFunction(() -> new Point2D(0, rand() * -0.03));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(rand(1, 3)));
        emitter.setColorFunction(() -> Color.rgb(230, 230, 230));
        emitter.setScaleFunction((i, x, y) -> new Point2D(-0.01, -0.05));

        return emitter;
    }

    /**
     * @param width width of the rain wall
     * @return new emitter with rain configuration
     */
    public static ParticleEmitter newRainEmitter(int width) {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(5);
        emitter.setEmissionRate(1);
        emitter.setSize(1, 2);
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(rand()*width, -25));
        emitter.setVelocityFunction((i, x, y) -> new Point2D(0, (rand() * 15)));
        emitter.setGravityFunction(() -> new Point2D(0, rand() * 0.03));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(rand(1, 3)));
        emitter.setColorFunction(() -> Color.AQUA.darker());
        emitter.setScaleFunction((i, x, y) -> new Point2D(-0.02, 0));

        return emitter;
    }
}
