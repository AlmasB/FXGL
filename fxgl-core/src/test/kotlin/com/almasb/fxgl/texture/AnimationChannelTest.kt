/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.texture

import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimationChannelTest {

    companion object {
        private val image: Image = WritableImage(320, 320)
    }

    @Test
    fun `Channel 1`() {
        val channel = AnimationChannel(image, 10, 32, 32, Duration.seconds(1.0), 0, 9)

        assertThat(channel.frameDuration, `is`(0.1))
        assertThat(channel.getFrameWidth(0), `is`(32))
        assertThat(channel.getFrameHeight(0), `is`(32))
        assertThat(channel.image, `is`(image))

        assertThat(channel.sequence, contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9))
    }

    @Test
    fun `Channel 2`() {
        val channel = AnimationChannel(image, 10, 32, 32, Duration.seconds(3.0), 10, 12)

        assertThat(channel.frameDuration, `is`(1.0))
        assertThat(channel.getFrameWidth(0), `is`(32))
        assertThat(channel.getFrameHeight(0), `is`(32))
        assertThat(channel.image, `is`(image))

        assertThat(channel.sequence, contains(10, 11, 12))
    }

    @Test
    fun `Channel 3`() {
        val channel = AnimationChannel(image, Duration.seconds(1.0), 10, listOf(
                AnimationChannelData(0, 15, 10, 10),
                AnimationChannelData(16, 19, 40, 40)
        ))

        assertThat(channel.frameDuration, `is`(0.05))
        assertThat(channel.getFrameWidth(0), `is`(10))
        assertThat(channel.getFrameHeight(0), `is`(10))
        assertThat(channel.getFrameWidth(16), `is`(40))
        assertThat(channel.getFrameHeight(16), `is`(40))
        assertThat(channel.image, `is`(image))

        assertThat(channel.sequence, contains(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19))
    }

    @Test
    fun `Channel 4`() {
        val channel = AnimationChannel(listOf(image, image), Duration.seconds(2.0), 2)

        assertThat(channel.frameDuration, `is`(1.0))
        assertThat(channel.getFrameWidth(0), `is`(320))
        assertThat(channel.getFrameHeight(0), `is`(320))
        assertThat(channel.getFrameWidth(1), `is`(320))
        assertThat(channel.getFrameHeight(1), `is`(320))
        assertThat(channel.image, `is`(not(image)))

        assertThat(channel.sequence, contains(0, 1))
    }
}