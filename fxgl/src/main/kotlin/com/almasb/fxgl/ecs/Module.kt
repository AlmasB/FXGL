/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class Module {

    var entity: Entity? = null
        internal set

    open fun onAdded(entity: Entity) {

    }

    open fun onRemoved(entity: Entity) {

    }
}

abstract class Component : Module() {

}

abstract class Control : Module() {

    abstract fun onUpdate(entity: Entity, tpf: Double)

    private var isPaused = false

    fun isPaused(): Boolean {
        return isPaused
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }
}