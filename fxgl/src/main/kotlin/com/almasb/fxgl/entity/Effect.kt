/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import javafx.util.Duration


/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Effect(duration: Duration) {

    private val duration = duration.toSeconds()
    private var t = 0.0

    var isFinished = false
        private set

    abstract fun onStart(entity: Entity)

    abstract fun onEnd(entity: Entity)

    fun start(entity: Entity) {
        t = 0.0
        onStart(entity)
    }

    internal fun onUpdate(tpf: Double) {
        if (isFinished)
            return

        t += tpf

        if (t >= duration) {
            isFinished = true
        }
    }
}