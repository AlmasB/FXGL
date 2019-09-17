/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.view

import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.input.MouseTrigger
import com.almasb.fxgl.input.Trigger
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ViewTest {

    @Test
    fun `Key view`() {
        KeyCode.values().forEach {
            val view = KeyView(it, Color.WHITE, 15.0)
            view.backgroundColor = Color.AQUAMARINE
            view.keyColor = Color.RED

            assertThat(view.backgroundColor, `is`<Paint>(Color.AQUAMARINE))
            assertThat(view.keyColor, `is`<Paint>(Color.RED))
        }
    }

    @Test
    fun `MouseButton view`() {
        val view = MouseButtonView(MouseButton.PRIMARY, Color.BLUE, 20.0)
    }

    @Test
    fun `MouseButton view throws if not supported`() {
        assertThrows<IllegalArgumentException> {
            MouseButtonView(MouseButton.MIDDLE)
        }
    }

    @ParameterizedTest
    @EnumSource(InputModifier::class)
    fun `Trigger view`(modifier: InputModifier) {
        val view = TriggerView(KeyTrigger(KeyCode.C, modifier), Color.BLUE, 18.0)
        val nodes = arrayListOf(view.children)

        assertTrue(nodes.isNotEmpty())

        val trigger = MouseTrigger(MouseButton.SECONDARY)
        view.trigger = trigger

        assertThat(view.triggerProperty().value, `is`<Trigger>(trigger))
        assertTrue(view.children.isNotEmpty())

        nodes.forEach {
            assertThat(view.children, not(hasItem(it)))
        }

        TriggerView(MouseTrigger(MouseButton.PRIMARY))
        TriggerView(MouseTrigger(MouseButton.PRIMARY), Color.GOLD)

        view.color = Color.RED
        view.size = 24.0
    }
}