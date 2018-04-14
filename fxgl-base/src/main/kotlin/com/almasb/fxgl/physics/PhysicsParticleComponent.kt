/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.particle.ParticleComponent
import com.almasb.fxgl.physics.box2d.particle.ParticleGroup
import com.almasb.fxgl.physics.box2d.particle.ParticleGroupDef
import javafx.scene.paint.Color

/**
 * Adds physics particle properties to an entity.
 * By setting the definition each property can be fine-tuned.
 *
 * The difference between physics and normal particle entity is that
 * the former is managed by the physics world, the latter
 * by the particle emitters.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PhysicsParticleComponent : ParticleComponent() {

    /**
     * Do NOT set directly.
     */
    var group: ParticleGroup? = null

    private val physicsWorld = FXGL.getApp().physicsWorld

    private val radiusMeters = physicsWorld.jBox2DWorld.particleSystem.particleRadius
    private val radiusPixels = physicsWorld.toPixels(radiusMeters.toDouble())

    var definition = ParticleGroupDef()
    var color = Color.BLACK

    override fun onUpdate(tpf: Double) {
        if (group == null)
            return

        entity.setPosition(0.0, 0.0)

        particles.forEach { entity.view.removeNode(it.view) }
        this.particles.clear()

        val centers = physicsWorld.jBox2DWorld.particleSystem.particlePositionBuffer

        val pointPhysics = Pools.obtain(Vec2::class.java)

        for (i in group!!.bufferIndex until group!!.bufferIndex + group!!.particleCount) {
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