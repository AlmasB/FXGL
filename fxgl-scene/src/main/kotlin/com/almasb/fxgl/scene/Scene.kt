/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.core.fsm.State
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer
import javafx.beans.property.DoubleProperty
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.transform.Scale
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface SubSceneStack {
    fun pushSubScene(subScene: SubScene)

    fun popSubScene()
}

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

    private val listeners = CopyOnWriteArrayList<SceneListener>()

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
        listeners += l
    }

    fun removeListener(l: SceneListener) {
        listeners -= l
    }

    fun update(tpf: Double) {
        input.update(tpf)
        timer.update(tpf)
        onUpdate(tpf)

        listeners.forEach { it.onUpdate(tpf) }
    }

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

