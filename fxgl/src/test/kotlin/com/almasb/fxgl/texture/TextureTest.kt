/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.texture

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import javafx.geometry.HorizontalDirection
import javafx.geometry.Rectangle2D
import javafx.geometry.VerticalDirection
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextureTest {

    private lateinit var texture: Texture

    companion object {
        private lateinit var image: Image

        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())

            image = WritableImage(320, 320)
        }
    }

    @Before
    fun setUp() {
        texture = Texture(image)
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

    @Test(expected = IllegalArgumentException::class)
    fun `Fail if subtexture larger`() {
        texture.subTexture(Rectangle2D(0.0, 0.0, 400.0, 400.0))
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
    }
}