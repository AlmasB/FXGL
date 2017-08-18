package com.almasb.fxgl.app

import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.settings.ReadOnlyGameSettings
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

    private val fxScene = Scene(Pane(), settings.width.toDouble(), settings.height.toDouble())

    private val currentScene = ReadOnlyObjectWrapper<FXGLScene>()

    private val scaledWidth: DoubleProperty = SimpleDoubleProperty()
    private val scaledHeight: DoubleProperty = SimpleDoubleProperty()
    private val scaleRatio: DoubleProperty = SimpleDoubleProperty()

    private val keyHandler = EventHandler<KeyEvent> {
        FXGL.getApp().stateMachine.currentState.input.onKeyEvent(it)
    }

    private val mouseHandler = EventHandler<MouseEvent> { e ->
        currentScene.value?.let {
            FXGL.getApp().stateMachine.currentState.input.onMouseEvent(e, it.viewport, scaleRatio.value)
        }
    }

    private val genericHandler = EventHandler<Event> {
        FXGL.getApp().stateMachine.currentState.input.fireEvent(it.copyFor(null, null))
    }

    var onShown: Runnable? = null

    init {
        // main key event handler
        fxScene.addEventHandler(KeyEvent.ANY, keyHandler)

        // main mouse event handler
        fxScene.addEventHandler(MouseEvent.ANY, mouseHandler)

        // reroute any events to current state input
        fxScene.addEventHandler(EventType.ROOT, genericHandler)
    }

    /**
     * Must be called on FX thread.
     */
    fun initAndShow() {
        // we call this late so that all scenes have been initialized
        // and computed their width / height
        initStage()

        computeScaledSize()

        show()
    }

    /**
     * Configure main stage based on user settings.
     */
    private fun initStage() {
        with(stage) {
            scene = fxScene

            title = "${settings.title} ${settings.version}"
            isResizable = false

            setOnCloseRequest { e ->
                e.consume()

                if (settings.isCloseConfirmation) {
                    if (FXGL.getApp().stateMachine.canShowCloseDialog()) {
                        FXGL.getDisplay().showConfirmationBox("Exit the game?", { yes ->
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

            icons.add(FXGLAssets.UI_ICON)

            if (settings.isFullScreen) {
                fullScreenExitHint = ""
                // don't let the user exit FS mode manually
                fullScreenExitKeyCombination = KeyCombination.NO_MATCH
                isFullScreen = true
            }

            sizeToScene()
            centerOnScreen()
        }
    }

    /**
     * Computes scaled size of the output based on screen and target
     * resolutions.
     */
    private fun computeScaledSize() {
        var newW = settings.width.toDouble()
        var newH = settings.height.toDouble()
        val bounds = if (settings.isFullScreen) Screen.getPrimary().bounds else Screen.getPrimary().visualBounds

        if (newW > bounds.width || newH > bounds.height) {
            log.debug("App size > screen size")

            val ratio = newW / newH

            for (newWidth in bounds.width.toInt() downTo 1) {
                if (newWidth / ratio <= bounds.height) {
                    newW = newWidth.toDouble()
                    newH = newWidth / ratio
                    break
                }
            }
        }

        scaledWidth.set(newW)
        scaledHeight.set(newH)
        scaleRatio.set(newW / settings.width)

        log.debug("Target size: ${settings.width.toDouble()} x ${settings.height.toDouble()} @ 1.0")
        log.debug("New size:    $newW x $newH @ ${scaleRatio.value}")
    }

    private fun show() {
        log.debug("Opening main window")

        stage.show()

        log.debug("Root size:  " + stage.scene.root.layoutBounds.width + "x" + stage.scene.root.layoutBounds.height)
        log.debug("Scene size: " + stage.scene.width + "x" + stage.scene.height)
        log.debug("Stage size: " + stage.width + "x" + stage.height)
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
    fun registerScene(scene: FXGLScene) {
        scene.bindSize(scaledWidth, scaledHeight, scaleRatio)
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