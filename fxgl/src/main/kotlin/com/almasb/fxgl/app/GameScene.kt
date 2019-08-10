/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.components.ViewComponent
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.ui.UI
import com.almasb.sslogger.Logger
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale

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
internal constructor(width: Int, height: Int,
                     val gameState: GameState,
                     val gameWorld: GameWorld,
                     val physicsWorld: PhysicsWorld) : FXGLScene(width, height), EntityWorldListener {

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

    private val entities = ArrayList<Entity>()

    private var isZSortingNeeded = false

    /**
     * @return unmodifiable list of UI nodes
     */
    val uiNodes: ObservableList<Node>
        get() = uiRoot.childrenUnmodifiable

    /**
     * If set to true, Game Scene will require calling step()
     * to advance each frame.
     */
    var isSingleStep = false

    init {
        contentRoot.children.addAll(gameRoot, uiRoot)

        initViewport(width.toDouble(), height.toDouble())

        contentRoot.children.add(viewport.camera.viewComponent.parent)

        log.debug("Game scene initialized: " + width + "x" + height)

        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(this)
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

    override fun onUpdate(tpf: Double) {
        // if single step is configured, then step() will be called manually
        if (isSingleStep)
            return

        step(tpf)
    }

    fun step(tpf: Double) {
        gameWorld.onUpdate(tpf)
        physicsWorld.onUpdate(tpf)
        viewport.onUpdate(tpf)

        if (isZSortingNeeded) {
            sortZ()
            isZSortingNeeded = false
        }
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
        // it is important to sort in a different list since gameRoot is part of active scene graph
        // and does not allow duplicates that may occur during sorting
        val tmp = ArrayList(gameRoot.children)
        tmp.sortBy { (it.properties["viewData"] as GameView).z }

        gameRoot.children.setAll(tmp)
    }

    /**
     * Unbinds viewport, clears game views and UI nodes.
     */
    fun clear() {
        log.debug("Clearing game scene")

        viewport.unbind()
        gameRoot.children.clear()
        uiRoot.children.clear()
    }

    override fun onEntityAdded(entity: Entity) {
        entities.add(entity)
        initView(entity.viewComponent)
    }

    override fun onEntityRemoved(entity: Entity) {
        entities.remove(entity)
        destroyView(entity.viewComponent)
    }

    fun addGameView(view: GameView) {
        view.node.properties["viewData"] = view

        gameRoot.children.add(view.node)

        isZSortingNeeded = true
    }

    fun removeGameView(view: GameView) {
        view.node.properties.clear()

        gameRoot.children.remove(view.node)
    }

    private fun initView(viewComponent: ViewComponent) {
        val view = GameView(viewComponent.parent, viewComponent.z.value)
        view.zProperty.bind(viewComponent.z)

        addGameView(view)
    }

    private fun destroyView(viewComponent: ViewComponent) {
        // can we find a neater way to store node <-> z value pairings
        // since properties[] is null e.g. for particle views
        viewComponent.parent.properties["viewData"]?.let {
            val view = it as GameView
            view.zProperty.unbind()

            removeGameView(view)
        }
    }
}

class GameView(val node: Node, zIndex: Int) {
    val zProperty = SimpleIntegerProperty(zIndex)

    var z: Int
        get() = zProperty.value
        set(value) {
            zProperty.value = value
        }
}