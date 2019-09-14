/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.texture

import javafx.geometry.HorizontalDirection
import javafx.geometry.Rectangle2D
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimatedTextureTest {

    private lateinit var texture: AnimatedTexture

    companion object {
        // BLACK | WHITE
        private val image: Image = ColoredTexture(320, 320, Color.BLACK)
                .superTexture(ColoredTexture(320, 320, Color.WHITE), HorizontalDirection.RIGHT).image
    }

    @BeforeEach
    fun setUp() {
        texture = Texture(image).toAnimatedTexture(2, Duration.seconds(1.0))
    }

    @Test
    fun `Next frame updates after counter reaches frame duration`() {
        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        texture.play()

        texture.onUpdate(0.1)
        texture.onUpdate(0.1)
        texture.onUpdate(0.1)
        texture.onUpdate(0.1)

        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        // should now move one frame
        texture.onUpdate(0.1)

        assertThat(texture.viewport, `is`(Rectangle2D(320.0, 0.0, 320.0, 320.0)))
    }

    @Test
    fun `Play animation ends with last frame`() {
        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        texture.play()

        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        // move single frame
        texture.onUpdate(0.5)

        assertThat(texture.viewport, `is`(Rectangle2D(320.0, 0.0, 320.0, 320.0)))

        // animation is complete with play() we stop on last frame
        texture.onUpdate(0.5)

        assertThat(texture.viewport, `is`(Rectangle2D(320.0, 0.0, 320.0, 320.0)))

        texture.onUpdate(2.0)

        assertThat(texture.viewport, `is`(Rectangle2D(320.0, 0.0, 320.0, 320.0)))
    }

    @Test
    fun `Loop animation repeats from 1st frame`() {
        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        texture.loop()

        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        // move single frame
        texture.onUpdate(0.5)

        assertThat(texture.viewport, `is`(Rectangle2D(320.0, 0.0, 320.0, 320.0)))

        // animation is complete with loop() we move to 1st frame
        texture.onUpdate(0.5)

        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        // even if we advanced by 2sec we still move one frame at a time

        texture.onUpdate(2.0)

        assertThat(texture.viewport, `is`(Rectangle2D(320.0, 0.0, 320.0, 320.0)))
    }

    @Test
    fun `Stop animation ends with 1st frame`() {
        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        texture.loop()

        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        // move single frame
        texture.onUpdate(0.5)

        assertThat(texture.viewport, `is`(Rectangle2D(320.0, 0.0, 320.0, 320.0)))

        // animation is complete with loop() we move to 1st frame
        texture.onUpdate(0.5)

        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))

        // we stop so 1st frame

        texture.stop()
        texture.onUpdate(2.0)

        assertThat(texture.viewport, `is`(Rectangle2D(0.0, 0.0, 320.0, 320.0)))
    }

    @Test
    fun `Run code on cycle finished`() {
        var count = 0

        texture.loop()
        texture.onUpdate(0.5)
        texture.onUpdate(0.5)

        assertThat(count, `is`(0))

        texture.onCycleFinished = Runnable { count++ }

        texture.onUpdate(0.5)
        texture.onUpdate(0.5)
        assertThat(count, `is`(1))

        // we only advance 1 frame at a time, so
        texture.onUpdate(0.5)
        assertThat(count, `is`(1))

        texture.onUpdate(0.5)
        assertThat(count, `is`(2))
    }
}