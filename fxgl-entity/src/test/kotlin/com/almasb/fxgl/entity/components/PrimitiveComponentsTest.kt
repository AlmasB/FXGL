/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.core.serialization.Bundle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PrimitiveComponentsTest {

    @Test
    fun `Boolean`() {
        val c = SimpleBooleanComponent()

        assertFalse(c.value)
        assertFalse(c.valueProperty().value)

        c.value = true
        assertTrue(c.value)

        val bundle = Bundle("")
        c.write(bundle)

        c.value = false
        c.read(bundle)

        assertTrue(c.value)

        assertThat(c.toString(), `is`("SimpleBoolean(true)"))
    }

    @Test
    fun `Integer`() {
        val c = SimpleIntegerComponent()

        assertThat(c.value, `is`(0))
        assertThat(c.valueProperty().value, `is`(0))

        c.value = 22
        assertThat(c.value, `is`(22))

        val bundle = Bundle("")
        c.write(bundle)

        c.value = 33
        c.read(bundle)

        assertThat(c.value, `is`(22))

        assertThat(c.toString(), `is`("SimpleInteger(22)"))
    }

    @Test
    fun `Double`() {
        val c = SimpleDoubleComponent()

        assertThat(c.value, `is`(0.0))
        assertThat(c.valueProperty().value, `is`(0.0))

        c.value = 22.0
        assertThat(c.value, `is`(22.0))

        val bundle = Bundle("")
        c.write(bundle)

        c.value = 33.0
        c.read(bundle)

        assertThat(c.value, `is`(22.0))

        assertThat(c.toString(), `is`("SimpleDouble(22.0)"))
    }

    @Test
    fun `String`() {
        val c = SimpleStringComponent()

        assertThat(c.value, `is`(""))
        assertThat(c.valueProperty().value, `is`(""))

        c.value = "hi"
        assertThat(c.value, `is`("hi"))

        val bundle = Bundle("")
        c.write(bundle)

        c.value = "hey"
        c.read(bundle)

        assertThat(c.value, `is`("hi"))

        assertThat(c.toString(), `is`("SimpleString(hi)"))
    }

    @Test
    fun `Object`() {
        val c = SimpleObjectComponent()

        assertThat(c.value, `is`(Vec2()))
        assertThat(c.valueProperty().value, `is`(Vec2()))

        c.value = Vec2(1.5, 33.0)
        assertThat(c.value, `is`(Vec2(1.5, 33.0)))

        assertThat(c.toString(), `is`("SimpleObject((1.5,33.0))"))
    }

    @Test
    fun `Collidable`() {
        val c = CollidableComponent(false)

        assertFalse(c.value)

        c.value = true

        assertTrue(c.value)
    }

    class SimpleBooleanComponent : BooleanComponent()
    class SimpleIntegerComponent : IntegerComponent()
    class SimpleDoubleComponent : DoubleComponent()
    class SimpleStringComponent : StringComponent()
    class SimpleObjectComponent : ObjectComponent<Vec2>(Vec2())
}