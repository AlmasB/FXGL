/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.animation

import com.almasb.fxgl.core.util.Consumer
import javafx.animation.Interpolator
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimationTest {

    @Test
    fun `Set animation interpolator`() {
        val i1 = Interpolators.ELASTIC.EASE_OUT()

        val builder = AnimationBuilder()
        val animValue = AnimatedValue(1.0, 5.0)

        val anim = builder
                .interpolator(i1)
                .duration(Duration.seconds(4.0))
                .build(animValue, Consumer {  })

        assertThat(anim.interpolator, `is`(i1))

        val i2 = Interpolators.BACK.EASE_IN()

        anim.interpolator = i2
        assertThat(anim.interpolator, `is`(i2))

        assertThat(anim.builder, `is`(builder))
        assertThat(anim.animatedValue, `is`(animValue))
    }

    @Test
    fun `Simple Animation`() {
        var count = 0.0

        val anim = AnimationBuilder()
                .interpolator(Interpolator.LINEAR)
                .duration(Duration.seconds(4.0))
                .build(AnimatedValue(1.0, 5.0), Consumer { count = it })

        assertFalse(anim.isAnimating)
        assertFalse(anim.isAutoReverse)
        assertFalse(anim.isReverse)
        assertFalse(anim.isPaused)
        assertThat(count, `is`(0.0))

        anim.start()

        assertTrue(anim.isAnimating)
        assertFalse(anim.isPaused)
        assertThat(count, `is`(1.0))

        for (i in 2..5) {
            anim.onUpdate(1.0)

            assertThat(count, `is`(i.toDouble()))
        }

        assertFalse(anim.isAnimating)
    }

    @Test
    fun `Infinite Animation`() {
        var count = 0

        val anim = AnimationBuilder()
                .duration(Duration.seconds(2.0))
                .repeatInfinitely()
                .build(AnimatedValue(1, 3), Consumer { count = it })

        anim.start()

        for (i in 0..100) {
            assertThat(count, `is`(1))

            anim.onUpdate(1.0)

            assertThat(count, `is`(2))

            anim.onUpdate(1.0)

            assertThat(count, `is`(3))

            // push over the cycle
            anim.onUpdate(0.1)
        }

        assertTrue(anim.isAnimating)
    }

    @Test
    fun `Simple Reverse Animation`() {
        var count = 0.0

        val anim = AnimationBuilder()
                .interpolator(Interpolator.LINEAR)
                .duration(Duration.seconds(4.0))
                .autoReverse(true)
                .repeat(3)
                .build(AnimatedValue(1.0, 5.0), Consumer { count = it })

        assertFalse(anim.isAnimating)
        assertTrue(anim.isAutoReverse)
        assertFalse(anim.isReverse)
        assertFalse(anim.isPaused)
        assertThat(count, `is`(0.0))

        anim.start()

        assertTrue(anim.isAnimating)
        assertFalse(anim.isPaused)
        assertThat(count, `is`(1.0))

        for (i in 2..5) {
            anim.onUpdate(1.0)

            assertThat(count, `is`(i.toDouble()))
        }

        assertThat(count, `is`(5.0))
        assertTrue(anim.isAnimating)
        assertTrue(anim.isReverse)

        for (i in 4 downTo 1) {
            anim.onUpdate(1.0)

            assertThat(count, `is`(i.toDouble()))
        }

        assertThat(count, `is`(1.0))
        assertTrue(anim.isAnimating)
        assertFalse(anim.isReverse)

        for (i in 2..5) {
            anim.onUpdate(1.0)

            assertThat(count, `is`(i.toDouble()))
        }

        assertThat(count, `is`(5.0))
        assertFalse(anim.isAnimating)
        assertFalse(anim.isReverse)
    }

    @Test
    fun `On Animation Finished`() {
        var count = 0

        val anim = AnimationBuilder()
                .interpolator(Interpolator.LINEAR)
                .duration(Duration.seconds(2.0))
                .onFinished(Runnable { count = 15 })
                .build(AnimatedValue(1, 3), Consumer { })

        assertThat(count, `is`(0))

        anim.start()

        anim.onUpdate(2.0)

        assertThat(count, `is`(15))
    }

    @Test
    fun `Animation with a delay`() {
        var count = 0

        val anim = AnimationBuilder()
                .delay(Duration.seconds(1.5))
                .duration(Duration.seconds(2.0))
                .build(AnimatedValue(1, 3), Consumer { count = it })

        assertThat(count, `is`(0))

        anim.start()
        assertThat(count, `is`(1))

        anim.onUpdate(1.0)
        assertThat(count, `is`(1))

        anim.onUpdate(0.5)
        assertThat(count, `is`(1))

        anim.onUpdate(1.0)
        assertThat(count, `is`(2))

        anim.onUpdate(1.0)
        assertThat(count, `is`(3))
    }

    @Test
    fun `Stop animation`() {
        var count = 0

        val anim = AnimationBuilder()
                .duration(Duration.seconds(3.0))
                .build(AnimatedValue(1, 4), Consumer { count = it })

        assertThat(count, `is`(0))

        anim.start()

        anim.onUpdate(2.0)
        assertThat(count, `is`(3))

        anim.stop()
        assertThat(count, `is`(3))

        anim.onUpdate(1.0)
        assertThat(count, `is`(3))
    }

    @Test
    fun `Pause and resume animation`() {
        var count = 0

        val anim = AnimationBuilder()
                .duration(Duration.seconds(3.0))
                .build(AnimatedValue(1, 4), Consumer { count = it })

        assertThat(count, `is`(0))

        anim.start()

        anim.onUpdate(2.0)
        assertThat(count, `is`(3))

        anim.pause()
        assertThat(count, `is`(3))

        anim.onUpdate(1.0)
        assertThat(count, `is`(3))

        anim.resume()

        anim.onUpdate(1.0)
        assertThat(count, `is`(4))
    }

    @Test
    fun `Start and stop noop if animation has started stopped`() {
        val anim = AnimationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .duration(Duration.seconds(3.0))
                .build(AnimatedValue(1, 4), Consumer { })

        anim.stop()

        anim.start()

        anim.start()

        anim.onUpdate(3.0)

        assertFalse(anim.isAnimating)
    }

    @Test
    fun `Start reverse`() {
        var count = 0

        val anim = AnimationBuilder()
                .duration(Duration.seconds(3.0))
                .build(AnimatedValue(1, 4), Consumer { count = it })

        anim.startReverse()

        assertTrue(anim.isAnimating)
        assertTrue(anim.isReverse)

        assertThat(count, `is`(4))

        // does nothing since we are animating already
        anim.startReverse()

        for (i in 3 downTo 1) {
            anim.onUpdate(1.0)

            assertThat(count, `is`(i))
        }
    }
}