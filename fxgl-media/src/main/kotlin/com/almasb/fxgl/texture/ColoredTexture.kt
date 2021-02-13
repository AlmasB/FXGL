/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color

/**
 * Represents a single colored 2D image view.
 *
 * @author Krisztián Nagy (Valdar) (okt.valdar@gmail.com)
 */
class ColoredTexture(width: Int, height: Int, color: Color) : Texture() {

    init {
        image = createImageFromColor(width, height, color)
    }

    private fun createImageFromColor(width: Int, height: Int, color: Color): Image {
        val image = WritableImage(width, height)
        val writer = image.pixelWriter

        for (x in 0 until width){
            for (y in 0 until height){
                writer.setColor(x, y, color)
            }
        }

        return image
    }
}