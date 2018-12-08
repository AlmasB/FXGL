/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Scene {

    val input = Input()
    val timer = Timer()

    private val listeners = CopyOnWriteArrayList<SceneListener>()

    fun addListener(l: SceneListener) {
        listeners += l
    }

    fun removeListener(l: SceneListener) {
        listeners -= l
    }

    /**
     * Called after entering this state from prevState
     */
    protected open fun onEnter(prevState: Scene) {

    }

    /**
     * Called before exit.
     */
    protected open fun onExit() {

    }

    protected open fun onUpdate(tpf: Double) {

    }

    internal fun enter(prevState: Scene) {
        onEnter(prevState)
    }

    internal fun update(tpf: Double) {
        input.update(tpf)
        timer.update(tpf)
        onUpdate(tpf)

        listeners.forEach { it.onUpdate(tpf) }
    }

    internal fun exit() {
        onExit()
        input.clearAll()
    }
}