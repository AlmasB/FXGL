/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.service.impl.display

import com.almasb.fxgl.app.DialogSubState
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.io.UIDialogHandler
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.scene.CSS
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.service.Display
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import com.almasb.fxgl.settings.SceneDimension
import com.almasb.fxgl.saving.UserProfile
import com.google.inject.Inject
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleDoubleProperty
import javafx.embed.swing.SwingFXUtils
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Screen
import javafx.stage.Stage
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import java.util.function.Predicate
import javax.imageio.ImageIO

/**
 * Display service.
 * Provides access to dialogs and display settings.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGLDisplay
@Inject
private constructor(private val stage: Stage, private val settings: ReadOnlyGameSettings) : Display {

    private val log = FXGL.getLogger(javaClass)

    // init with placeholder scene
    private val currentScene = ReadOnlyObjectWrapper<FXGLScene>(object : FXGLScene() {})

    private val targetWidth: DoubleProperty
    private val targetHeight: DoubleProperty
    private val scaledWidth: DoubleProperty
    private val scaledHeight: DoubleProperty
    private val scaleRatio: DoubleProperty

    private val css: CSS

    private val sceneDimensions = ArrayList<SceneDimension>()

    private lateinit var fxScene: Scene

    init {
        targetWidth = SimpleDoubleProperty(settings.width.toDouble())
        targetHeight = SimpleDoubleProperty(settings.height.toDouble())
        scaledWidth = SimpleDoubleProperty()
        scaledHeight = SimpleDoubleProperty()
        scaleRatio = SimpleDoubleProperty()

        // if default css then use menu css, else use specified
        css = if (FXGLAssets.UI_CSS.isDefault())
            FXGL.getAssetLoader().loadCSS(settings.menuStyle.cssFileName)
        else
            FXGLAssets.UI_CSS

        log.debug("Using CSS: $css")
    }

    /**
     * Must be called on FX thread.
     */
    override fun initAndShow() {
        initScene()
        initStage()
        initDialogBox()

        computeSceneSettings(settings.width.toDouble(), settings.height.toDouble())
        computeScaledSize()

        log.debug("Opening primary stage")

        stage.show()
    }

    private val keyHandler = EventHandler<KeyEvent> {
        FXGL.getApp().stateMachine.currentState.input.onKeyEvent(it)
    }

    private val mouseHandler = EventHandler<MouseEvent> {
        FXGL.getApp().stateMachine.currentState.input.onMouseEvent(it, getCurrentScene().viewport, getScaleRatio())
    }

    private val genericHandler = EventHandler<Event> {
        FXGL.getApp().stateMachine.currentState.input.fireEvent(it.copyFor(null, null))
    }

    private fun initScene() {
        fxScene = Scene(Pane(), targetWidth.value, targetHeight.value)

        // main key event handler
        fxScene.addEventFilter(KeyEvent.ANY, keyHandler)

        // main mouse event handler
        fxScene.addEventFilter(MouseEvent.ANY, mouseHandler)

        // reroute any events to current state input
        fxScene.addEventFilter(EventType.ROOT, genericHandler)
    }

    /**
     * Configure main stage based on user settings.
     */
    private fun initStage() {
        with(stage) {
            scene = fxScene

            title = settings.title + " " + settings.version
            isResizable = false
            setOnCloseRequest { e ->
                e.consume()

                if (settings.isCloseConfirmation) {
                    if (FXGL.getApp().stateMachine.canShowCloseDialog()) {
                        showConfirmationBox("Exit the game?", { yes ->
                            if (yes)
                                FXGL.getEventBus().fireEvent(DisplayEvent(DisplayEvent.CLOSE_REQUEST))
                        })
                    }
                } else {
                    FXGL.getEventBus().fireEvent(DisplayEvent(DisplayEvent.CLOSE_REQUEST))
                }
            }

            setOnShown {
                log.debug("Stage shown")
                log.debug("Root size: " + stage.scene.root.layoutBounds.width + "x" + stage.scene.root.layoutBounds.height)
                log.debug("Scene size: " + stage.scene.width + "x" + stage.scene.height)
                log.debug("Stage size: " + stage.width + "x" + stage.height)
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

    private val scenes = arrayListOf<FXGLScene>()

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    override fun registerScene(scene: FXGLScene) {
        scene.bindSize(scaledWidth, scaledHeight, scaleRatio)
        scene.appendCSS(css)
        scenes.add(scene)
    }

    /**
     * Set current FXGL scene. The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    override fun setScene(scene: FXGLScene) {
        if (!scenes.contains(scene)) {
            registerScene(scene)
        }

        getCurrentScene().activeProperty().set(false)

        currentScene.set(scene)
        scene.activeProperty().set(true)

        fxScene.root = scene.root
    }

    /**
     * @return current scene property
     */
    override fun currentSceneProperty() = currentScene.readOnlyProperty

    private fun getBounds() = if (settings.isFullScreen) Screen.getPrimary().bounds
                                else Screen.getPrimary().visualBounds

    /**
     * Saves a screenshot of the current scene into a ".png" file.

     * @return true if the screenshot was saved successfully, false otherwise
     */
    override fun saveScreenshot(): Boolean {
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

    /**
     * @return a list of supported scene dimensions with 360, 480, 720 and 1080 heights
     */
    override fun getSceneDimensions() = ArrayList(sceneDimensions)

    /**
     * Computes scene settings based on target size and screen bounds.
     *
     * @param width  target (app) width
     * @param height target (app) height
     */
    private fun computeSceneSettings(width: Double, height: Double) {
        val bounds = getBounds()

        val ratio = width / height

        sceneDimensions.addAll(
                intArrayOf(360, 480, 720, 1080)
                        .filter { it <= bounds.height && it * ratio <= bounds.width }
                        .map { SceneDimension(it * ratio, it.toDouble()) }
        )
    }

    /**
     * @return target width (requested width)
     */
    fun getTargetWidth(): Double {
        return targetWidth.get()
    }

    /**
     * @return target height (requested height)
     */
    fun getTargetHeight(): Double {
        return targetHeight.get()
    }

    /**
     * @return scaled width (actual width of the scenes)
     */
    fun getScaledWidth(): Double {
        return scaledWidth.get()
    }

    /**
     * @return scaled height (actual height of the scenes)
     */
    fun getScaledHeight(): Double {
        return scaledHeight.get()
    }

    /**
     * @return scale ratio (set width / settings width)
     */
    override fun getScaleRatio(): Double {
        return scaleRatio.get()
    }

    /**
     * Computes scaled size of the output based on screen and target
     * resolutions.
     */
    private fun computeScaledSize() {
        var newW = getTargetWidth()
        var newH = getTargetHeight()
        val bounds = getBounds()

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

        log.debug("Target size: $targetWidth x $targetHeight @ 1.0")
        log.debug("New size:    $newW x $newH @ $scaleRatio")
    }

    /**
     * Performs actual change of output resolution.
     * It will create a new underlying JavaFX scene.
     * fxglTODO: impl is incorrect
     *
     * @param w new width
     *
     * @param h new height
     */
    private fun setNewResolution(w: Double, h: Double) {
        targetWidth.set(w)
        targetHeight.set(h)
        computeScaledSize()

        val root = fxScene.root

        // clear listener
        // main key event handler
        fxScene.removeEventFilter(KeyEvent.ANY, keyHandler)

        // main mouse event handler
        fxScene.removeEventFilter(MouseEvent.ANY, mouseHandler)

        // reroute any events to current state input
        fxScene.removeEventFilter(EventType.ROOT, genericHandler)

        // clear root of previous JavaFX scene
        fxScene.root = Pane()

        // create and init new JavaFX scene
        fxScene = Scene(root)

        // main key event handler
        fxScene.addEventFilter(KeyEvent.ANY, keyHandler)

        // main mouse event handler
        fxScene.addEventFilter(MouseEvent.ANY, mouseHandler)

        // reroute any events to current state input
        fxScene.addEventFilter(EventType.ROOT, genericHandler)

        stage.scene = fxScene
        if (settings.isFullScreen) {
            stage.isFullScreen = true
        }
    }

    /**
     * Set new scene dimension. This will change the video output
     * resolution and adapt all subsystems.
     *
     * @param dimension scene dimension
     */
    override fun setSceneDimension(dimension: SceneDimension) {
        if (sceneDimensions.contains(dimension)) {
            log.debug { "Setting scene dimension: $dimension" }
            setNewResolution(dimension.width, dimension.height)
        } else {
            log.warning { "$dimension is not supported!" }
        }
    }

    private lateinit var dialogState: DialogSubState

    private fun initDialogBox() {
        dialogState = DialogSubState
    }

    override fun showMessageBox(message: String, callback: Runnable) {
        dialogState.showMessageBox(message, callback)
    }

    /**
     * Shows a blocking message box with YES and NO buttons. The callback is
     * invoked with the user answer as parameter.
     *
     * @param message        message to show
     *
     * @param resultCallback the function to be called
     */
    override fun showConfirmationBox(message: String, resultCallback: Consumer<Boolean>) {
        dialogState.showConfirmationBox(message, resultCallback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     * *
     * @param filter  filter to validate input
     * *
     * @param resultCallback the function to be called
     */
    override fun showInputBox(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        dialogState.showInputBox(message, filter, resultCallback)
    }

    override fun showInputBoxWithCancel(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        dialogState.showInputBoxWithCancel(message, filter, resultCallback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param error the error to show
     */
    override fun showErrorBox(error: Throwable) {
        dialogState.showErrorBox(error)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param errorMessage error message to show
     *
     * @param callback the function to be called when dialog is dismissed
     */
    override fun showErrorBox(errorMessage: String, callback: Runnable) {
        dialogState.showErrorBox(errorMessage, callback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) generic dialog.
     *
     * @param message the message
     *
     * @param content the content
     *
     * @param buttons buttons present
     */
    override fun showBox(message: String, content: Node, vararg buttons: Button) {
        dialogState.showBox(message, content, *buttons)
    }

    override fun showProgressBox(message: String): UIDialogHandler {
        return dialogState.showProgressBox(message)
    }

    override fun save(profile: UserProfile) {
        log.debug("Saving data to profile")

        val bundle = Bundle("scene")
        bundle.put("sizeW", getTargetWidth())
        bundle.put("sizeH", getTargetHeight())

        bundle.log()
        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        log.debug("Loading data from profile")

        val bundle = profile.getBundle("scene")
        bundle.log()

        setNewResolution(bundle.get<Double>("sizeW"), bundle.get<Double>("sizeH"))
    }
}