/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer
import javafx.scene.layout.Pane
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class Scene {

    /**
     * Top-level root node.
     */
    val root = Pane()

    /**
     * Root node for content. All children of this class
     * should use content root.
     */
    val contentRoot = Pane()

    val input = Input()
    val timer = Timer()

    private val listeners = CopyOnWriteArrayList<SceneListener>()

    init {
        root.background = null
        contentRoot.background = null

        root.children.addAll(contentRoot)
    }

    fun addListener(l: SceneListener) {
        listeners += l
    }

    fun removeListener(l: SceneListener) {
        listeners -= l
    }

    /**
     * Called after entering this state from prevState.
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

    override fun toString(): String = javaClass.simpleName
}