/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.app.MainWindow
import com.almasb.fxgl.core.View
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.dsl.FXGL.Companion.getAppHeight
import com.almasb.fxgl.dsl.FXGL.Companion.getAppWidth
import com.almasb.fxgl.dsl.FXGL.Companion.getSettings
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.EntityWorldListener
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.components.ViewComponent
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.ui.UI
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.robot.Robot
import javafx.scene.shape.Rectangle
import javafx.scene.transform.Rotate
import javafx.scene.transform.Scale
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

    private val updatableViews = arrayListOf<View>()

    val entitySelectionRectangle: EntitySelectionRectangle by lazy {
        EntitySelectionRectangle(this)
    }

    /**
     * If set to true, Game Scene will require calling step()
     * to advance each frame.
     */
    var isSingleStep = false

    val camera3D by lazy { Camera3D() }

    private val mouseWarper by lazy {
        Async.startAsyncFX(Callable { MouseWarper(window) }).await()
    }

    var isMouseGrabbed = false

    /**
     * If set to true, mouse movements will rotate the 3D camera as if it's an FPS camera.
     * Changing this value will also set [isMouseGrabbed] to the same value.
     */
    var isFPSCamera = false
        set(value) {
            field = value
            isMouseGrabbed = value

            if (value) {
                Async.startAsyncFX {
                    mouseWarper.warpToCenter()
                }
            }
        }

    private var lastMouseX = 0.0
    private var lastMouseY = 0.0

    init {
        contentRoot.children.addAll(
                if (is3D) make3DSubScene(width.toDouble(), height.toDouble()) else gameRoot,
                uiRoot
        )

        if (is3D) {
            val mouse3DHandler = EventHandler<MouseEvent> {
                if (isMouseGrabbed) {
                    // ignore warp mouse events
                    if (it.screenX.toInt() == mouseWarper.warpScreenX.toInt() && it.screenY.toInt() == mouseWarper.warpScreenY.toInt()) {
                        lastMouseX = it.screenX - window.x
                        lastMouseY = it.screenY - window.y

                        return@EventHandler
                    }
                }

                if (!isFPSCamera)
                    return@EventHandler

                val mouseX = it.screenX - window.x
                val mouseY = it.screenY - window.y

                val offsetX = mouseX - lastMouseX
                val offsetY = mouseY - lastMouseY

                // TODO: extract 100 and 0.5?
                if (FXGLMath.abs(offsetX) < 100 && FXGLMath.abs(offsetY) < 100) {
                    val mouseSensitivity = getSettings().mouseSensitivity

                    // only rotate if > 0.5 pixels
                    if (FXGLMath.abs(offsetX) > 0.5) {
                        if (mouseX > lastMouseX) {
                            camera3D.transform.lookRightBy(mouseSensitivity * (mouseX - lastMouseX))
                        } else if (mouseX < lastMouseX) {
                            camera3D.transform.lookLeftBy(mouseSensitivity * (lastMouseX - mouseX))
                        }
                    }

                    if (FXGLMath.abs(offsetY) > 0.5) {
                        if (mouseY > lastMouseY) {
                            val angle = mouseSensitivity * (mouseY - lastMouseY)

                            // use 85, rather than 90, to avoid potential rounding errors
                            if (camera3D.isOverRotationXAllowed || camera3D.transform.rotationX - angle > -85) {
                                camera3D.transform.lookDownBy(angle)
                            }

                        } else if (mouseY < lastMouseY) {
                            val angle = mouseSensitivity * (lastMouseY - mouseY)

                            if (camera3D.isOverRotationXAllowed || camera3D.transform.rotationX + angle < 85) {
                                camera3D.transform.lookUpBy(angle)
                            }
                        }
                    }
                }

                lastMouseX = mouseX
                lastMouseY = mouseY
            }

            input.addEventHandler(MouseEvent.MOUSE_MOVED, mouse3DHandler)
            input.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouse3DHandler)
        }

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

        // update UI nodes
        updatableViews.forEach { it.onUpdate(tpf) }

        if (!is3D && isZSortingNeeded) {
            sortZ()
            isZSortingNeeded = false
        }

        if (is3D) {
            camera3D.update(tpf)
        }

        // TODO: extract 10?
        if (isMouseGrabbed && window.isFocused) {
            if (input.mouseXUI < 10) {
                mouseWarper.warpToCenter()
            } else if (input.mouseXUI + 10 > getAppWidth()) {
                mouseWarper.warpToCenter()
            } else if (input.mouseYUI < 10) {
                mouseWarper.warpToCenter()
            } else if (input.mouseYUI + 10 > getAppHeight()) {
                mouseWarper.warpToCenter()
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

        if (node is View)
            updatableViews += node
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
     * @param node node to remove
     */
    fun removeUINode(node: Node) {
        uiRoot.children.remove(node)

        if (node is View)
            updatableViews -= node
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
        updatableViews.clear()
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

        entitySelectionRectangle.lastSelection.clear()

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

    private var isCursorInvisibleNeeded = false

    override fun onEnteredFrom(prevState: Scene) {
        if (isCursorInvisibleNeeded) {
            setCursorInvisible()
        }
    }

    override fun onExitingTo(nextState: Scene) {
        isCursorInvisibleNeeded = isCursorInvisible

        if (isCursorInvisibleNeeded) {
            val newCursor = preInvisibleCursor ?: window.defaultCursor

            newCursor?.let {
                setCursor(it)
            }
        }
    }

    /**
     * Must be constructed and used only on JavaFX App Thread.
     */
    private inner class MouseWarper(val window: MainWindow) {
        private val robot = Robot()

        var warpScreenX = 0.0
        var warpScreenY = 0.0

        /**
         * Warps the mouse cursor to window center.
         */
        fun warpToCenter() {
            warpScreenX = window.x + window.width / 2.0
            warpScreenY = window.y + window.height / 2.0

            robot.mouseMove(warpScreenX, warpScreenY)
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

class EntitySelectionRectangle(
    private val gameScene: GameScene
) : Rectangle(), View {

    private val world = gameScene.gameWorld
    private val input = gameScene.input

    /**
     * This list is populated on [stopSelection].
     */
    val lastSelection: MutableList<Entity> = arrayListOf()

    private var selectionStart: Point2D = Point2D.ZERO

    init {
        fill = Color.web("darkblue", 0.4)
        stroke = Color.LIGHTBLUE
        isVisible = false
    }

    fun startSelection() {
        if (scene == null) {
            gameScene.addUINode(this)
        }

        selectionStart = input.mousePositionUI
        translateX = selectionStart.x
        translateY = selectionStart.y
        width = 0.0
        height = 0.0

        isVisible = true
    }

    fun stopSelection() {
        isVisible = false

        lastSelection.clear()

        lastSelection.addAll(
            world.getEntitiesInRange(Rectangle2D(translateX, translateY, width, height))
        )
    }

    override fun onUpdate(tpf: Double) {
        if (isVisible) {
            val dx = input.mouseXUI - selectionStart.x
            val dy = input.mouseYUI - selectionStart.y

            if (dx > 0) {
                width = dx
            } else {
                translateX = input.mouseXUI
                width = -dx
            }

            if (dy > 0) {
                height = dy
            } else {
                translateY = input.mouseYUI
                height = -dy
            }
        }
    }

    override fun getNode(): Node {
        return this
    }

    override fun dispose() { }
}