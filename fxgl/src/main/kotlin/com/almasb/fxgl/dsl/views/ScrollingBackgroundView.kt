/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.views

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.texture.ScrollingView
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Orientation
import javafx.scene.image.Image

/**
 * Scrolling view that binds to viewport.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ScrollingBackgroundView
@JvmOverloads constructor(
        image: Image,
        viewWidth: Double,
        viewHeight: Double,
        orientation: Orientation = Orientation.HORIZONTAL,

        private val speed: Double = 1.0
) : ScrollingView(image, viewWidth, viewHeight, orientation) {

    @Deprecated("")
    @JvmOverloads constructor(texture: Texture,
                orientation: Orientation = Orientation.HORIZONTAL,
                speed: Double = 1.0)
            : this(
            texture.image,
            FXGL.getGameScene().viewport.width,
            FXGL.getGameScene().viewport.height,
            orientation,
            speed
    )

    init {
        val viewport = FXGL.getGameScene().viewport

        if (orientation == Orientation.HORIZONTAL) {
            translateXProperty().addListener { _, _, x ->

                scrollX = x.toDouble() * speed
            }

            translateXProperty().bind(viewport.xProperty())
        } else {
            translateYProperty().addListener { _, _, y ->

                scrollY = y.toDouble() * speed
            }

            translateYProperty().bind(viewport.yProperty())
        }
    }
}
