/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.fsm.StateMachine
import com.almasb.fxgl.input.MouseEventData
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SubScene
import com.almasb.sslogger.Logger
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.event.Event
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.ImageCursor
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.stage.Screen
import javafx.stage.Stage

/**
 * A wrapper around JavaFX primary stage.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class MainWindow(

        /**
         * Primary stage.
         */
        val stage: Stage,

        /**
         * The starting scene which is used when the window is created.
         */
        scene: FXGLScene,

        private val settings: ReadOnlyGameSettings) {

    private val log = Logger.get(javaClass)

    private val fxScene: javafx.scene.Scene

    val currentFXGLSceneProperty = ReadOnlyObjectWrapper<FXGLScene>(scene)
    val currentSceneProperty = ReadOnlyObjectWrapper<Scene>(scene)

    val currentFXGLScene: FXGLScene
        get() = currentFXGLSceneProperty.value

    val currentScene: Scene
        get() = currentSceneProperty.value

    var onClose: (() -> Unit)? = null
    var defaultCursor: ImageCursor? = null

    private val scenes = arrayListOf<Scene>()

    private val scaledWidth: DoubleProperty = SimpleDoubleProperty()
    private val scaledHeight: DoubleProperty = SimpleDoubleProperty()
    private val scaleRatioX: DoubleProperty = SimpleDoubleProperty()
    private val scaleRatioY: DoubleProperty = SimpleDoubleProperty()

    private val stateMachine = StateMachine(scene)

    init {
        fxScene = createFXScene(scene.root)

        setInitialScene(scene)

        initStage()

        addKeyHandler { e ->
            stateMachine.runOnActiveStates { it.input.onKeyEvent(e) }
        }

        addMouseHandler { e ->
            stateMachine.runOnActiveStates { it.input.onMouseEvent(e) }
        }

        // reroute any events to current state input
        addGlobalHandler { e ->
            stateMachine.runOnActiveStates { it.input.fireEvent(e) }
        }
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

    private fun setInitialScene(scene: FXGLScene) {
        registerScene(scene)

        currentFXGLSceneProperty.value = scene
        scene.activeProperty().set(true)
        currentSceneProperty.value = scene

        log.debug("Set initial scene to $scene")
    }

    /**
     * Add desktop taskbar / window icon.
     * Multiple images of different sizes can be added: 16x16, 32x32
     * and most suitable will be chosen.
     * Can only be called before [show].
     */
    fun addIcons(vararg images: Image) {
        if (!settings.isExperimentalNative) {
            stage.icons += images
        }
    }

    fun addCSS(vararg cssList: CSS) {
        fxScene.stylesheets += cssList.map { it.externalForm }
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

    fun onUpdate(tpf: Double) {
        stateMachine.runOnActiveStates { it.update(tpf) }
    }

    /**
     * Set current FXGL scene.
     * The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    fun setScene(scene: FXGLScene) {
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
        fxScene.root = scene.root
        scene.activeProperty().set(true)

        currentSceneProperty.value = scene

        log.debug("$prevScene -> $scene")
    }

    fun pushState(newScene: SubScene) {
        log.debug("Push state: $newScene")

        if (newScene !in scenes) {
            registerScene(newScene)
        }

        val prevScene = stateMachine.currentState

        stateMachine.changeState(newScene)

        prevScene.input.clearAll()

        // push view
        currentFXGLScene.root.children.add(newScene.root)

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
        currentFXGLScene.root.children.remove(prevScene.root)

        currentSceneProperty.value = stateMachine.currentState

        log.debug("${stateMachine.currentState} <- $prevScene")
    }

    private var windowBorderWidth = 0.0
    private var windowBorderHeight = 0.0

    fun show() {
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

        if (settings.isPreserveResizeRatio) {
            scaleRatioX.bind(Bindings.min(
                    scaledWidth.divide(settings.width), scaledHeight.divide(settings.height)
            ))
            scaleRatioY.bind(scaleRatioX)
        } else {
            scaleRatioX.bind(scaledWidth.divide(settings.width))
            scaleRatioY.bind(scaledHeight.divide(settings.height))
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

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    private fun registerScene(scene: Scene) {
        scene.bindSize(scaledWidth, scaledHeight, scaleRatioX, scaleRatioY)

        if (!settings.isExperimentalNative
                && settings.isDesktop
                && scene is FXGLScene
                && scene.root.cursor == null) {
            defaultCursor?.let {
                scene.setCursor(it.image, Point2D(it.hotspotX, it.hotspotY))
            }
        }

        scenes.add(scene)
    }

    private fun addKeyHandler(handler: (KeyEvent) -> Unit) {
        fxScene.addEventHandler(KeyEvent.ANY, handler)
    }

    private fun addMouseHandler(handler: (MouseEventData) -> Unit) {
        fxScene.addEventHandler(MouseEvent.ANY) {
            handler(MouseEventData(it, Point2D(currentFXGLScene.viewport.x, currentFXGLScene.viewport.y), scaleRatioX.value, scaleRatioY.value))
        }
    }

    private fun addGlobalHandler(handler: (Event) -> Unit) {
        fxScene.addEventHandler(EventType.ROOT) {
            handler(it.copyFor(null, null))
        }
    }

    fun takeScreenshot(): Image = fxScene.snapshot(null)
}