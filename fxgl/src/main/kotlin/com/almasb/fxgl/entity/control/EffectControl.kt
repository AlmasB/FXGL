/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.Control
import com.almasb.fxgl.entity.Effect
import com.almasb.fxgl.entity.Entity

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EffectControl : Control() {

    private val effectTypes = arrayListOf<Class<in Effect>>()
    private val effects: MutableList<Effect> = arrayListOf()

    override fun onUpdate(entity: Entity, tpf: Double) {
        val iterator = effects.iterator()
        while (iterator.hasNext()) {
            val effect = iterator.next()

            // we use app tpf because this entity may be under TimeComponent effect
            // which means effect computation will be affected too and so we avoid that
            effect.onUpdate(FXGL.getApp().tpf())

            if (effect.isFinished) {
                iterator.remove()
                effectTypes.remove(effect.javaClass)
                effect.onEnd(entity)
            }
        }
    }

    /**
     * If the effect class is the same, the new effect overrides
     * the old one.
     */
    fun startEffect(effect: Effect) {
        if (effectTypes.contains(effect.javaClass)) {
            val oldEffect = effects.find { it.javaClass == effect.javaClass }!!
            oldEffect.onEnd(entity)
            effects.remove(oldEffect)
        } else {
            effectTypes.add(effect.javaClass)
        }

        effects.add(effect)
        effect.start(entity)
    }

    fun endEffect(effect: Effect) {
        effectTypes.remove(effect.javaClass)

        effects.remove(effect)
        effect.onEnd(entity)
    }
}