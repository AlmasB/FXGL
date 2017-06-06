/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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