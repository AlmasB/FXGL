/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.effect

import com.almasb.fxgl.entity.Entity
import javafx.util.Duration

/**
 * Stateful temporary effect to be applied to an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Effect(duration: Duration) {

    private val duration = duration.toSeconds()
    private var t = 0.0

    private lateinit var entity: Entity

    var isFinished = false
        private set

    abstract fun onStart(entity: Entity)

    open fun onUpdate(entity: Entity, tpf: Double) {}

    abstract fun onEnd(entity: Entity)

    fun start(entity: Entity) {
        this.entity = entity

        t = 0.0
        onStart(entity)
    }

    internal fun onUpdate(tpf: Double) {
        if (isFinished)
            return

        onUpdate(entity, tpf)
        t += tpf

        if (t >= duration) {
            isFinished = true
        }
    }
}