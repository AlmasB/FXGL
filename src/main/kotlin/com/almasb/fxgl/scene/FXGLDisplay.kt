/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.scene

import com.almasb.easyio.serialization.Bundle
import com.almasb.fxeventbus.EventBus
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.asset.FXGLAssets
import com.almasb.fxgl.event.DisplayEvent
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import com.almasb.fxgl.settings.SceneDimension
import com.almasb.fxgl.settings.UserProfile
import com.google.inject.Inject
import com.google.inject.Singleton
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
import javafx.scene.control.Dialog
import javafx.scene.control.ProgressIndicator
import javafx.scene.input.KeyCombination
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
 * Display service. Provides access to dialogs and display settings.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
@Singleton
class FXGLDisplay
@Inject
private constructor(private val stage: Stage,
                    /**
                     * Underlying JavaFX scene. We only use 1 scene to avoid
                     * problems in fullscreen mode. Switching between scenes
                     * in FS mode will otherwise temporarily toggle FS.
                     */
                    private var fxScene: Scene) : Display {

    private val log = FXGL.getLogger(javaClass)

    private val currentScene = ReadOnlyObjectWrapper<FXGLScene>()

    private val scenes = ArrayList<FXGLScene>()

    private val targetWidth: DoubleProperty
    private val targetHeight: DoubleProperty
    private val scaledWidth: DoubleProperty
    private val scaledHeight: DoubleProperty
    private val scaleRatio: DoubleProperty

    private val css: CSS

    private val eventBus: EventBus

    private val settings: ReadOnlyGameSettings

    private val sceneDimensions = ArrayList<SceneDimension>()

    /*
     * Since FXGL scenes are not JavaFX nodes they don't get notified of events.
     * This is a desired behavior because we only have 1 JavaFX scene for all FXGL scenes.
     * So we copy the occurred event and reroute to whichever FXGL scene is current.
     */
    private val fxToFXGLFilter: EventHandler<Event> = EventHandler { event ->
        val copy = event.copyFor(null, null)
        getCurrentScene().fireEvent(copy)
    }

    init {
        settings = FXGL.getSettings()

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

        initStage()
        initDialogBox()

        fxScene.addEventFilter(EventType.ROOT, fxToFXGLFilter)

        computeSceneSettings(settings.width.toDouble(), settings.height.toDouble())
        computeScaledSize()

        eventBus = FXGL.getEventBus()

        log.debug { "Service [Display] initialized" }
        log.debug { "Using CSS: $css" }
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

                if (!dialog.isShowing) {
                    showConfirmationBox("Exit the game?", { yes ->
                        if (yes)
                            eventBus.fireEvent(DisplayEvent(DisplayEvent.CLOSE_REQUEST))
                    })
                }
            }

            setOnShown {
                log.debug("Showing stage")
                log.debug("Root size: " + stage.scene.root.layoutBounds.width + "x" + stage.scene.root.layoutBounds.height)
                log.debug("Scene size: " + stage.scene.width + "x" + stage.scene.height)
                log.debug("Stage size: " + stage.width + "x" + stage.height)
            }

            icons.add(FXGLAssets.UI_ICON)

            if (settings.isFullScreen) {
                fullScreenExitHint = ""
                // don't let the user to exit FS mode manually
                fullScreenExitKeyCombination = KeyCombination.NO_MATCH
                isFullScreen = true
            }

            sizeToScene()
        }
    }

    /**
     * Register an FXGL scene to be managed by display settings.
     *
     * @param scene the scene
     */
    override fun registerScene(scene: FXGLScene) {
        scenes.add(scene)

        scene.bindSize(scaledWidth, scaledHeight, scaleRatio)
        scene.appendCSS(css)
    }

    /**
     * Set current FXGL scene. The scene will be immediately displayed.
     *
     * @param scene the scene
     */
    override fun setScene(scene: FXGLScene) {
        if (getCurrentScene() != null) {
            getCurrentScene().activeProperty().set(false)
        }

        currentScene.set(scene)
        scene.activeProperty().set(true)

        fxScene.root = scene.root
    }

    /**
     * @return current scene property
     */
    override fun currentSceneProperty() = currentScene.readOnlyProperty

    override fun getBounds() = if (settings.isFullScreen) Screen.getPrimary().bounds
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
        val bounds = bounds

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
        val bounds = bounds

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
        fxScene.removeEventFilter(EventType.ROOT, fxToFXGLFilter)
        // clear root of previous JavaFX scene
        fxScene.root = Pane()

        // create and init new JavaFX scene
        fxScene = Scene(root)
        fxScene.addEventFilter(EventType.ROOT, fxToFXGLFilter)
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

    private lateinit var dialog: DialogPane

    private fun initDialogBox() {
        dialog = DialogPane(this)
        dialog.setOnShown {
            fxScene.removeEventFilter(EventType.ROOT, fxToFXGLFilter)
            eventBus.fireEvent(DisplayEvent(DisplayEvent.DIALOG_OPENED))
        }
        dialog.setOnClosed {
            eventBus.fireEvent(DisplayEvent(DisplayEvent.DIALOG_CLOSED))
            fxScene.addEventFilter(EventType.ROOT, fxToFXGLFilter)
        }
    }

    /**
     * Shows given dialog and blocks execution of the game until the dialog is
     * dismissed. The provided callback will be called with the dialog result as
     * parameter when the dialog closes.
     *
     * @param dialog         JavaFX dialog
     *
     * @param resultCallback the function to be called
     */
    override fun <T> showDialog(dialog: Dialog<T>, resultCallback: Consumer<T>) {
        eventBus.fireEvent(DisplayEvent(DisplayEvent.DIALOG_OPENED))

        dialog.initOwner(stage)
        dialog.setOnCloseRequest { e ->
            eventBus.fireEvent(DisplayEvent(DisplayEvent.DIALOG_CLOSED))

            resultCallback.accept(dialog.result)
        }
        dialog.show()
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button. On
     * button press, the message box will be dismissed.
     *
     * @param message the message to show
     */
    override fun showMessageBox(message: String) {
        dialog.showMessageBox(message)
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
        dialog.showConfirmationBox(message, resultCallback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) message box with OK button and input field. The callback
     * is invoked with the field text as parameter.
     *
     * @param message        message to show
     *
     * @param resultCallback the function to be called
     */
    override fun showInputBox(message: String, resultCallback: Consumer<String>) {
        dialog.showInputBox(message, resultCallback)
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
        dialog.showInputBox(message, filter, resultCallback)
    }

    override fun showInputBoxWithCancel(message: String, filter: Predicate<String>, resultCallback: Consumer<String>) {
        dialog.showInputBoxWithCancel(message, filter, resultCallback)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param error the error to show
     */
    override fun showErrorBox(error: Throwable) {
        dialog.showErrorBox(error)
    }

    /**
     * Shows a blocking (stops game execution, method returns normally) dialog with the error.
     *
     * @param errorMessage error message to show
     *
     * @param callback the function to be called when dialog is dismissed
     */
    override fun showErrorBox(errorMessage: String, callback: Runnable) {
        dialog.showErrorBox(errorMessage, callback)
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
        dialog.showBox(message, content, *buttons)
    }

    override fun showProgressBox(message: String): DialogHandler {
        val progress = ProgressIndicator()
        progress.setPrefSize(200.0, 200.0)

        val btn = Button()
        btn.isVisible = false

        showBox(message, progress, btn)

        return DialogHandler(btn)
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