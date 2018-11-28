/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityView
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.entity.components.ViewComponent
import com.almasb.fxgl.ui.FontType
import com.almasb.fxgl.ui.UI
import com.almasb.sslogger.Logger
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import java.util.*

/**
 * Represents the scene that shows entities on the screen during "play" mode.
 * Contains 2 layers. From bottom to top:
 *
 *  Entities and their render layers (game view)
 *  UI Overlay
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class GameScene
internal constructor(width: Int, height: Int) : FXGLScene(width, height), EntityWorldListener {

    companion object {

        private val log = Logger.get(GameScene::class.java)
    }

    /**
     * Root for entity views, it is affected by viewport movement.
     */
    private val gameRoot = Group()

    /**
     * The overlay root above [.gameRoot]. Contains UI elements, native JavaFX nodes.
     * uiRoot isn't affected by viewport movement.
     */
    private val uiRoot = Group()

    val profilerText = Text()


    private val entities = ArrayList<Entity>()


    private val debugPositions = ObjectMap<Entity, EntityView>()


    /**
     * @return unmodifiable list of UI nodes
     */
    val uiNodes: ObservableList<Node>
        get() = uiRoot.childrenUnmodifiable

    init {

        contentRoot.children.addAll(gameRoot, uiRoot)

        if (FXGL.getSettings().isProfilingEnabled) {
            initProfilerText(0.0, (height - 120).toDouble())
        }

        initViewport(width.toDouble(), height.toDouble())

        addDebugListener()

        log.debug("Game scene initialized: " + width + "x" + height)
    }

    private fun initProfilerText(x: Double, y: Double) {
        profilerText.font = FXGL.getUIFactory().newFont(FontType.MONO, 20.0)
        profilerText.fill = Color.RED
        profilerText.translateX = x
        profilerText.translateY = y

        uiRoot.children.add(profilerText)
    }

    private fun initViewport(w: Double, h: Double) {
        val viewport = viewport
        gameRoot.layoutXProperty().bind(viewport.xProperty().negate())
        gameRoot.layoutYProperty().bind(viewport.yProperty().negate())

        val scale = Scale()
        scale.pivotXProperty().bind(viewport.xProperty())
        scale.pivotYProperty().bind(viewport.yProperty())
        scale.xProperty().bind(viewport.zoomProperty())
        scale.yProperty().bind(viewport.zoomProperty())
        gameRoot.transforms.add(scale)

        val rotate = Rotate(0.0, Rotate.Z_AXIS)
        rotate.pivotXProperty().bind(viewport.xProperty().add(w / 2))
        rotate.pivotYProperty().bind(viewport.yProperty().add(h / 2))
        rotate.angleProperty().bind(viewport.angleProperty().negate())
        gameRoot.transforms.add(rotate)
    }

    private fun addDebugListener() {
        FXGL.getSettings().devShowPosition.addListener { o, prev, show ->
            if (show!!) {
                //forEach<Entity>(FXGL.getGameWorld().entities) { e -> addDebugView(e) }
            }
        }
    }

    private fun addDebugView(e: Entity) {

    }

    /**
     * Add a node to the UI overlay.
     *
     * @param node UI node to add
     */
    fun addUINode(node: Node) {
        uiRoot.children.add(node)
    }

    /**
     * Add nodes to the UI overlay.
     *
     * @param nodes UI nodes to add
     */
    fun addUINodes(vararg nodes: Node) {
        for (node in nodes)
            addUINode(node)
    }

    /**
     * Remove given node from the UI overlay.
     *
     * @param n node to remove
     * @return true iff the node has been removed
     */
    fun removeUINode(n: Node): Boolean {
        return uiRoot.children.remove(n)
    }

    /**
     * Remove nodes from the UI overlay.
     *
     * @param nodes nodes to remove
     */
    fun removeUINodes(vararg nodes: Node) {
        for (node in nodes)
            removeUINode(node)
    }

    fun addUI(ui: UI) {
        addUINode(ui.root)
    }

    fun removeUI(ui: UI) {
        removeUINode(ui.root)
    }

    /**
     * Removes all nodes from the game view layer.
     */
    fun clearGameViews() {
        gameRoot.children.clear()
    }

    /**
     * Removes all nodes from the UI overlay.
     */
    fun clearUINodes() {
        uiRoot.children.clear()
    }

    /**
     * Set true if UI elements should forward mouse events
     * to the game layer.
     *
     * @param b flag
     * @defaultValue false
     */
    fun setUIMouseTransparent(b: Boolean) {
        uiRoot.isMouseTransparent = b
    }

    private fun sortZ() {
        val tmpE = ArrayList(entities)
        tmpE.sortBy { it.z }

        gameRoot.children.setAll(
                tmpE.map { e -> e.viewComponent.parent }
        )
    }

    fun onUpdate(tpf: Double) {
        viewport.onUpdate(tpf)
    }

    /**
     * Unbinds viewport, clears game views and UI nodes.
     */
    fun clear() {
        log.debug("Clearing game scene")

        viewport.unbind()
        gameRoot.children.clear()
        uiRoot.children.setAll(profilerText)
    }

    override fun onEntityAdded(entity: Entity) {
        entities.add(entity)
        initView(entity.viewComponent)

        if (FXGL.getSettings().devShowPosition.value!!) {
            addDebugView(entity)
        }
    }

    override fun onEntityRemoved(entity: Entity) {
        entities.remove(entity)
        destroyView(entity.viewComponent)

    }

    private fun initView(viewComponent: ViewComponent) {

    }

    private fun destroyView(viewComponent: ViewComponent) {}
}