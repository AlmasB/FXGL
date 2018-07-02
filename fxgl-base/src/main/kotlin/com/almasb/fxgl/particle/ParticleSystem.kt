/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle

import javafx.geometry.Point2D
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * A particle system to use when not in game,
 * e.g. in menus, intro, UI, etc.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ParticleSystem {

    val pane = Pane()

    private val emitters = hashMapOf<ParticleEmitter, Point2D>()
    private val particles = hashMapOf<ParticleEmitter, MutableList<Particle>>()

    init {

    }

    fun addParticleEmitter(emitter: ParticleEmitter, x: Double, y: Double) {
        emitters[emitter] = Point2D(x, y)
        particles[emitter] = arrayListOf()
    }

    fun removeParticleEmitter(emitter: ParticleEmitter) {
        emitters.remove(emitter)
        particles.remove(emitter)
    }

    fun onUpdate(tpf: Double) {
        emitters.forEach { emitter, p ->
            val particlesList = particles[emitter]!!

            particlesList.addAll(emitter.emit(p.x, p.y))

            val iter = particlesList.iterator()
            while (iter.hasNext()) {
                val particle = iter.next()

                if (particle.update(tpf)) {
                    iter.remove()

                    pane.children.remove(particle.view)
                } else {
                    if (particle.getView().getParent() == null)
                        pane.children.add(particle.view)
                }
            }
        }


//

//
//        if (particles.isEmpty() && emitter.isFinished()) {
//            onFinished.run();
//        }
//    }
//
//    @Override
//    public void onRemoved() {
//        forEach(particles, Pools::free);
//        particles.clear();
//
//        parent.removeFromWorld();
//    }
//
//    public final void setOnFinished(Runnable onFinished) {
//        this.onFinished = onFinished;
//    }
    }
}