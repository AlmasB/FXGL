/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.test.RunWithFX
import javafx.geometry.HorizontalDirection
import javafx.geometry.Rectangle2D
import javafx.geometry.VerticalDirection
import javafx.scene.Node
import javafx.scene.effect.BlendMode
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class TextureTest {

    private lateinit var texture: Texture

    companion object {
        private val image: Image = WritableImage(320, 320)
    }

    @BeforeEach
    fun setUp() {
        texture = Texture(image)
    }

    @Test
    fun `Width and Height`() {
        assertThat(texture.width, `is`(320.0))
        assertThat(texture.height, `is`(320.0))
    }

    @Test
    fun `Copy creates new node with same image`() {
        val copy = texture.copy()

        assertThat(texture !== copy, `is`(true))
        assertThat(texture.image === copy.image, `is`(true))
    }

    @Test
    fun `Test subtexture`() {
        val sub = texture.subTexture(Rectangle2D(0.0, 0.0, 128.0, 64.0))

        assertThat(sub.image.width, `is`(128.0))
        assertThat(sub.image.height, `is`(64.0))
    }

    @Test
    fun `Fail if subtexture larger or x y are negative`() {
        assertAll(
                Executable {
                    assertThrows(IllegalArgumentException::class.java) {
                        texture.subTexture(Rectangle2D(0.0, 0.0, 400.0, 300.0))
                    }
                },

                Executable {
                    assertThrows(IllegalArgumentException::class.java) {
                        texture.subTexture(Rectangle2D(0.0, 0.0, 300.0, 400.0))
                    }
                },

                Executable {
                    assertThrows(IllegalArgumentException::class.java) {
                        texture.subTexture(Rectangle2D(-10.0, 0.0, 400.0, 300.0))
                    }
                },

                Executable {
                    assertThrows(IllegalArgumentException::class.java) {
                        texture.subTexture(Rectangle2D(0.0, -10.0, 300.0, 400.0))
                    }
                }
        )
    }

    @Test
    fun `Test supertexture`() {
        val super1 = texture.superTexture(texture, HorizontalDirection.RIGHT)

        assertThat(super1.image.width, `is`(640.0))
        assertThat(super1.image.height, `is`(320.0))

        val super2 = texture.superTexture(texture, HorizontalDirection.LEFT)

        assertThat(super2.image.width, `is`(640.0))
        assertThat(super2.image.height, `is`(320.0))

        val super3 = texture.superTexture(texture, VerticalDirection.UP)

        assertThat(super3.image.width, `is`(320.0))
        assertThat(super3.image.height, `is`(640.0))

        val super4 = texture.superTexture(texture, VerticalDirection.DOWN)

        assertThat(super4.image.width, `is`(320.0))
        assertThat(super4.image.height, `is`(640.0))

        // now test with different size textures, so transparent pixels will be added to compensate

        val diffSizeTexture1 = Texture(WritableImage(10, 320))

        val super5 = texture.superTexture(diffSizeTexture1, VerticalDirection.DOWN)

        assertThat(super5.image.width, `is`(320.0))
        assertThat(super5.image.height, `is`(640.0))

        val super6 = diffSizeTexture1.superTexture(texture, VerticalDirection.DOWN)

        assertThat(super6.image.width, `is`(320.0))
        assertThat(super6.image.height, `is`(640.0))

        val diffSizeTexture2 = Texture(WritableImage(320, 10))

        val super7 = texture.superTexture(diffSizeTexture2, HorizontalDirection.RIGHT)

        assertThat(super7.image.width, `is`(640.0))
        assertThat(super7.image.height, `is`(320.0))

        val super8 = diffSizeTexture2.superTexture(texture, HorizontalDirection.RIGHT)

        assertThat(super8.image.width, `is`(640.0))
        assertThat(super8.image.height, `is`(320.0))
    }

    @Test
    fun `Color conversions`() {
        assertThat(texture.toGrayscale().image, `is`(not(image)))
        assertThat(texture.brighter().image, `is`(not(image)))
        assertThat(texture.darker().image, `is`(not(image)))
        assertThat(texture.invert().image, `is`(not(image)))
        assertThat(texture.desaturate().image, `is`(not(image)))
        assertThat(texture.saturate().image, `is`(not(image)))
        assertThat(texture.discolor().image, `is`(not(image)))
        assertThat(texture.multiplyColor(Color.BLUE).image, `is`(not(image)))
        assertThat(texture.toColor(Color.GRAY).image, `is`(not(image)))
        assertThat(texture.replaceColor(Color.WHITE, Color.GRAY).image, `is`(not(image)))
        assertThat(texture.replaceColor(Color.TRANSPARENT, Color.GRAY).image, `is`(not(image)))
        assertThat(texture.transparentColor(Color.PURPLE).image, `is`(not(image)))
        assertThat(texture.outline(Color.PURPLE).image, `is`(not(image)))
    }

    @ParameterizedTest
    @EnumSource(BlendMode::class)
    fun `Blending`(blend: BlendMode) {
        assertThat(texture.blend(WritableImage(320, 320), blend).image, `is`(not(image)))
        assertThat(ColoredTexture(320, 320, Color.BLACK).blend(ColoredTexture(320, 320, Color.WHITE).image, blend).image, `is`(not(image)))
        assertThat(ColoredTexture(320, 320, Color.WHITE).blend(ColoredTexture(320, 320, Color.BLACK).image, blend).image, `is`(not(image)))
    }

    @Test
    fun `Set from another texture`() {
        val newImage = WritableImage(320, 320)

        assertThat(texture.image, `is`(image))

        texture.set(Texture(newImage))

        assertThat(texture.image, `is`<Image>(newImage))
    }

    @Test
    fun `Get renderable node`() {
        assertThat(texture.node, `is`<Node>(texture))
    }

    @Test
    fun `Dispose`() {
        assertNotNull(texture.image)

        texture.dispose()

        assertNull(texture.image)
    }
}