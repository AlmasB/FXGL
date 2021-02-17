/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.texture

import com.almasb.fxgl.test.RunWithFX
import javafx.geometry.HorizontalDirection.RIGHT
import javafx.geometry.Orientation
import javafx.geometry.VerticalDirection.DOWN
import javafx.scene.canvas.Canvas
import javafx.scene.paint.Color
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.closeTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RunWithFX::class)
class ScrollingViewTest {

    private lateinit var viewHor: ScrollingView
    private lateinit var viewVer: ScrollingView

    @Test
    fun `Scrolling horizontally`() {
        val left = ColoredTexture(200, 200, Color.WHITE)
        val right = ColoredTexture(200, 200, Color.BLACK)

        viewHor = ScrollingView(left.superTexture(right, RIGHT).image, orientation = Orientation.HORIZONTAL)

        viewHor.scrollX = 200.0

        val canvas = viewHor.childrenUnmodifiable[0] as Canvas

        val image = toImage(canvas)

        for (y in 0..199) {
            for (x in 0..199) {
                val color = image.pixelReader.getColor(x, y)

                assertThat(color.rgbSum(), closeTo(0.0, 0.4))
            }
        }

        for (y in 0..199) {
            for (x in 200..399) {
                val color = image.pixelReader.getColor(x, y)

                assertThat(color.rgbSum(), closeTo(3.0, 0.4))
            }
        }
    }

    @Test
    fun `Scrolling vertically`() {
        val up = ColoredTexture(200, 200, Color.WHITE)
        val down = ColoredTexture(200, 200, Color.BLACK)

        viewVer = ScrollingView(up.superTexture(down, DOWN).image, orientation = Orientation.VERTICAL)

        viewVer.scrollY = 200.0

        val canvas = viewVer.childrenUnmodifiable[0] as Canvas

        val image = toImage(canvas)

        for (y in 0..199) {
            for (x in 0..199) {
                val color = image.pixelReader.getColor(x, y)

                assertThat(color.rgbSum(), closeTo(0.0, 0.4))
            }
        }

        for (y in 200..399) {
            for (x in 0..199) {
                val color = image.pixelReader.getColor(x, y)

                assertThat(color.rgbSum(), closeTo(3.0, 0.4))
            }
        }
    }
}