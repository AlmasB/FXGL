/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.components.TimeComponent
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EffectTest {

    @Test
    fun `Effect start and end`() {
        val e = Entity()
        val control = EffectComponent()
        e.addComponent(control)

        control.startEffect(TestEffect(Duration.seconds(1.0)))
        assertThat(e.getInt("key"), `is`(10))

        control.onUpdate(1.0)
        assertThat(e.getInt("key"), `is`(-10))

        val effect = TestEffect(Duration.seconds(1.0))

        control.startEffect(effect)
        assertThat(e.getInt("key"), `is`(10))

        control.endEffect(effect)
        assertThat(e.getInt("key"), `is`(-10))

        control.startEffect(effect)
        assertThat(e.getInt("key"), `is`(10))

        control.endEffect(TestEffect::class.java)
        assertThat(e.getInt("key"), `is`(-10))
    }

    @Test
    fun `Effect length is not affected by TimeComponent`() {
        val e = Entity()
        e.addComponent(TimeComponent(0.25))
        val control = EffectComponent()
        e.addComponent(control)

        control.startEffect(TestEffect(Duration.seconds(1.0)))
        assertThat(e.getInt("key"), `is`(10))

        control.onUpdate(1.0)
        assertThat(e.getInt("key"), `is`(-10))
    }

    @Test
    fun `New effect replaces old effect with new duration`() {
        val e = Entity()
        val control = EffectComponent()
        e.addComponent(control)

        control.startEffect(TestEffect(Duration.seconds(3.0)))
        assertThat(e.getInt("key"), `is`(10))

        control.onUpdate(1.0)
        assertThat(e.getInt("key"), `is`(10))

        control.startEffect(TestEffect(Duration.seconds(1.0)))

        control.onUpdate(1.0)
        assertThat(e.getInt("key"), `is`(-10))
    }

    @Test
    fun `End all effects`() {
        val e = Entity()
        val control = EffectComponent()
        e.addComponent(control)

        control.startEffect(TestEffect(Duration.seconds(3.0)))
        control.startEffect(TestEffect2())

        control.endAllEffects()

        assertThat(e.getInt("key"), `is`(-10))
        assertThat(e.getInt("key2"), `is`(-20))
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