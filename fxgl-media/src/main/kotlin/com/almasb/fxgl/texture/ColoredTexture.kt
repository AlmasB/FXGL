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
 * Represents a simple single colored 2D image which can be set as view for an entity.
 * The size ratio and viewport can be modified as necessary.
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

        for(i in 0 until width){
            for(j in 0 until height){
                writer.setColor(i, j, color)
            }
        }

        return image
    }

}