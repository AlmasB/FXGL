/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle

import com.almasb.fxgl.core.Updatable
import com.almasb.fxgl.core.pool.Pools
import javafx.geometry.Point2D
import javafx.scene.layout.Pane

/**
 * A particle system to use when not in game,
 * e.g. in menus, intro, UI, etc.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ParticleSystem : Updatable {

    val pane = Pane()

    private val emitters = hashMapOf<ParticleEmitter, Point2D>()
    private val particles = hashMapOf<ParticleEmitter, MutableList<Particle>>()

    fun addParticleEmitter(emitter: ParticleEmitter, x: Double, y: Double) {
        emitters[emitter] = Point2D(x, y)
        particles[emitter] = arrayListOf()
    }

    fun removeParticleEmitter(emitter: ParticleEmitter) {
        emitters.remove(emitter)
        particles.remove(emitter)?.let { it.forEach { Pools.free(it) } }
    }

    override fun onUpdate(tpf: Double) {
        emitters.forEach { (emitter, p) ->
            val particlesList = particles[emitter]!!

            particlesList.addAll(emitter.emit(p.x, p.y))

            val iter = particlesList.iterator()
            while (iter.hasNext()) {
                val particle = iter.next()

                if (particle.update(tpf)) {
                    iter.remove()

                    pane.children.remove(particle.view)
                    Pools.free(p)
                } else {
                    if (particle.view.parent == null)
                        pane.children.add(particle.view)
                }
            }
        }
    }
}