/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.effect.ParticleControl
import com.almasb.fxgl.physics.box2d.particle.ParticleGroup
import javafx.geometry.Point2D
import javafx.scene.paint.Color

/**
 * The difference between physics and normal particle entity is that
 * the former is managed (controlled) by the physics world, the latter
 * by the particle emitters.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PhysicsParticleControl(private val group: ParticleGroup,
                             private val color: Color,
                             private val physicsWorld: PhysicsWorld) : ParticleControl() {

    private var radiusMeters: Double
    private var radiusPixels: Double

    init {
        radiusMeters = physicsWorld.jBox2DWorld.particleSystem.getParticleRadius().toDouble()
        radiusPixels = physicsWorld.toPixels(radiusMeters).toDouble()
    }

    override fun onUpdate(entity: Entity, tpf: Double) {
        this.particles.clear()

        val centers = physicsWorld.jBox2DWorld.particleSystem.getParticlePositionBuffer()

        for (i in group.bufferIndex..group.bufferIndex + group.particleCount - 1) {
            val center = centers[i]

            val x = physicsWorld.toPixels(center.x - radiusMeters).toDouble()
            val y = physicsWorld.toPixels(physicsWorld.toMeters(physicsWorld.appHeight.toDouble()).toDouble() - center.y.toDouble() - radiusMeters).toDouble()

            this.particles.add(PhysicsParticle(Point2D(x, y), radiusPixels, color))
        }
    }

    override fun onRemoved(entity: Entity) {
        physicsWorld.jBox2DWorld.destroyParticlesInGroup(group)
        super.onRemoved(entity)
    }
}