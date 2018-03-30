/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.particle.ParticleControl
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.physics.box2d.particle.ParticleGroup
import javafx.scene.paint.Color

/**
 * The difference between physics and normal particle entity is that
 * the former is managed by the physics world, the latter
 * by the particle emitters.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PhysicsParticleComponent(private val group: ParticleGroup,
                               private val color: Color,
                               private val physicsWorld: PhysicsWorld) : ParticleControl() {

    private val radiusMeters = physicsWorld.jBox2DWorld.particleSystem.particleRadius
    private val radiusPixels = physicsWorld.toPixels(radiusMeters.toDouble())

    override fun onUpdate(tpf: Double) {
        entity.setPosition(0.0, 0.0)

        particles.forEach { entity.view.removeNode(it.view) }
        this.particles.clear()

        val centers = physicsWorld.jBox2DWorld.particleSystem.particlePositionBuffer

        val pointPhysics = Pools.obtain(Vec2::class.java)

        for (i in group.bufferIndex until group.bufferIndex + group.particleCount) {
            val center = centers[i]
            pointPhysics.set(center.x - radiusMeters, center.y + radiusMeters)

            val pointPixels = physicsWorld.toPoint(pointPhysics)

            val particle = PhysicsParticle(pointPixels, radiusPixels, color)

            this.particles.add(particle)
            entity.view.addNode(particle.view)
        }

        Pools.free(pointPhysics)
    }

    override fun onRemoved() {
        physicsWorld.jBox2DWorld.destroyParticlesInGroup(group)
        super.onRemoved()
    }
}