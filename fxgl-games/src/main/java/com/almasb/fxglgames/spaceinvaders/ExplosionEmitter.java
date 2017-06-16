/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.spaceinvaders;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.effect.ParticleEmitter;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ExplosionEmitter extends ParticleEmitter {

    public ExplosionEmitter() {
        setColor(Color.color(0.85, 0.85, 0.75, 0.75));
        setVelocityFunction((i, x, y) -> Vec2.fromAngle(i*10).mulLocal(FXGLMath.random(4f, 5f)).toPoint2D());
        setExpireFunction((i, x, y) -> Duration.seconds(0.6));
        setSpawnPointFunction((i, x, y) -> new Point2D(x, y));
        setSize(1, 2);
        setEmissionRate(0.001);
        setNumParticles(36);
    }
}
