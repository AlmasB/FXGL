/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.scene.Scene
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.ImageCursor
import javafx.scene.effect.Effect
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BackgroundImage
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle

/**
 * Base class for all FXGL scenes.
 */
abstract class FXGLScene
@JvmOverloads constructor(width: Int = FXGL.getAppWidth(), height: Int = FXGL.getAppHeight()) : Scene() {

    val viewport = Viewport(width.toDouble(), height.toDouble())

    val paddingTop = Rectangle()
    val paddingBot = Rectangle()
    val paddingLeft = Rectangle()
    val paddingRight = Rectangle()

    init {
        paddingTop.widthProperty().bind(root.prefWidthProperty())
        paddingTop.heightProperty().bind(contentRoot.translateYProperty())

        paddingBot.translateYProperty().bind(root.prefHeightProperty().subtract(paddingTop.heightProperty()))
        paddingBot.widthProperty().bind(root.prefWidthProperty())
        paddingBot.heightProperty().bind(paddingTop.heightProperty())

        paddingLeft.widthProperty().bind(contentRoot.translateXProperty())
        paddingLeft.heightProperty().bind(root.prefHeightProperty())

        paddingRight.translateXProperty().bind(root.prefWidthProperty().subtract(paddingLeft.widthProperty()))
        paddingRight.widthProperty().bind(contentRoot.translateXProperty())
        paddingRight.heightProperty().bind(root.prefHeightProperty())

        root.children.addAll(paddingTop, paddingBot, paddingLeft, paddingRight)
    }

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
     * Makes cursor invisible.
     */
    fun setCursorInvisible() {
        root.cursor = Cursor.NONE
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

    override fun bindSize(scaledWidth: DoubleProperty, scaledHeight: DoubleProperty, scaleRatioX: DoubleProperty, scaleRatioY: DoubleProperty) {
        super.bindSize(scaledWidth, scaledHeight, scaleRatioX, scaleRatioY)

        // in addition, we bind content root x and y since viewport can change
        contentRoot.translateXProperty().bind(scaledWidth.divide(2).subtract(scaleRatioX.multiply(viewport.width).divide(2)))
        contentRoot.translateYProperty().bind(scaledHeight.divide(2).subtract(scaleRatioY.multiply(viewport.height).divide(2)))
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