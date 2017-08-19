/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.effect;

import com.almasb.fxgl.animation.Interpolators;
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
        emitter.setColor(Color.rgb(230, 75, 40));
        emitter.setSize(9, 12);
        emitter.setVelocityFunction((i, x, y) -> new Point2D(rand(-0.5, 0.5) * 0.25 * 60, rand() * -1 * 60));
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(x, y).add(new Point2D(i * (rand() - 0.5), (rand() - 1))));
        emitter.setScaleFunction((i, x, y) -> new Point2D(rand(-0.01, 0.01) * 10, rand() * -0.1));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(1));
        emitter.setBlendMode(BlendMode.ADD);

        return emitter;
    }

    /**
     * @return new emitter with explosion configuration
     */
    public static ParticleEmitter newExplosionEmitter(int explosionRadius) {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(100);
        emitter.setEmissionRate(0.0166);
        emitter.setMaxEmissions(1);
        emitter.setSize(5, 20);
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(x, y));
        emitter.setVelocityFunction((i, x, y) -> new Point2D(Math.cos(i), Math.sin(i)).multiply(explosionRadius));
        emitter.setScaleFunction((i, x, y) -> new Point2D(rand() * -0.1, rand() * -0.1));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(0.5));
        emitter.setColor(Color.rgb((int) rand(200, 255), 30, 20));
        emitter.setBlendMode(BlendMode.ADD);
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());

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
            return newPos.subtract(new Point2D(x, y)).multiply(-0.05 * 60);
        });
        emitter.setScaleFunction((i, x, y) -> new Point2D(rand() * -0.1, rand() * -0.1));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(0.5));
        emitter.setColor(Color.rgb((int) rand(200, 255), 30, 20));
        emitter.setBlendMode(BlendMode.ADD);

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
        emitter.setVelocityFunction((i, x, y) -> new Point2D(rand(-1, 1), rand(-6, -5)).multiply(0.1 * 60));
        emitter.setAccelerationFunction(() -> new Point2D(0, rand(0.01, 0.015)));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(2));
        emitter.setColor(Color.rgb(30, 35, (int) rand(200, 255)));

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
        emitter.setVelocityFunction((i, x, y) -> new Point2D((rand() * 0.1 * 60), 0));
        emitter.setAccelerationFunction(() -> new Point2D(0, rand() * -0.03));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(rand(1, 3)));
        emitter.setColor(Color.rgb(230, 230, 230));
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
        emitter.setSize(6, 7);
        emitter.setColor(Color.AQUA);
        emitter.setSpawnPointFunction((i, x, y) -> new Point2D(rand()*width + x, -25 + y));
        emitter.setVelocityFunction((i, x, y) -> new Point2D(0, (rand() * 15 * 60)));
        emitter.setAccelerationFunction(() -> new Point2D(0, rand() * 0.03));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(rand(1, 3)));
        emitter.setScaleFunction((i, x, y) -> new Point2D(-0.02, 0));

        return emitter;
    }
}
