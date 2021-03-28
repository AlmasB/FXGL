/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.core.math.FXGLMath.random;

/**
 * Holds configuration of predefined particle emitters.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class ParticleEmitters {

    /**
     * @return new emitter with fire configuration
     */
    public static ParticleEmitter newFireEmitter() {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(15);
        emitter.setEmissionRate(0.5);
        emitter.setStartColor(Color.rgb(255, 255, 90));
        emitter.setEndColor(Color.rgb(230, 75, 40));
        emitter.setSize(9, 12);
        emitter.setVelocityFunction(i -> new Point2D(random(-0.5, 0.5) * 0.25 * 60, FXGLMath.randomDouble() * -1 * 60));
        emitter.setSpawnPointFunction(i -> new Point2D(0, 0).add(new Point2D(i * (FXGLMath.randomDouble() - 0.5), FXGLMath.randomDouble() - 1)));
        emitter.setScaleFunction(i -> new Point2D(random(-0.01, 0.01) * 10, FXGLMath.randomDouble() * -0.1));
        emitter.setExpireFunction(i -> Duration.seconds(1));
        emitter.setBlendMode(BlendMode.ADD);

        return emitter;
    }

    /**
     * @param width emitter width
     * @return new emitter with fire configuration
     */
    public static ParticleEmitter newFireEmitter(int width) {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(width / 32 + 1);
        emitter.setEmissionRate(0.05);
        emitter.setSize(16, 32);
        emitter.setVelocityFunction((i) -> new Point2D(random(-1, 1) * 2.5, -FXGLMath.randomDouble() * random(10, 30)));
        emitter.setSpawnPointFunction((i) -> new Point2D(random(0, width-32), 0));
        emitter.setScaleFunction((i) -> new Point2D(0.15, 0.10));
        emitter.setExpireFunction(i -> Duration.seconds(random(4, 7)));
        emitter.setBlendMode(BlendMode.ADD);
        //emitter.setSourceImage(image("particles/fire.png", 32, 32));

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
        emitter.setSpawnPointFunction(i -> new Point2D(0, 0));
        emitter.setVelocityFunction(i -> new Point2D(Math.cos(i), Math.sin(i)).multiply(explosionRadius));
        emitter.setScaleFunction(i -> new Point2D(FXGLMath.randomDouble() * -0.1, FXGLMath.randomDouble() * -0.1));
        emitter.setExpireFunction(i -> Duration.seconds(0.5));
        emitter.setColor(Color.rgb((int) random(200, 255), 30, 20));
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

        emitter.setSpawnPointFunction(i -> {
            Point2D vector = new Point2D(Math.cos(i), Math.sin(i));
            return new Point2D(0, 0).add(vector.multiply(25));
        });
        emitter.setVelocityFunction(i -> {
            Point2D vector = new Point2D(Math.cos(i), Math.sin(i));
            Point2D newPos = new Point2D(0, 0).add(vector.multiply(25));
            return newPos.subtract(Point2D.ZERO).multiply(-0.05 * 60);
        });
        emitter.setScaleFunction(i -> new Point2D(FXGLMath.randomDouble() * -0.1, FXGLMath.randomDouble() * -0.1));
        emitter.setExpireFunction(i -> Duration.seconds(0.5));
        emitter.setColor(Color.rgb((int) random(200, 255), 30, 20));
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
        emitter.setSpawnPointFunction(i -> Point2D.ZERO);
        emitter.setVelocityFunction(i -> new Point2D(random(-1, 1), random(-6, -5)).multiply(0.1 * 60));
        emitter.setAccelerationFunction(() -> new Point2D(0, random(0.01, 0.015)));
        emitter.setExpireFunction(i -> Duration.seconds(2));
        emitter.setColor(Color.rgb(30, 35, (int) random(200, 255)));

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
        emitter.setSpawnPointFunction(i -> Point2D.ZERO.add(random(-1, 1), 0));
        emitter.setVelocityFunction(i -> new Point2D(FXGLMath.randomDouble() * 0.1 * 60, 0));
        emitter.setAccelerationFunction(() -> new Point2D(0, FXGLMath.randomDouble() * -0.03));
        emitter.setExpireFunction(i -> Duration.seconds(random(1, 3)));
        emitter.setColor(Color.rgb(230, 230, 230));
        emitter.setScaleFunction(i -> new Point2D(-0.01, -0.05));

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
        emitter.setSpawnPointFunction(i -> new Point2D(FXGLMath.randomDouble()*width, -25));
        emitter.setVelocityFunction(i -> new Point2D(0, FXGLMath.randomDouble() * 15 * 60));
        emitter.setAccelerationFunction(() -> new Point2D(0, FXGLMath.randomDouble() * 0.03));
        emitter.setExpireFunction(i -> Duration.seconds(random(1, 3)));
        emitter.setScaleFunction(i -> new Point2D(-0.02, 0));

        return emitter;
    }
}
