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

    // TODO: internal set does not work
    var entity: Entity? = null

    open fun onAdded(entity: Entity) {}

    open fun onRemoved(entity: Entity) {}

    fun isComponent() = this is Component

    fun isControl() = this is Control
}

abstract class Component : Module()

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

interface ModuleListener<in T : Module> {

    fun onAdded(module: T) {}

    fun onRemoved(module: T) {}
}

interface ComponentListener : ModuleListener<Component>

interface ControlListener : ModuleListener<Control>