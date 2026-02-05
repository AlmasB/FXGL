/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.texture

import javafx.geometry.HorizontalDirection
import javafx.geometry.VerticalDirection
import javafx.scene.paint.Color
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ImagesTest {

    @Test
    fun `Flip horizontally`() {
        val left = ColoredTexture(200, 200, Color.WHITE)
        val right = ColoredTexture(200, 100, Color.BLACK).superTexture(ColoredTexture(200, 100, Color.RED), VerticalDirection.DOWN)

        val texture = left.superTexture(right, HorizontalDirection.RIGHT)

        val result = flipHorizontally(texture.image)

        val expected = right.superTexture(left, HorizontalDirection.RIGHT)

        assertThat("Horizontal flip is not correct", matchPixels(expected, Texture(result)))
    }

    @Test
    fun `Flip vertically`() {
        val left = ColoredTexture(200, 200, Color.WHITE)
        val right = ColoredTexture(200, 100, Color.BLACK).superTexture(ColoredTexture(200, 100, Color.RED), VerticalDirection.DOWN)

        val texture = left.superTexture(right, HorizontalDirection.RIGHT)

        val result = flipVertically(texture.image)

        val expected = left.superTexture(
                ColoredTexture(200, 100, Color.RED).superTexture(ColoredTexture(200, 100, Color.BLACK), VerticalDirection.DOWN),
                HorizontalDirection.RIGHT
        )

        assertThat("Vertical flip is not correct", matchPixels(expected, Texture(result)))
    }

    @Test
    fun `Flip diagonally`() {
        val left = ColoredTexture(200, 200, Color.WHITE)
        val right = ColoredTexture(200, 100, Color.BLACK).superTexture(ColoredTexture(200, 100, Color.RED), VerticalDirection.DOWN)

        val texture = left.superTexture(right, HorizontalDirection.RIGHT)

        val result = flipDiagonally(texture.image)

        val expected = ColoredTexture(200, 100, Color.RED).superTexture(ColoredTexture(200, 100, Color.BLACK), VerticalDirection.DOWN)
                .superTexture(left, HorizontalDirection.RIGHT)

        assertThat("Diagonal flip is not correct", matchPixels(expected, Texture(result)))
    }

    @Test
    fun `Interpolate intermediate images`() {
        val img1 = ColoredTexture(200, 200, Color.WHITE).image
        val img2 = ColoredTexture(200, 200, Color.RED).image
        val img3 = ColoredTexture(200, 200, Color.BLACK).image

        val result = interpolateIntermediateImages(listOf(img1, img2, img3), 10)
        assertThat(result.size, `is`(10*2 + 3))
        assertThat(result[0], `is`(img1))
        assertThat(result[11], `is`(img2))
        assertThat(result[22], `is`(img3))
    }

    @Test
    fun `Compare two images`() {
        var img1 = ColoredTexture(100, 100, Color.WHITE)
            .superTexture(ColoredTexture(100, 100, Color.BLACK), HorizontalDirection.RIGHT)
            .image

        var img2 = ColoredTexture(100, 100, Color.WHITE)
            .superTexture(ColoredTexture(100, 100, Color.RED), HorizontalDirection.RIGHT)
            .image

        var result = img1.compareStrict(img2)

        assertThat(result, `is`(0.5))

        // compare identical
        img1 = ColoredTexture(100, 100, Color.WHITE).image
        img2 = ColoredTexture(100, 100, Color.WHITE).image
        result = img1.compareStrict(img2)

        assertThat(result, `is`(1.0))

        // compare different sizes
        img2 = ColoredTexture(100, 50, Color.WHITE).image
        result = img1.compareStrict(img2)

        assertThat(result, `is`(0.0))
    }

    @Test
    fun `Convert to base64`() {
        val img1 = ColoredTexture(100, 100, Color.WHITE).image

        val s = img1.toBase64()

        assertThat(s, `is`(
            "iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAYAAABw4pVUAAAA/ElEQVR4Xu3RoQEAMAyAsP7/dOv3wBCJxDJLyryBvwyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTGkBhDYgyJMSTmAPgrrNZaawxDAAAAAElFTkSuQmCC"
        ))
    }
}