/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

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

abstract class Scene : State<Scene> {

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

    override val isSubState: Boolean = false
    override val isAllowConcurrency: Boolean = false

    private val listeners = Array<SceneListener>()
    private val listenersToAdd = Array<SceneListener>()
    private val listenersToRemove = Array<SceneListener>()

    init {
        root.background = null
        contentRoot.background = null

        root.children.addAll(contentRoot)
    }

    fun addChild(node: Node) {
        contentRoot.children += node
    }

    fun removeChild(node: Node) {
        contentRoot.children -= node
    }

    fun addListener(l: SceneListener) {
        listenersToAdd.add(l)
    }

    fun removeListener(l: SceneListener) {
        listenersToRemove.add(l)
    }

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

