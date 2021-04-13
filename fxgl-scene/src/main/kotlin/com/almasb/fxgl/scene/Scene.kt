/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.core.Updatable
import com.almasb.fxgl.core.UpdatableRunner
import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.core.fsm.State
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer
import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

abstract class SubScene : Scene() {

    override final val isSubState: Boolean = true
}

abstract class Scene : State<Scene>, UpdatableRunner {

    /**
     * Top-level root node. *Only* used by FXGL itself.
     * Do NOT access this, use [contentRoot] instead.
     */
    val root = Pane()

    /**
     * Root node for content. All children of this class
     * should use content root and must not access [root].
     */
    val contentRoot = Pane()

    /**
     * Input specific to this scene.
     * It only receives events if this scene is active.
     */
    val input = Input()

    /**
     * Timer specific to this scene.
     * It only runs if this scene is active.
     */
    val timer = Timer()

    override val isSubState: Boolean = false
    override val isAllowConcurrency: Boolean = false

    private val listeners = Array<Updatable>()
    private val listenersToAdd = Array<Updatable>()
    private val listenersToRemove = Array<Updatable>()

    init {
        root.background = null
        contentRoot.background = null

        root.children.addAll(contentRoot)
    }

    /**
     * Add [node] to this scene.
     */
    fun addChild(node: Node) {
        contentRoot.children += node
    }

    /**
     * Remove [node] from this scene.
     */
    fun removeChild(node: Node) {
        contentRoot.children -= node
    }

    /**
     * Add an update listener, which is notified when this scene updates.
     */
    override fun addListener(l: Updatable) {
        listenersToAdd.add(l)
    }

    /**
     * Remove a previously added listener.
     */
    override fun removeListener(l: Updatable) {
        listenersToRemove.add(l)
    }

    /**
     * Update this scene, which updates (in this order):
     * input, timer, onUpdate() callback, listeners.
     */
    fun update(tpf: Double) {
        input.update(tpf)
        timer.update(tpf)
        onUpdate(tpf)

        listeners.addAll(listenersToAdd)
        listeners.removeAllByIdentity(listenersToRemove)

        listenersToAdd.clear()
        listenersToRemove.clear()

        listeners.forEach { it.onUpdate(tpf) }
    }

    /**
     * Binds the scene size to given properties.
     * [scaledWidth] and [scaledHeight] are the values in pixels this scene (the root) will be drawn at.
     * [scaleRatioX] and [scaleRatioY] are the ratios by which to scale the content root inside the scene.
     */
    open fun bindSize(scaledWidth: DoubleProperty, scaledHeight: DoubleProperty, scaleRatioX: DoubleProperty, scaleRatioY: DoubleProperty) {
        root.prefWidthProperty().bind(scaledWidth)
        root.prefHeightProperty().bind(scaledHeight)

        val scale = Scale()
        scale.xProperty().bind(scaleRatioX)
        scale.yProperty().bind(scaleRatioY)
        contentRoot.transforms.setAll(scale)
    }

    protected open fun onUpdate(tpf: Double) { }

    override fun onCreate() { }
    override fun onDestroy() { }
    override fun onEnteredFrom(prevState: Scene) { }
    override fun onExitingTo(nextState: Scene) { }

    override fun toString(): String = javaClass.simpleName
}

