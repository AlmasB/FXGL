/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.app.scene.ErrorSubScene
import com.almasb.fxgl.app.scene.FXGLScene
import com.almasb.fxgl.core.fsm.StateMachine
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.MouseEventData
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SubScene
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.ImageCursor
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.stage.Screen
import javafx.stage.Stage

/**
 * A wrapper around JavaFX primary stage.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
sealed class MainWindow(
        /**
         * The starting scene which is used when the window is created.
         */
        scene: FXGLScene,

        protected val settings: ReadOnlyGameSettings) {

    protected val log = Logger.get(javaClass)

    val currentFXGLSceneProperty = ReadOnlyObjectWrapper<FXGLScene>(scene)
    val currentSceneProperty = ReadOnlyObjectWrapper<Scene>(scene)

    val currentFXGLScene: FXGLScene
        get() = currentFXGLSceneProperty.value

    val currentScene: Scene
        get() = currentSceneProperty.value

    var onClose: (() -> Unit)? = null
    var defaultCursor: ImageCursor? = null

    abstract val x: Double
    abstract val y: Double
    abstract val width: Double
    abstract val height: Double
    abstract val isFocused: Boolean

    protected val scenes = arrayListOf<Scene>()

    protected val eventSubscribers = arrayListOf<SceneEventSubscriber<*>>()

    protected val scaleRatioX: DoubleProperty = SimpleDoubleProperty()
    protected val scaleRatioY: DoubleProperty = SimpleDoubleProperty()
    protected val scaledWidth: DoubleProperty = SimpleDoubleProperty()
    protected val scaledHeight: DoubleProperty = SimpleDoubleProperty()

    protected val stateMachine = StateMachine(scene)

    /**
     * Input that is active in any scene.
     */
    internal val input = Input()

    protected fun setInitialScene(scene: FXGLScene) {
        registerScene(scene)

        currentFXGLSceneProperty.value = scene
        scene.activeProperty().set(true)
        currentSceneProperty.value = scene

        log.debug("Set initial scene to $scene")
    }

    abstract fun iconifiedProperty(): ReadOnlyBooleanProperty

    /**
     * Add desktop taskbar / window icon.
     * Multiple images of different sizes can be added: 16x16, 32x32
     * and most suitable will be chosen.
     * Can only be called before [show].
     */
    abstract fun addIcons(vararg images: Image)

    abstract fun addCSS(vararg cssList: CSS)

    fun isInHierarchy(scene: Scene): Boolean = stateMachine.isInHierarchy(scene)

    fun update(tpf: Double) {
        input.update(tpf)
        stateMachine.runOnActiveStates { it.update(tpf) }
    }

    /**
     * Set current FXGL scene.
     * The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    fun setScene(scene: FXGLScene) {
        popAllSubScenes()

        if (scene !in scenes) {
            registerScene(scene)
        }

        val prevScene = stateMachine.parentState

        stateMachine.changeState(scene)

        if (stateMachine.parentState === prevScene) {
            log.warning("Cannot set to $scene. Probably because subscenes are present.")
            return
        }

        prevScene.input.clearAll()

        currentFXGLSceneProperty.value.activeProperty().set(false)

        currentFXGLSceneProperty.value = scene
        setRoot(scene.root)
        scene.activeProperty().set(true)

        currentSceneProperty.value = scene

        log.debug("$prevScene -> $scene")
    }

    protected abstract fun setRoot(root: Pane)

    fun pushState(newScene: SubScene) {
        log.debug("Push state: $newScene")

        val prevScene = stateMachine.currentState

        stateMachine.changeState(newScene)

        prevScene.input.clearAll()

        // push view to content root, which is correctly offset, scaled etc.
        currentFXGLScene.contentRoot.children.add(newScene.root)

        currentSceneProperty.value = stateMachine.currentState

        log.debug("$prevScene -> ${stateMachine.currentState}")
    }

    fun popState() {
        val prevScene = stateMachine.currentState

        if (!stateMachine.popSubState()) {
            log.warning("Cannot pop substate. Probably because substates are empty!")
            return
        }

        log.debug("Pop state: $prevScene")

        prevScene.input.clearAll()

        // pop view
        currentFXGLScene.contentRoot.children.remove(prevScene.root)

        currentSceneProperty.value = stateMachine.currentState

        log.debug("${stateMachine.currentState} <- $prevScene")
    }

    fun popAllSubScenes() {
        while (currentScene !== currentFXGLScene) {
            popState()
        }
    }

    abstract fun show()

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    private fun registerScene(scene: Scene) {
        scene.bindSize(scaledWidth, scaledHeight, scaleRatioX, scaleRatioY)

        if (!settings.isNative
                && settings.isDesktop
                && scene is FXGLScene
                && scene.root.cursor == null) {
            defaultCursor?.let {
                scene.setCursor(it.image, Point2D(it.hotspotX, it.hotspotY))
            }
        }

        scenes.add(scene)
    }

    protected fun addKeyHandler(fxScene: javafx.scene.Scene, handler: (KeyEvent) -> Unit) {
        val keyHandler: EventHandler<KeyEvent> = EventHandler(handler)

        eventSubscribers.add(SceneEventSubscriber(fxScene, KeyEvent.ANY, keyHandler, isFilter = false))
    }

    protected fun addMouseHandler(fxScene: javafx.scene.Scene, handler: (MouseEventData) -> Unit) {
        val mouseHandler: EventHandler<MouseEvent> = EventHandler {
            val data = MouseEventData(
                    it,
                    Point2D(currentFXGLScene.contentRoot.translateX, currentFXGLScene.contentRoot.translateY),
                    Point2D(currentFXGLScene.viewport.x, currentFXGLScene.viewport.y),
                    currentFXGLScene.viewport.getZoom(),
                    scaleRatioX.value,
                    scaleRatioY.value
            )

            handler(data)
        }

        eventSubscribers.add(SceneEventSubscriber(fxScene, MouseEvent.ANY, mouseHandler, isFilter = false))
    }

    protected fun addGlobalHandler(fxScene: javafx.scene.Scene, filter: (Event) -> Unit, handler: (Event) -> Unit) {
        val rootFilter: EventHandler<Event> = EventHandler(filter)
        val rootHandler: EventHandler<Event> = EventHandler(handler)

        eventSubscribers.add(SceneEventSubscriber(fxScene, EventType.ROOT, rootFilter, isFilter = true))
        eventSubscribers.add(SceneEventSubscriber(fxScene, EventType.ROOT, rootHandler, isFilter = false))
    }

    protected fun removeAllEventFiltersAndHandlers(fxScene: javafx.scene.Scene) {
        eventSubscribers.removeIf {
            val shouldRemove = it.fxScene === fxScene

            if (shouldRemove) {
                it.unsubscribe()
            }

            shouldRemove
        }
    }

    abstract fun takeScreenshot(): Image

    fun showFatalError(error: Throwable, action: Runnable) {
        pushState(ErrorSubScene(settings.width.toDouble(), settings.height.toDouble(), error, action))
    }

    abstract fun close()
}

internal class PrimaryStageWindow(
        /**
         * Primary stage.
         */
        val stage: Stage,
        scene: FXGLScene,
        settings: ReadOnlyGameSettings

) : MainWindow(scene, settings) {

    private val fxScene: javafx.scene.Scene

    override val x: Double
        get() = stage.x

    override val y: Double
        get() = stage.y

    override val width: Double
        get() = stage.width

    override val height: Double
        get() = stage.height

    override val isFocused: Boolean
        get() = stage.isFocused

    init {
        fxScene = createFXScene(scene.root)

        setInitialScene(scene)

        initStage()

        addKeyHandler(fxScene) { e ->
            input.onKeyEvent(e)
            stateMachine.runOnActiveStates { it.input.onKeyEvent(e) }
        }

        addMouseHandler(fxScene) { e ->
            input.onMouseEvent(e)
            stateMachine.runOnActiveStates { it.input.onMouseEvent(e) }
        }

        // reroute any events to current state input
        addGlobalHandler(fxScene,
                { e ->
                    input.fireEventViaFilters(e)
                    stateMachine.runOnActiveStates { it.input.fireEventViaFilters(e) }
                },
                { e ->
                    input.fireEventViaHandlers(e)
                    stateMachine.runOnActiveStates { it.input.fireEventViaHandlers(e) }
                }
        )
    }

    /**
     * Construct the only JavaFX scene with computed size based on user settings.
     */
    private fun createFXScene(root: Parent): javafx.scene.Scene {
        log.debug("Creating a JavaFX scene")

        var newW = settings.width.toDouble()
        var newH = settings.height.toDouble()

        val bounds = if (settings.isFullScreenAllowed) Screen.getPrimary().bounds else Screen.getPrimary().visualBounds

        if (newW > bounds.width || newH > bounds.height) {
            log.debug("Target size > screen size")

            // margin so the window size is slightly smaller than bounds
            // to account for platform-specific window borders
            val extraMargin = 25.0
            val ratio = newW / newH

            for (newWidth in bounds.width.toInt() downTo 1) {
                if (newWidth / ratio <= bounds.height) {
                    newW = newWidth.toDouble() - extraMargin
                    newH = newWidth / ratio
                    break
                }
            }
        }

        // round to a whole number
        newW = newW.toInt().toDouble()
        newH = newH.toInt().toDouble()

        val scene = javafx.scene.Scene(root, newW, newH)

        scaledWidth.set(newW)
        scaledHeight.set(newH)
        scaleRatioX.set(scaledWidth.value / settings.width)
        scaleRatioY.set(scaledHeight.value / settings.height)

        log.debug("Target settings size: ${settings.width.toDouble()} x ${settings.height.toDouble()}")
        log.debug("Scaled scene size:    $newW x $newH")
        log.debug("Scaled ratio: (${scaleRatioX.value}, ${scaleRatioY.value})")

        return scene
    }

    /**
     * Configure main stage based on user settings.
     */
    private fun initStage() {
        with(stage) {
            scene = fxScene

            title = "${settings.title} ${settings.version}"

            isResizable = settings.isManualResizeEnabled

            if (settings.isDesktop) {
                initStyle(settings.stageStyle)
            }

            setOnCloseRequest { e ->
                e.consume()
                onClose?.invoke()
            }

            if (settings.isFullScreenAllowed) {
                fullScreenExitHint = ""
                // don't let the user exit FS mode manually
                fullScreenExitKeyCombination = KeyCombination.NO_MATCH

                settings.fullScreen.addListener { _, _, fullscreenNow ->
                    isFullScreen = fullscreenNow
                }
            }

            sizeToScene()
            centerOnScreen()
        }
    }

    private var windowBorderWidth = 0.0
    private var windowBorderHeight = 0.0

    override fun show() {
        log.debug("Opening main window")

        stage.show()

        // platform offsets
        windowBorderWidth = stage.width - scaledWidth.value
        windowBorderHeight = stage.height - scaledHeight.value

        // this is a hack to estimate platform offsets on ubuntu and potentially other Linux os
        // because for some reason javafx does not create a stage to contain scene of given size
        if (windowBorderHeight < 0.5 && settings.isLinux) {
            windowBorderHeight = 35.0
        }

        scaledWidth.bind(stage.widthProperty().subtract(
                Bindings.`when`(stage.fullScreenProperty()).then(0).otherwise(windowBorderWidth)
        ))
        scaledHeight.bind(stage.heightProperty().subtract(
                Bindings.`when`(stage.fullScreenProperty()).then(0).otherwise(windowBorderHeight)
        ))

        settings.scaledWidthProp.bind(scaledWidth)
        settings.scaledHeightProp.bind(scaledHeight)

        if (settings.isScaleAffectedOnResize) {
            if (settings.isPreserveResizeRatio) {
                scaleRatioX.bind(Bindings.min(
                        scaledWidth.divide(settings.width), scaledHeight.divide(settings.height)
                ))
                scaleRatioY.bind(scaleRatioX)
            } else {
                scaleRatioX.bind(scaledWidth.divide(settings.width))
                scaleRatioY.bind(scaledHeight.divide(settings.height))
            }
        } else {
            scaleRatioX.value = 1.0
            scaleRatioY.value = 1.0

            scaledWidth.addListener { _, _, newWidth -> onStageResize() }
            scaledHeight.addListener { _, _, newHeight -> onStageResize() }
        }

        log.debug("Window border size: ($windowBorderWidth, $windowBorderHeight)")
        log.debug("Scaled size: ${scaledWidth.value} x ${scaledHeight.value}")
        log.debug("Scaled ratio: (${scaleRatioX.value}, ${scaleRatioY.value})")
        log.debug("Scene size: ${stage.scene.width} x ${stage.scene.height}")
        log.debug("Stage size: ${stage.width} x ${stage.height}")

        if (settings.isFullScreenAllowed && settings.isFullScreenFromStart) {
            stage.isFullScreen = true

            log.debug("Going fullscreen")
        }
    }

    override fun close() {
        log.debug("Closing main window")

        stage.close()
    }

    override fun addIcons(vararg images: Image) {
        if (!settings.isNative) {
            stage.icons += images
        }
    }

    override fun addCSS(vararg cssList: CSS) {
        fxScene.stylesheets += cssList.map { it.externalForm }
    }

    override fun setRoot(root: Pane) {
        fxScene.root = root
    }

    override fun iconifiedProperty(): ReadOnlyBooleanProperty = stage.iconifiedProperty()

    override fun takeScreenshot(): Image = fxScene.snapshot(null)

    /**
     * Called when the user has resized the main window.
     * Only called when settings.isScaleAffectedOnResize = false.
     */
    private fun onStageResize() {
        val newW = scaledWidth.value
        val newH = scaledHeight.value

        log.debug("On Stage resize: ${newW}x$newH")

        scenes.filterIsInstance<FXGLScene>()
                .forEach {
                    it.viewport.width = newW
                    it.viewport.height = newH
                }
    }
}

internal class EmbeddedPaneWindow(

        val fxglPane: FXGLPane,
        scene: FXGLScene,
        settings: ReadOnlyGameSettings

) : MainWindow(scene, settings) {

    private val backgroundRect = Rectangle()
    private val clipRect = Rectangle()

    override val x: Double
        get() = fxglPane.localToScene(0.0, 0.0).x

    override val y: Double
        get() = fxglPane.localToScene(0.0, 0.0).y

    // TODO: fix impl
    override val isFocused: Boolean
        get() = true

    override val width: Double
        get() = fxglPane.renderWidth

    override val height: Double
        get() = fxglPane.renderHeight

    init {
        computeScaledDimensions()

        // this clips the max area (ensures max size)
        clipRect.widthProperty().bind(fxglPane.renderWidthProperty())
        clipRect.heightProperty().bind(fxglPane.renderHeightProperty())
        fxglPane.clip = clipRect

        // this rect ensures min size
        backgroundRect.widthProperty().bind(fxglPane.renderWidthProperty())
        backgroundRect.heightProperty().bind(fxglPane.renderHeightProperty())
        backgroundRect.fillProperty().bind(fxglPane.renderFillProperty())
        fxglPane.allChildren += backgroundRect

        setInitialScene(scene)

        fxglPane.sceneProperty().addListener { _, oldScene, newScene ->
            if (oldScene != null) {
                removeAllEventFiltersAndHandlers(oldScene)
            }

            if (newScene != null) {
                addKeyHandler(newScene) { e ->
                    input.onKeyEvent(e)
                    stateMachine.runOnActiveStates { it.input.onKeyEvent(e) }
                }

                // we also take fxglPane scene location into account, hence we add event subscriber directly
                val mouseHandler: EventHandler<MouseEvent> = EventHandler {
                    val data = MouseEventData(
                            it,
                            fxglPane.localToScene(0.0, 0.0).add(currentFXGLScene.contentRoot.translateX, currentFXGLScene.contentRoot.translateY),
                            Point2D(currentFXGLScene.viewport.x, currentFXGLScene.viewport.y),
                            currentFXGLScene.viewport.getZoom(),
                            scaleRatioX.value,
                            scaleRatioY.value
                    )

                    input.onMouseEvent(data)
                    stateMachine.runOnActiveStates { it.input.onMouseEvent(data) }
                }

                eventSubscribers.add(SceneEventSubscriber(newScene, MouseEvent.ANY, mouseHandler, isFilter = false))

                // reroute any events to current state input
                addGlobalHandler(newScene,
                        { e ->
                            input.fireEventViaFilters(e)
                            stateMachine.runOnActiveStates { it.input.fireEventViaFilters(e) }
                        },
                        { e ->
                            input.fireEventViaHandlers(e)
                            stateMachine.runOnActiveStates { it.input.fireEventViaHandlers(e) }
                        }
                )
            }
        }
    }

    private fun computeScaledDimensions() {
        var newW = settings.width.toDouble()
        var newH = settings.height.toDouble()

        val bounds = Rectangle2D(0.0, 0.0, newW, newH)

        if (newW > bounds.width || newH > bounds.height) {
            log.debug("Target size > screen size")

            // margin so the window size is slightly smaller than bounds
            // to account for platform-specific window borders
            val extraMargin = 25.0
            val ratio = newW / newH

            for (newWidth in bounds.width.toInt() downTo 1) {
                if (newWidth / ratio <= bounds.height) {
                    newW = newWidth.toDouble() - extraMargin
                    newH = newWidth / ratio
                    break
                }
            }
        }

        // round to a whole number
        newW = newW.toInt().toDouble()
        newH = newH.toInt().toDouble()

        scaledWidth.set(newW)
        scaledHeight.set(newH)
        scaleRatioX.set(scaledWidth.value / settings.width)
        scaleRatioY.set(scaledHeight.value / settings.height)

        log.debug("Target settings size: ${settings.width.toDouble()} x ${settings.height.toDouble()}")
        log.debug("Scaled scene size:    $newW x $newH")
        log.debug("Scaled ratio: (${scaleRatioX.value}, ${scaleRatioY.value})")
    }

    override fun iconifiedProperty(): ReadOnlyBooleanProperty {
        return ReadOnlyBooleanWrapper().readOnlyProperty
    }

    override fun addIcons(vararg images: Image) {
    }

    override fun addCSS(vararg cssList: CSS) {
        fxglPane.stylesheets += cssList.map { it.externalForm }
    }

    override fun setRoot(root: Pane) {
        fxglPane.allChildren.setAll(
                backgroundRect,
                root
        )
    }

    override fun show() {
        log.debug("Opening embedded window")

        // platform offsets
        var windowBorderWidth = 0
        var windowBorderHeight = 0

        scaledWidth.bind(fxglPane.renderWidthProperty())
        scaledHeight.bind(fxglPane.renderHeightProperty())

        settings.scaledWidthProp.bind(scaledWidth)
        settings.scaledHeightProp.bind(scaledHeight)

        if (settings.isScaleAffectedOnResize) {
            if (settings.isPreserveResizeRatio) {
                scaleRatioX.bind(Bindings.min(
                        scaledWidth.divide(settings.width), scaledHeight.divide(settings.height)
                ))
                scaleRatioY.bind(scaleRatioX)
            } else {
                scaleRatioX.bind(scaledWidth.divide(settings.width))
                scaleRatioY.bind(scaledHeight.divide(settings.height))
            }
        } else {
            scaleRatioX.value = 1.0
            scaleRatioY.value = 1.0

            scaledWidth.addListener { _, _, newWidth -> onStageResize() }
            scaledHeight.addListener { _, _, newHeight -> onStageResize() }
        }

        log.debug("Window border size: ($windowBorderWidth, $windowBorderHeight)")
        log.debug("Scaled size: ${scaledWidth.value} x ${scaledHeight.value}")
        log.debug("Scaled ratio: (${scaleRatioX.value}, ${scaleRatioY.value})")
        //log.debug("Scene size: ${stage.scene.width} x ${stage.scene.height}")
        //log.debug("Stage size: ${stage.width} x ${stage.height}")
    }

    /**
     * Called when the user has resized the main window.
     * Only called when settings.isScaleAffectedOnResize = false.
     */
    private fun onStageResize() {
        val newW = scaledWidth.value
        val newH = scaledHeight.value

        log.debug("On Stage resize: ${newW}x$newH")

        scenes.filterIsInstance<FXGLScene>()
                .forEach {
                    it.viewport.width = newW
                    it.viewport.height = newH
                }
    }

    override fun takeScreenshot(): Image {
        return WritableImage(1, 1)
    }

    override fun close() {
    }
}

class FXGLPane(w: Double, h: Double) : Region() {

    val allChildren
        get() = children

    private val renderWidthProp = SimpleDoubleProperty(w)
    private val renderHeightProp = SimpleDoubleProperty(h)

    // default is white to be consistent with FXGL's scene default in non-embedded mode
    private val renderFillProp = SimpleObjectProperty<Paint>(Color.WHITE)

    var renderWidth: Double
        get() = renderWidthProp.value
        set(value) { renderWidthProp.value = value }

    var renderHeight: Double
        get() = renderHeightProp.value
        set(value) { renderHeightProp.value = value }

    var renderFill: Paint
        get() = renderFillProp.value
        set(value) { renderFillProp.value = value }

    fun renderWidthProperty(): DoubleProperty = renderWidthProp
    fun renderHeightProperty(): DoubleProperty = renderHeightProp

    fun renderFillProperty(): ObjectProperty<Paint> = renderFillProp
}

class SceneEventSubscriber<T : Event>(
        val fxScene: javafx.scene.Scene,
        val eventType: EventType<T>,
        val eventHandler: EventHandler<T>,
        val isFilter: Boolean
) {
    init {
        if (isFilter) {
            fxScene.addEventFilter(eventType, eventHandler)
        } else {
            fxScene.addEventHandler(eventType, eventHandler)
        }
    }

    fun unsubscribe() {
        if (isFilter) {
            fxScene.removeEventFilter(eventType, eventHandler)
        } else {
            fxScene.removeEventHandler(eventType, eventHandler)
        }
    }
}