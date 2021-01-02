/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.dsl.FXGL.Companion.getAppHeight
import com.almasb.fxgl.dsl.FXGL.Companion.getAppWidth
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.components.ViewComponent
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.ui.UI
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.robot.Robot
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
import javafx.stage.Screen
import java.util.concurrent.Callable

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
@JvmOverloads
internal constructor(width: Int, height: Int,
                     val gameWorld: GameWorld,
                     val physicsWorld: PhysicsWorld,
                     private val is3D: Boolean = false) : FXGLScene(width, height), EntityWorldListener {

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

    private val zChangeListener = ChangeListener<Number> { _, _, _ ->
        isZSortingNeeded = true
    }

    /**
     * @return unmodifiable list of UI nodes
     */
    @get:JvmName("getUINodes")
    val uiNodes: ObservableList<Node>
        get() = uiRoot.childrenUnmodifiable

    /**
     * If set to true, Game Scene will require calling step()
     * to advance each frame.
     */
    var isSingleStep = false

    val camera3D by lazy { Camera3D() }

    private val mouseWarper by lazy {
        Async.startAsyncFX(Callable { MouseWarper() }).await()
    }

    var isMouseGrabbed = false

    init {
        contentRoot.children.addAll(
                if (is3D) make3DSubScene(width.toDouble(), height.toDouble()) else gameRoot,
                uiRoot
        )

        initViewport(width.toDouble(), height.toDouble())

        contentRoot.children.add(viewport.camera.viewComponent.parent)

        log.debug("Game scene initialized: " + width + "x" + height)

        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(this)
    }

    private fun make3DSubScene(w: Double, h: Double): SubScene {
        val scene3D = SubScene(gameRoot, w, h, true, SceneAntialiasing.BALANCED)
        scene3D.camera = camera3D.perspectiveCamera

        return scene3D
    }

    private fun initViewport(w: Double, h: Double) {
        gameRoot.translateXProperty().bind(viewport.xProperty().negate())
        gameRoot.translateYProperty().bind(viewport.yProperty().negate())

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

        if (!is3D && isZSortingNeeded) {
            sortZ()
            isZSortingNeeded = false
        }

        if (is3D) {
            camera3D.update(tpf)
        }

        if (isMouseGrabbed) {
            if (input.mouseXWorld < 10) {
                mouseWarper.warp()
            } else if (input.mouseXWorld + 10 > getAppWidth()) {
                mouseWarper.warp()
            } else if (input.mouseYWorld < 10) {
                mouseWarper.warp()
            } else if (input.mouseYWorld + 10 > getAppHeight()) {
                mouseWarper.warp()
            }
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
     * Resets game world, physics world, game timer.
     * Unbinds viewport, clears game views and UI nodes.
     */
    internal fun reset() {
        log.debug("Clearing game scene")

        gameWorld.reset()

        // re-add listeners since above calls resets everything
        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(this)

        physicsWorld.clear()
        physicsWorld.clearCollisionHandlers()

        timer.clear()

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
        view.zProperty.addListener(zChangeListener)
        view.node.properties["viewData"] = view

        gameRoot.children.add(view.node)

        isZSortingNeeded = true
    }

    fun removeGameView(view: GameView) {
        view.node.properties.clear()

        gameRoot.children.remove(view.node)
    }

    private fun initView(viewComponent: ViewComponent) {
        val view = GameView(viewComponent.parent, viewComponent.zIndexProperty.value)
        view.zProperty.bind(viewComponent.zIndexProperty)

        addGameView(view)
    }

    private fun destroyView(viewComponent: ViewComponent) {
        // can we find a neater way to store node <-> z value pairings
        // since properties[] is null e.g. for particle views
        viewComponent.parent.properties["viewData"]?.let {
            val view = it as GameView
            view.zProperty.unbind()
            view.zProperty.removeListener(zChangeListener)

            removeGameView(view)
        }
    }

    /**
     * Must be constructed and used only on JavaFX App Thread.
     */
    private inner class MouseWarper {
        private val primaryScreen = Screen.getPrimary()

        private val screenCenter = Point2D(primaryScreen.bounds.width / 2.0, primaryScreen.bounds.height / 2.0)

        private val robot = Robot()

        /**
         * Warps the mouse cursor to primary screen center.
         */
        fun warp() {
            log.debug("Warping mouse to: $screenCenter")

            robot.mouseMove(screenCenter)
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