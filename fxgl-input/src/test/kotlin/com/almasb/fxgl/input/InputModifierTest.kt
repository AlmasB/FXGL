/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InputModifierTest {

    @Test
    fun `Convert Shift from MouseEvent`() {
        val e = mouseEvent(true, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.SHIFT))
    }

    @Test
    fun `Convert Ctrl from MouseEvent`() {
        val e = mouseEvent(false, true, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.CTRL))
    }

    @Test
    fun `Convert Alt from MouseEvent`() {
        val e = mouseEvent(false, false, true)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.ALT))
    }

    @Test
    fun `Convert from MouseEvent`() {
        val e = mouseEvent(false, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.NONE))
    }

    @Test
    fun `Convert Shift from KeyEvent`() {
        val e = keyEvent(true, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.SHIFT))
    }

    @Test
    fun `Convert Ctrl from KeyEvent`() {
        val e = keyEvent(false, true, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.CTRL))
    }

    @Test
    fun `Convert Alt from KeyEvent`() {
        val e = keyEvent(false, false, true)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.ALT))
    }

    @Test
    fun `Convert from KeyEvent`() {
        val e = keyEvent(false, false, false)

        val modifier = InputModifier.from(e)
        assertThat(modifier, `is`(InputModifier.NONE))
    }

    @Test
    fun `Is triggered mouse`() {
        assertTrue(InputModifier.SHIFT.isTriggered(mouseEvent(true, false, false)))
        assertTrue(InputModifier.CTRL.isTriggered(mouseEvent(false, true, false)))
        assertTrue(InputModifier.ALT.isTriggered(mouseEvent(false, false, true)))
        assertTrue(InputModifier.NONE.isTriggered(mouseEvent(false, false, false)))
    }

    @Test
    fun `Is triggered key`() {
        assertTrue(InputModifier.SHIFT.isTriggered(keyEvent(true, false, false)))
        assertTrue(InputModifier.CTRL.isTriggered(keyEvent(false, true, false)))
        assertTrue(InputModifier.ALT.isTriggered(keyEvent(false, false, true)))
        assertTrue(InputModifier.NONE.isTriggered(keyEvent(false, false, false)))
    }

    private fun mouseEvent(shift: Boolean, ctrl: Boolean, alt: Boolean): MouseEvent {
        return MouseEvent(MouseEvent.ANY, 0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                shift, ctrl, alt,
                false, false, false, false, false, false, false, null)
    }

    private fun keyEvent(shift: Boolean, ctrl: Boolean, alt: Boolean): KeyEvent {
        return KeyEvent(KeyEvent.ANY, "", "", KeyCode.A, shift, ctrl, alt, false)
    }
}