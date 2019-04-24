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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TriggerTest {

    @Test
    fun `Key trigger`() {
        val key = KeyTrigger(KeyCode.A)

        assertTrue(key.isKey)
        assertFalse(key.isButton)
        assertThat(key.modifier, `is`(InputModifier.NONE))
        assertThat(key.key, `is`(KeyCode.A))
        assertThat(key.name, `is`("A"))
    }

    @Test
    fun `Mouse trigger`() {
        val btn = MouseTrigger(MouseButton.PRIMARY)

        assertFalse(btn.isKey)
        assertTrue(btn.isButton)
        assertThat(btn.modifier, `is`(InputModifier.NONE))
        assertThat(btn.button, `is`(MouseButton.PRIMARY))
        assertThat(btn.name, `is`("LMB"))
    }

    @Test
    fun `Is triggered`() {
        val key = KeyTrigger(KeyCode.A)
        val btn = MouseTrigger(MouseButton.PRIMARY)

        val keyevent = keyEvent(KeyCode.A, false, false, false)
        val mouseEvent = mouseEvent(MouseButton.PRIMARY, false, false, false)

        assertTrue(key.isTriggered(keyevent))
        assertFalse(key.isTriggered(mouseEvent))

        assertTrue(btn.isTriggered(mouseEvent))
        assertFalse(btn.isTriggered(keyevent))
    }

    @Test
    fun `Test toString`() {
        val key = KeyTrigger(KeyCode.A, InputModifier.CTRL)

        assertThat(key.toString(), `is`("CTRL+A"))

        val btn = MouseTrigger(MouseButton.PRIMARY, InputModifier.ALT)

        assertThat(btn.toString(), `is`("ALT+LMB"))
    }

    @Test
    fun `toString throws if not a button`() {
        assertThrows(RuntimeException::class.java) {
            MouseTrigger(MouseButton.NONE).toString()
        }
    }

    @Test
    fun `Mouse button from string`() {
        assertThat(MouseTrigger.buttonFromString("LMB"), `is`(MouseButton.PRIMARY))
        assertThat(MouseTrigger.buttonFromString("MMB"), `is`(MouseButton.MIDDLE))
        assertThat(MouseTrigger.buttonFromString("RMB"), `is`(MouseButton.SECONDARY))
    }

    @Test
    fun `Mouse button from string throws if not LMB MMB or RMB`() {
        assertThrows(RuntimeException::class.java) {
            MouseTrigger.buttonFromString("")
        }
    }

    // TODO: make it common to input tests?
    private fun mouseEvent(button: MouseButton, shift: Boolean, ctrl: Boolean, alt: Boolean): MouseEvent {
        return MouseEvent(MouseEvent.ANY, 0.0, 0.0, 0.0, 0.0, button, 1,
                shift, ctrl, alt,
                false, false, false, false, false, false, false, null)
    }

    private fun keyEvent(key: KeyCode, shift: Boolean, ctrl: Boolean, alt: Boolean): KeyEvent {
        return KeyEvent(KeyEvent.ANY, "", "", key, shift, ctrl, alt, false)
    }
}