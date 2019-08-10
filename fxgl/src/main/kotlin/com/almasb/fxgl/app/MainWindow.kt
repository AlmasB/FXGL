package com.almasb.fxgl.app

import com.almasb.fxgl.core.local.Local
import com.almasb.fxgl.dsl.FXGL
import com.almasb.sslogger.Logger
import com.almasb.fxgl.input.MouseEventData
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.scene.Scene
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.event.Event
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.Parent
import javafx.scene.image.Image
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
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

    val currentFXGLScene = ReadOnlyObjectWrapper<FXGLScene>(scene)

    val currentStateProperty = ReadOnlyObjectWrapper<Scene>(scene)

    private val scenes = arrayListOf<FXGLScene>()

    private val scaledWidth: DoubleProperty = SimpleDoubleProperty()
    private val scaledHeight: DoubleProperty = SimpleDoubleProperty()
    private val scaleRatioX: DoubleProperty = SimpleDoubleProperty()
    private val scaleRatioY: DoubleProperty = SimpleDoubleProperty()

    private val subScenes = FXCollections.observableArrayList<SubScene>()

    val currentState: Scene
        get() = if (subScenes.isEmpty()) currentFXGLScene.value else subScenes.last()

    init {
        fxScene = createScene(scene.root)

        setScene(scene)

        initStage()

        addKeyHandler {
            currentState.input.onKeyEvent(it)
        }

        addMouseHandler {
            currentState.input.onMouseEvent(it)
        }

        // reroute any events to current state input
        addGlobalHandler {
            currentState.input.fireEvent(it)
        }
    }

    /**
     * Construct the only JavaFX scene with computed size based on user settings.
     */
    private fun createScene(root: Parent): javafx.scene.Scene {
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

                if (settings.isCloseConfirmation) {
                    if (canShowCloseDialog()) {
                        showConfirmExitDialog()
                    }
                } else {
                    FXGL.getGameController().exit()
                }
            }

            if (!settings.isExperimentalNative) {
                icons.add(FXGL.image(settings.appIcon))
            }

            if (settings.isFullScreenAllowed) {
                fullScreenExitHint = ""
                // don't let the user exit FS mode manually
                fullScreenExitKeyCombination = KeyCombination.NO_MATCH
            }

            settings.fullScreen.addListener { _, _, fullscreenNow ->
                isFullScreen = fullscreenNow
            }

            sizeToScene()
            centerOnScreen()
        }
    }

    private fun showConfirmExitDialog() {
        FXGL.getDisplay().showConfirmationBox(Local.getLocalizedString("dialog.exitGame")) { yes ->
            if (yes)
                FXGL.getGameController().exit()
        }
    }

    internal fun onUpdate(tpf: Double) {
        currentState.update(tpf)
    }

    /**
     * Set current FXGL scene.
     * The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    fun setScene(scene: FXGLScene) {
        if (!subScenes.isEmpty()) {
            log.warning("Cannot change states with active substates")
            return
        }

        if (scene !in scenes) {
            registerScene(scene)
        }

        val prevState = currentFXGLScene.value

        prevState.exit()

        currentFXGLScene.value.activeProperty().set(false)

        // new state
        currentFXGLScene.value = scene
        fxScene.root = scene.root
        scene.activeProperty().set(true)

        log.debug("$prevState -> $scene")

        currentFXGLScene.value.enter(prevState)

        currentStateProperty.value = currentState
    }

    fun pushState(newState: SubScene) {
        log.debug("Push state: $newState")

        // substate, so prevState does not exit
        val prevState = currentState
        prevState.input.clearAll()

        log.debug("$prevState -> $newState")

        // new state
        subScenes.add(newState)

        // push view
        getCurrentScene().root.children.add(newState.root)

        newState.enter(prevState)

        currentStateProperty.value = currentState
    }

    fun popState() {
        if (subScenes.isEmpty()) {
            throw IllegalStateException("Cannot pop state: Substates are empty!")
        }

        val prevState = subScenes.last()
        log.debug("Pop state: $prevState")

        prevState.exit()

        subScenes.removeAt(subScenes.size - 1)

        // pop view
        getCurrentScene().root.children.remove(prevState.root)

        currentStateProperty.value = currentState

        log.debug("$currentState <- $prevState")
    }

    /**
     * @return true if can show close dialog
     */
    private fun canShowCloseDialog(): Boolean {
        return true
        // do not allow close dialog if
        // 1. a dialog is shown
        // 2. we are loading a game
        // 3. we are showing intro
//        return (state !== FXGL.getStateMachine().dialogState
//                && state !== FXGL.getStateMachine().loadingState
//                && (!FXGL.getSettings().isIntroEnabled || state !== FXGL.getStateMachine().introState))
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
    }

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    private fun registerScene(scene: FXGLScene) {
        scene.bindSize(scaledWidth, scaledHeight, scaleRatioX, scaleRatioY)

        settings.cssList.forEach {
            log.debug("Applying CSS: $it")
            scene.appendCSS(FXGL.getAssetLoader().loadCSS(it))
        }

        if (!settings.isExperimentalNative && settings.isDesktop && scene.root.cursor == null) {
            scene.setCursor(FXGL.getAssetLoader().loadCursorImage("fxgl_default.png"), Point2D(7.0, 6.0))
        }

        scenes.add(scene)
    }

    fun getCurrentScene(): FXGLScene {
        return currentFXGLScene.value
    }

    fun addKeyHandler(handler: (KeyEvent) -> Unit) {
        fxScene.addEventHandler(KeyEvent.ANY, handler)
    }

    fun addMouseHandler(handler: (MouseEventData) -> Unit) {
        fxScene.addEventHandler(MouseEvent.ANY, {
            handler(MouseEventData(it, Point2D(getCurrentScene().viewport.getX(), getCurrentScene().viewport.getY()), scaleRatioX.value, scaleRatioY.value))
        })
    }

    fun addGlobalHandler(handler: (Event) -> Unit) {
        fxScene.addEventHandler(EventType.ROOT, {
            handler(it.copyFor(null, null))
        })
    }

    fun takeScreenshot(): Image = fxScene.snapshot(null)

    /**
     * Saves a screenshot of the current scene into a ".png" file,
     * named by title + version + time.
     *
     * @return true if the screenshot was saved successfully, false otherwise
     */
    fun saveScreenshot(): Boolean {
        var fileName = "./" + settings.title + settings.version + LocalDateTime.now()
        fileName = fileName.replace(":", "_")

        return saveScreenshot(fileName)
    }

    /**
     * Saves a screenshot of the current scene into a ".png" [fileName].
     *
     * @return true if the screenshot was saved successfully, false otherwise
     */
    fun saveScreenshot(fileName: String): Boolean {
        val fxImage = takeScreenshot()

        val img = SwingFXUtils.fromFXImage(fxImage, null)

        try {
            val name = if (fileName.endsWith(".png")) fileName else "$fileName.png"

            Files.newOutputStream(Paths.get(name)).use {
                return ImageIO.write(img, "png", it)
            }
        } catch (e: Exception) {
            log.warning("saveScreenshot($fileName.png) failed: $e")
            return false
        }
    }
}