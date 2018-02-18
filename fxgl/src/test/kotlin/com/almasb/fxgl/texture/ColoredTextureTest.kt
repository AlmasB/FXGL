package com.almasb.fxgl.texture

import com.almasb.fxgl.app.FXGLMock
import javafx.geometry.HorizontalDirection
import javafx.geometry.Rectangle2D
import javafx.geometry.VerticalDirection
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ColoredTextureTest {

    private lateinit var texture: ColoredTexture

    companion object {
        private lateinit var image: Image

        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()

            image = WritableImage(320, 320)
        }
    }

    @BeforeEach
    fun setUp() {
        texture = ColoredTexture(320, 320, Color.BLUE)
    }

    @Test
    fun `Width and Height`() {
        MatcherAssert.assertThat(texture.width, CoreMatchers.`is`(320.0))
        MatcherAssert.assertThat(texture.height, CoreMatchers.`is`(320.0))
    }

    @Test
    fun `Copy creates new node with same image`() {
        val copy = texture.copy()

        MatcherAssert.assertThat(texture !== copy, CoreMatchers.`is`(true))
        MatcherAssert.assertThat(texture.image === copy.image, CoreMatchers.`is`(true))
    }

    @Test
    fun `Test subtexture`() {
        val sub = texture.subTexture(Rectangle2D(0.0, 0.0, 128.0, 64.0))

        MatcherAssert.assertThat(sub.image.width, CoreMatchers.`is`(128.0))
        MatcherAssert.assertThat(sub.image.height, CoreMatchers.`is`(64.0))
    }

    @Test
    fun `Fail if subtexture larger`() {
        Assertions.assertThrows(IllegalArgumentException::class.java, {
            texture.subTexture(Rectangle2D(0.0, 0.0, 400.0, 400.0))
        })
    }

    @Test
    fun `Test supertexture`() {
        val super1 = texture.superTexture(texture, HorizontalDirection.RIGHT)

        MatcherAssert.assertThat(super1.image.width, CoreMatchers.`is`(640.0))
        MatcherAssert.assertThat(super1.image.height, CoreMatchers.`is`(320.0))

        val super2 = texture.superTexture(texture, HorizontalDirection.LEFT)

        MatcherAssert.assertThat(super2.image.width, CoreMatchers.`is`(640.0))
        MatcherAssert.assertThat(super2.image.height, CoreMatchers.`is`(320.0))

        val super3 = texture.superTexture(texture, VerticalDirection.UP)

        MatcherAssert.assertThat(super3.image.width, CoreMatchers.`is`(320.0))
        MatcherAssert.assertThat(super3.image.height, CoreMatchers.`is`(640.0))

        val super4 = texture.superTexture(texture, VerticalDirection.DOWN)

        MatcherAssert.assertThat(super4.image.width, CoreMatchers.`is`(320.0))
        MatcherAssert.assertThat(super4.image.height, CoreMatchers.`is`(640.0))
    }
}