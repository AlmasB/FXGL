/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockState
import com.almasb.fxgl.app.State
import com.almasb.fxgl.app.SubState
import com.almasb.fxgl.entity.GameEntity
import javafx.geometry.Point2D
import javafx.util.Duration
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AnimationBuilderTest {

    private lateinit var state: MockState

    @BeforeEach
    fun `init`() {
        FXGL.setProperty("dev.showbbox", false)
        state = MockState()
    }

    @Test
    fun `Translate`() {
        val e = GameEntity()

        val anim = AnimationBuilder()
                .duration(Duration.millis(150.0))
                .translate(e)
                .from(Point2D(0.0, 0.0))
                .to(Point2D(100.0, 50.0))
                .build()

        anim.start(state)

        for (i in 0..9) {
            state.mockUpdate(0.015)
        }

        assertThat(e.position, `is`(Point2D(100.0, 50.0)))
    }

    @Test
    fun `Rotate`() {
        val e = GameEntity()

        val anim = AnimationBuilder()
                .duration(Duration.millis(150.0))
                .rotate(e)
                .rotateFrom(0.0)
                .rotateTo(35.0)
                .build()

        anim.start(state)

        for (i in 0..9) {
            state.mockUpdate(0.015)
        }

        assertThat(e.rotation, `is`(35.0))
    }

    @Test
    fun `Scale`() {
        val e = GameEntity()

        val anim = AnimationBuilder()
                .duration(Duration.millis(150.0))
                .scale(e)
                .from(Point2D(1.0, 1.0))
                .to(Point2D(1.5, 1.5))
                .build()

        anim.start(state)

        for (i in 0..9) {
            state.mockUpdate(0.015)
        }

        assertThat(e.view.scaleX, `is`(1.5))
        assertThat(e.view.scaleY, `is`(1.5))
    }
}