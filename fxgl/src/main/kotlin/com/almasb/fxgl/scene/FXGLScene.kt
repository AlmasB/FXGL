package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.texture
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.time.Timer
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Point2D
import javafx.scene.ImageCursor
import javafx.scene.effect.Effect
import javafx.scene.image.Image
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import javafx.scene.transform.Scale

/**
 * Base class for all FXGL scenes.
 */
abstract class FXGLScene
@JvmOverloads constructor(width: Int = FXGL.getAppWidth(), height: Int = FXGL.getAppHeight()) : Scene() {

    /**
     * Top-level root node.
     */
    /**
     * @return top-level root node of the scene
     */
    val root = Pane()

    /**
     * Root node for content.
     */
    /**
     * @return root node of the content
     */
    val contentRoot: Pane


    /**
     * @return viewport
     */
    val viewport: Viewport

    /**
     * @return width
     */
    val width: Double
        get() = root.prefWidth

    /**
     * @return height
     */
    val height: Double
        get() = root.prefHeight



    /**
     * @return currently applied effect or null if no effect is applied
     */
    /**
     * Applies given effect to the scene.
     *
     * @param effect the effect to apply
     */
    var effect: Effect?
        get() = contentRoot.effect
        set(effect) {
            contentRoot.effect = effect
        }

    private val active = SimpleBooleanProperty(false)

    init {
        root.background = null

        contentRoot = Pane()
        contentRoot.background = null

        root.children.addAll(contentRoot)

        viewport = Viewport(width.toDouble(), height.toDouble())

        if (FXGL.isDesktop()) {
            setCursor("fxgl_default.png", Point2D(7.0, 6.0))
        }
    }
















    /**
     * Removes any effects applied to the scene.
     */
    fun clearEffect() {
        effect = null
    }

    /**
     * Sets global game cursor using given name to find
     * the image cursor within assets/ui/cursors/.
     * Hotspot is location of the pointer end on the image.
     *
     * @param imageName name of image file
     * @param hotspot hotspot location
     */
    fun setCursor(imageName: String, hotspot: Point2D) {
        root.cursor = ImageCursor(FXGL.getAssetLoader().loadCursorImage(imageName),
                hotspot.x, hotspot.y)
    }

    /**
     * If a scene is active it is being shown by the display.
     *
     * @return active property
     */
    fun activeProperty(): BooleanProperty {
        return active
    }

    fun appendCSS(css: CSS) {
        root.stylesheets.add(css.externalForm)
    }

    fun clearCSS() {
        root.stylesheets.clear()
    }

    fun bindSize(scaledWidth: DoubleProperty, scaledHeight: DoubleProperty, scaleRatioX: DoubleProperty, scaleRatioY: DoubleProperty) {
        root.prefWidthProperty().bind(scaledWidth)
        root.prefHeightProperty().bind(scaledHeight)

        val scale = Scale()
        scale.xProperty().bind(scaleRatioX)
        scale.yProperty().bind(scaleRatioY)
        root.transforms.setAll(scale)
    }

    fun setBackgroundColor(color: Paint) {
        root.background = Background(BackgroundFill(color, null, null))
    }

    /**
     * Convenience method to load the texture and repeat as often as needed to cover the background.
     */
    fun setBackgroundRepeat(textureName: String) {
        setBackgroundRepeat(texture(textureName).image)
    }

    /**
     * The image is repeated as often as needed to cover the background.
     */
    fun setBackgroundRepeat(image: Image) {
        root.background = Background(BackgroundImage(image,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null))
    }
}