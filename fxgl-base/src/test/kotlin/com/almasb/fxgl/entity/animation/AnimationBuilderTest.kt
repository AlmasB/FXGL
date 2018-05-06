/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.animation

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockState
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.ColorComponent
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.CubicCurve
import javafx.scene.shape.QuadCurve
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
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
        FXGL.getProperties().setValue("dev.showbbox", false)
        state = MockState()
    }

    @Test
    fun `Translate`() {
        val e = Entity()

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
    fun `Translate along quad curve`() {
        val e = Entity()

        val curve = QuadCurve(0.0, 0.0, 100.0, 200.0, 300.0, 300.0)

        val anim = AnimationBuilder()
                .duration(Duration.millis(150.0))
                .translate(e)
                .alongPath(curve)
                .build()

        anim.start(state)

        for (i in 0..9) {
            state.mockUpdate(0.015)
        }

        assertThat(e.position, `is`(Point2D(300.0, 300.0)))
    }

    @Test
    fun `Translate along cubic curve`() {
        val e = Entity()

        val curve = CubicCurve(0.0, 0.0, 20.0, 30.0, 100.0, 200.0, 300.0, 300.0)

        val anim = AnimationBuilder()
                .duration(Duration.millis(150.0))
                .translate(e)
                .alongPath(curve)
                .build()

        anim.start(state)

        for (i in 0..9) {
            state.mockUpdate(0.015)
        }

        assertThat(e.position, `is`(Point2D(300.0, 300.0)))
    }

    @Test
    fun `Throw if translate along unknown shape`() {
        assertThrows(IllegalArgumentException::class.java, {
            val e = Entity()

            val curve = Circle()

            AnimationBuilder()
                    .duration(Duration.millis(150.0))
                    .translate(e)
                    .alongPath(curve)
                    .build()
        })
    }

    @Test
    fun `Rotate`() {
        val e = Entity()

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
        val e = Entity()

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

    @Test
    fun `Color animation`() {
        val e = Entity()
        e.addComponent(ColorComponent())

        val endColor = Color.color(0.5, 0.2, 0.33, 0.5)

        val anim = AnimationBuilder()
                .duration(Duration.millis(100.0))
                .color(e)
                .fromColor(Color.AQUA)
                .toColor(endColor)
                .build()

        anim.start(state)

        for (i in 0..7) {
            state.mockUpdate(0.015)
        }

        val color = e.getComponent(ColorComponent::class.java).value

        assertThat(color, `is`(endColor))
    }
}