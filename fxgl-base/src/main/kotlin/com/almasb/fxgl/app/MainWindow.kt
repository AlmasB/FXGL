package com.almasb.fxgl.app

import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.embed.swing.SwingFXUtils
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Scene
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Screen
import javafx.stage.Stage
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.imageio.ImageIO

/**
 * A wrapper around JavaFX primary stage.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class MainWindow(val stage: Stage, private val settings: ReadOnlyGameSettings) {

    private val log = Logger.get(javaClass)

    private val fxScene: Scene

    private val currentScene = ReadOnlyObjectWrapper<FXGLScene>()

    private val scaledWidth: DoubleProperty = SimpleDoubleProperty()
    private val scaledHeight: DoubleProperty = SimpleDoubleProperty()
    private val scaleRatioX: DoubleProperty = SimpleDoubleProperty()
    private val scaleRatioY: DoubleProperty = SimpleDoubleProperty()

    private val keyHandler = EventHandler<KeyEvent> {
        FXGL.getApp().stateMachine.currentState.input.onKeyEvent(it)
    }

    private val mouseHandler = EventHandler<MouseEvent> { e ->
        currentScene.value?.let {
            FXGL.getApp().stateMachine.currentState.input.onMouseEvent(e, it.viewport, scaleRatioX.value, scaleRatioY.value)
        }
    }

    private val genericHandler = EventHandler<Event> {
        FXGL.getApp().stateMachine.currentState.input.fireEvent(it.copyFor(null, null))
    }

    var onShown: Runnable? = null

    init {
        fxScene = createScene()

        // main key event handler
        fxScene.addEventHandler(KeyEvent.ANY, keyHandler)

        // main mouse event handler
        fxScene.addEventHandler(MouseEvent.ANY, mouseHandler)

        // reroute any events to current state input
        fxScene.addEventHandler(EventType.ROOT, genericHandler)
    }

    private fun createScene(): Scene {
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

        val scene = Scene(Pane(), newW, newH)

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
     * Must be called on FX thread.
     */
    fun initAndShow() {
        // we call this late so that all scenes have been initialized
        // and computed their width / height
        initStage()

        show()
    }

    /**
     * Configure main stage based on user settings.
     */
    private fun initStage() {
        with(stage) {
            scene = fxScene

            title = "${settings.title} ${settings.version}"

            isResizable = settings.isManualResizeEnabled

            if (FXGL.isDesktop()) {
                initStyle(settings.stageStyle)
            }

            setOnCloseRequest { e ->
                e.consume()

                if (settings.isCloseConfirmation) {
                    if (canShowCloseDialog()) {
                        FXGL.getDisplay().showConfirmationBox(FXGL.getLocalizedString("dialog.exitGame"), { yes ->
                            if (yes)
                                FXGL.getApp().exit()
                        })
                    }
                } else {
                    FXGL.getApp().exit()
                }
            }

            setOnShown {
                this@MainWindow.onShown?.run()
            }

            icons.add(image(settings.appIcon))

            if (settings.isFullScreenAllowed) {
                fullScreenExitHint = ""
                // don't let the user exit FS mode manually
                fullScreenExitKeyCombination = KeyCombination.NO_MATCH
            }

            FXGL.getMenuSettings().fullScreenProperty().addListener { _, _, fullscreenNow ->
                isFullScreen = fullscreenNow
            }

            sizeToScene()
            centerOnScreen()
        }
    }

    /**
     * @return true if can show close dialog
     */
    private fun canShowCloseDialog(): Boolean {
        val state = FXGL.getApp().stateMachine.currentState

        // do not allow close dialog if
        // 1. a dialog is shown
        // 2. we are loading a game
        // 3. we are showing intro
        return (state !== DialogSubState
                && state !== FXGL.getApp().stateMachine.loadingState
                && (!FXGL.getApp().settings.isIntroEnabled || state !== FXGL.getApp().stateMachine.introState))
    }

    private var windowBorderWidth = 0.0
    private var windowBorderHeight = 0.0

    private fun show() {
        log.debug("Opening main window")

        stage.show()

        // platform offsets
        windowBorderWidth = stage.width - scaledWidth.value
        windowBorderHeight = stage.height - scaledHeight.value

        scaledWidth.bind(stage.widthProperty().subtract(
                Bindings.`when`(stage.fullScreenProperty()).then(0).otherwise(windowBorderWidth)
        ))
        scaledHeight.bind(stage.heightProperty().subtract(
                Bindings.`when`(stage.fullScreenProperty()).then(0).otherwise(windowBorderHeight)
        ))
        scaleRatioX.bind(scaledWidth.divide(settings.width))
        scaleRatioY.bind(scaledHeight.divide(settings.height))

        log.debug("Window border size: ($windowBorderWidth, $windowBorderHeight)")
        log.debug("Scaled size: ${scaledWidth.value} x ${scaledHeight.value}")
        log.debug("Scaled ratio: (${scaleRatioX.value}, ${scaleRatioY.value})")
        log.debug("Scene size: ${stage.scene.width} x ${stage.scene.height}")
        log.debug("Stage size: ${stage.width} x ${stage.height}")
    }

    fun fixAspectRatio() {
        log.debug("Fixing aspect ratio")

        val ratio = settings.width.toDouble() / settings.height

        stage.height = scaledWidth.value / ratio + windowBorderHeight

        log.debug("Scaled size: ${scaledWidth.value} x ${scaledHeight.value}")
        log.debug("Scaled ratio: (${scaleRatioX.value}, ${scaleRatioY.value})")
        log.debug("Scene size: ${stage.scene.width} x ${stage.scene.height}")
        log.debug("Stage size: ${stage.width} x ${stage.height}")
    }

    private val scenes = arrayListOf<FXGLScene>()

    /**
     * Set current FXGL scene.
     * The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    fun setScene(scene: FXGLScene) {
        if (!scenes.contains(scene)) {
            registerScene(scene)
        }

        currentScene.value?.activeProperty()?.set(false)

        currentScene.set(scene)
        scene.activeProperty().set(true)

        fxScene.root = scene.root
    }

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    private fun registerScene(scene: FXGLScene) {
        scene.bindSize(scaledWidth, scaledHeight, scaleRatioX, scaleRatioY)
        scene.appendCSS(FXGLAssets.UI_CSS)
        scenes.add(scene)
    }

    fun getCurrentScene(): FXGLScene {
        return currentScene.value
    }

    /**
     * Saves a screenshot of the current scene into a ".png" file.
     *
     * @return true if the screenshot was saved successfully, false otherwise
     */
    fun saveScreenshot(): Boolean {
        val fxImage = fxScene.snapshot(null)

        var fileName = "./" + settings.title + settings.version + LocalDateTime.now()
        fileName = fileName.replace(":", "_")

        val img = SwingFXUtils.fromFXImage(fxImage, null)

        try {
            Files.newOutputStream(Paths.get(fileName + ".png")).use {
                return ImageIO.write(img, "png", it)
            }
        } catch (e: Exception) {
            log.warning("saveScreenshot() failed: $e")
            return false
        }
    }
}