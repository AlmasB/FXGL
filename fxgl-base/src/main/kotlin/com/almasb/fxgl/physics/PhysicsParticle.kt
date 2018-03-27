/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.particle.Particle
import javafx.geometry.Point2D
import javafx.scene.effect.BlendMode
import javafx.scene.paint.Paint
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class PhysicsParticle(position: Point2D, radius: Double, color: Paint)
    : Particle(position, Point2D.ZERO, Point2D.ZERO, radius, Point2D.ZERO,
        Duration.seconds(10.0), color, color, BlendMode.SRC_OVER) {
}