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
class ParticleComponent(val emitter: ParticleEmitter) : Component() {

    var onFinished: Runnable = EmptyRunnable

    // TODO: find a way to not use extra entity
    // may involve having a separate parent in entity view component that
    // does not scale / rotate
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

                parent.viewComponent.parent.children -= p.view
                Pools.free(p)
            } else {
                if (p.view.parent == null)
                    parent.viewComponent.parent.children += p.view
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