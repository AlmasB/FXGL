package com.almasb.fxgl.texture

import com.almasb.fxgl.app.FXGLMock
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ColoredTextureTest {

    private lateinit var texture: ColoredTexture

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @BeforeEach
    fun setUp() {
        texture = ColoredTexture(320, 120, Color.BLUE)
    }

    @Test
    fun `Width and Height`() {
        assertThat(texture.width, CoreMatchers.`is`(320.0))
        assertThat(texture.height, CoreMatchers.`is`(120.0))
    }

    @Test
    fun `Color`() {
        var count = 0

        val reader = texture.image.pixelReader

        for (y in 0 until 120) {
            for (x in 0 until 320) {
                assertThat(reader.getColor(x, y), `is`(Color.BLUE))

                count++
            }
        }

        assertThat(count, `is`(120*320))
    }
}