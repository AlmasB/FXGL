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