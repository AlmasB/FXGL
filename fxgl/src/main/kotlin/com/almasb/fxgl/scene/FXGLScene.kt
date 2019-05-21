package com.almasb.fxgl.scene

import com.almasb.fxgl.dsl.FXGL
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

    val viewport = Viewport(width.toDouble(), height.toDouble())

    val width: Double
        get() = root.prefWidth

    val height: Double
        get() = root.prefHeight

    /**
     * Applies given effect to the scene.
     *
     * @param effect the effect to apply
     * @return currently applied effect or null if no effect is applied
     */
    var effect: Effect?
        get() = contentRoot.effect
        set(effect) {
            contentRoot.effect = effect
        }

    private val active = SimpleBooleanProperty(false)

    /**
     * Removes any effects applied to the scene.
     */
    fun clearEffect() {
        effect = null
    }

    /**
     * @param image cursor image
     * @param hotspot hotspot location
     */
    fun setCursor(image: Image, hotspot: Point2D) {
        root.cursor = ImageCursor(image, hotspot.x, hotspot.y)
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
        setBackgroundRepeat(FXGL.texture(textureName).image)
    }

    /**
     * The image is repeated as often as needed to cover the background.
     */
    fun setBackgroundRepeat(image: Image) {
        root.background = Background(BackgroundImage(image,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null))
    }
}