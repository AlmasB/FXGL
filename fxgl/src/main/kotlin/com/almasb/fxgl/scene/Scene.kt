/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Scene {

    val input = Input()
    val timer = Timer()

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

//        for (i in listeners.indices) {
//            listeners.get(i).onEnter(prevState)
//        }
    }

    internal fun update(tpf: Double) {
        input.update(tpf)
        timer.update(tpf)
        onUpdate(tpf)

//        for (i in listeners.indices) {
//            listeners.get(i).onUpdate(tpf)
//        }
    }

    internal fun exit() {
        onExit()
        input.clearAll()

//        for (i in listeners.indices) {
//            listeners.get(i).onExit()
//        }
    }
}