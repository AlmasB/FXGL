/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.entity.component.TimeComponent
import com.almasb.fxgl.entity.control.EffectControl
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.geometry.Point2D
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EffectTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @Test
    fun `Effect start and end`() {
        val e = Entity()
        val control = EffectControl()
        e.addControl(control)

        control.startEffect(TestEffect(Duration.seconds(1.0)))
        assertThat(e.getProperty("key"), `is`(10))

        control.onUpdate(e, 1.0)
        assertThat(e.getProperty("key"), `is`(-10))

        val effect = TestEffect(Duration.seconds(1.0))

        control.startEffect(effect)
        assertThat(e.getProperty("key"), `is`(10))

        control.endEffect(effect)
        assertThat(e.getProperty("key"), `is`(-10))

        control.startEffect(effect)
        assertThat(e.getProperty("key"), `is`(10))

        control.endEffect(TestEffect::class.java)
        assertThat(e.getProperty("key"), `is`(-10))
    }

    @Test
    fun `Effect length is not affected by TimeComponent`() {
        val e = Entity()
        e.addComponent(TimeComponent(0.25))
        val control = EffectControl()
        e.addControl(control)

        control.startEffect(TestEffect(Duration.seconds(1.0)))
        assertThat(e.getProperty("key"), `is`(10))

        control.onUpdate(e, 1.0)
        assertThat(e.getProperty("key"), `is`(-10))
    }

    @Test
    fun `New effect replaces old effect with new duration`() {
        val e = Entity()
        val control = EffectControl()
        e.addControl(control)

        control.startEffect(TestEffect(Duration.seconds(3.0)))
        assertThat(e.getProperty("key"), `is`(10))

        control.onUpdate(e, 1.0)
        assertThat(e.getProperty("key"), `is`(10))

        control.startEffect(TestEffect(Duration.seconds(1.0)))

        control.onUpdate(e, 1.0)
        assertThat(e.getProperty("key"), `is`(-10))
    }

    @Test
    fun `End all effects`() {
        val e = Entity()
        val control = EffectControl()
        e.addControl(control)

        control.startEffect(TestEffect(Duration.seconds(3.0)))
        control.startEffect(TestEffect2())

        control.endAllEffects()

        assertThat(e.getProperty("key"), `is`(-10))
        assertThat(e.getProperty("key2"), `is`(-20))
    }

    private class TestEffect(duration: Duration) : Effect(duration) {
        override fun onStart(entity: Entity) {
            entity.setProperty("key", 10)
        }

        override fun onEnd(entity: Entity) {
            entity.setProperty("key", -10)
        }
    }

    private class TestEffect2() : Effect(Duration.seconds(1.0)) {
        override fun onStart(entity: Entity) {
            entity.setProperty("key2", 20)
        }

        override fun onEnd(entity: Entity) {
            entity.setProperty("key2", -20)
        }
    }
}