/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.particle

import com.almasb.fxgl.core.collection.UnorderedArray
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.component.Component

/**
 * Allows adding particle effects to an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
open class ParticleComponent(val emitter: ParticleEmitter) : Component() {

    var onFinished: Runnable = EmptyRunnable

    /**
     * This is the entity whose view is used to render particles.
     * Use of extra entity allows to render particles independently from
     * the entity to which this component is attached. Otherwise, the entire
     * set of emitted particles will be moved based on entity's view.
     */
    private val parent = Entity()

    private val particles = UnorderedArray<Particle>(256)

    override fun onUpdate(tpf: Double) {
        if (parent.world == null) {
            entity.world.addEntity(parent)
        }

        particles.addAll(emitter.emit(entity.x, entity.y))

        val iter = particles.iterator()
        while (iter.hasNext()) {
            val p = iter.next()

            if (p.update(tpf)) {
                iter.remove()

                parent.viewComponent.removeChild(p.view)
                Pools.free(p)
            } else {
                if (p.view.parent == null)
                    parent.viewComponent.addChild(p.view)
            }
        }

        if (particles.isEmpty && emitter.isFinished) {
            onFinished.run();
        }
    }

    override fun onRemoved() {
        particles.forEach { Pools.free(it) }
        particles.clear()

        parent.removeFromWorld()
    }
}