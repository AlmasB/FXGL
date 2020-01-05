/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components.view

import com.almasb.fxgl.particle.ParticleComponent
import com.almasb.fxgl.particle.ParticleEmitters
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Point2D
import javafx.scene.effect.BlendMode
import javafx.util.Duration

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TrailParticleComponent(t: Texture) : ParticleComponent(ParticleEmitters.newSparkEmitter()) {

    init {
        emitter.blendMode = BlendMode.SRC_OVER
        emitter.isAllowParticleRotation = true
        emitter.setSourceImage(t)
        emitter.emissionRate = 0.4
        emitter.numParticles = 1
        emitter.setSize(t.width / 2, t.width / 2)
        emitter.setVelocityFunction { Point2D.ZERO }
        emitter.setScaleFunction { Point2D.ZERO }
        emitter.setAccelerationFunction { Point2D.ZERO }
        emitter.setSpawnPointFunction { entity.boundingBoxComponent.centerLocal.subtract(t.width / 2, t.width / 2) }
        emitter.setExpireFunction { Duration.millis(150.0) }
    }
}