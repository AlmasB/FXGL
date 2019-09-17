/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import com.almasb.fxgl.input.InputModifier.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

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
        assertThat(key.modifier, `is`(NONE))
        assertThat(key.key, `is`(KeyCode.A))
        assertThat(key.name, `is`("A"))
    }

    @Test
    fun `Mouse trigger`() {
        val btn = MouseTrigger(MouseButton.PRIMARY)

        assertFalse(btn.isKey)
        assertTrue(btn.isButton)
        assertThat(btn.modifier, `is`(NONE))
        assertThat(btn.button, `is`(MouseButton.PRIMARY))
        assertThat(btn.name, `is`("LMB"))
    }

    @Test
    fun `When trigger key is pressed and modifier is pressed then trigger fires`() {
        val key = KeyTrigger(KeyCode.A)

        val keyevent = keyEvent(KeyCode.A, false, false, false)

        assertTrue(key.isTriggered(keyevent))
        assertTrue(key.isReleased(keyevent))
    }

    @Test
    fun `When trigger button is pressed and modifier is pressed then trigger fires`() {
        val btn = MouseTrigger(MouseButton.PRIMARY)

        val mouseEvent = mouseEvent(MouseButton.PRIMARY, false, false, false)

        assertTrue(btn.isTriggered(mouseEvent))
        assertTrue(btn.isReleased(mouseEvent))
    }

    @Test
    fun `Key trigger does not fire when a different key is pressed`() {
        val key = KeyTrigger(KeyCode.A)

        val keyevent = keyEvent(KeyCode.B, false, false, false)

        assertFalse(key.isTriggered(keyevent))
        assertFalse(key.isReleased(keyevent))
    }

    @Test
    fun `Mouse trigger does not fire when a different button is pressed`() {
        val btn = MouseTrigger(MouseButton.PRIMARY)

        val mouseEvent = mouseEvent(MouseButton.SECONDARY, false, false, false)

        assertFalse(btn.isTriggered(mouseEvent))
        assertFalse(btn.isReleased(mouseEvent))
    }

    @Test
    fun `Trigger does not fire when a different event is fired`() {
        val key = KeyTrigger(KeyCode.A)
        val btn = MouseTrigger(MouseButton.PRIMARY)

        val keyevent = keyEvent(KeyCode.A, false, false, false)
        val mouseEvent = mouseEvent(MouseButton.PRIMARY, false, false, false)

        assertFalse(key.isTriggered(mouseEvent))
        assertFalse(btn.isTriggered(keyevent))
        assertFalse(key.isReleased(mouseEvent))
        assertFalse(btn.isReleased(keyevent))
    }

    @ParameterizedTest
    @EnumSource(value = InputModifier::class, names = ["SHIFT", "CTRL", "ALT"])
    fun `Trigger with modifier is released if a modifier is released`(modifier: InputModifier) {
        val key = KeyTrigger(KeyCode.A, modifier)
        val btn = MouseTrigger(MouseButton.PRIMARY, modifier)

        val key2 = KeyTrigger(KeyCode.A)
        val btn2 = MouseTrigger(MouseButton.PRIMARY)

        assertTrue(key.isReleased(keyEvent(modifier.toKeyCode(), modifier == SHIFT, modifier == CTRL, modifier == ALT)))
        assertTrue(btn.isReleased(keyEvent(modifier.toKeyCode(), modifier == SHIFT, modifier == CTRL, modifier == ALT)))

        assertFalse(key2.isReleased(keyEvent(modifier.toKeyCode(), modifier == SHIFT, modifier == CTRL, modifier == ALT)))
        assertFalse(btn2.isReleased(keyEvent(modifier.toKeyCode(), modifier == SHIFT, modifier == CTRL, modifier == ALT)))
    }

    @Test
    fun `When trigger key is pressed and modifier is not pressed then trigger does not fire`() {
        val key = KeyTrigger(KeyCode.A, SHIFT)

        val keyevent = keyEvent(KeyCode.A, false, false, false)

        assertFalse(key.isTriggered(keyevent))
    }

    @Test
    fun `When trigger button is pressed and modifier is not pressed then trigger does not fire`() {
        val btn = MouseTrigger(MouseButton.PRIMARY, SHIFT)

        val mouseEvent = mouseEvent(MouseButton.PRIMARY, false, false, false)

        assertFalse(btn.isTriggered(mouseEvent))
    }

    @Test
    fun `Test toString`() {
        val key = KeyTrigger(KeyCode.A, CTRL)

        assertThat(key.toString(), `is`("CTRL+A"))

        val btn = MouseTrigger(MouseButton.PRIMARY, ALT)

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
}

internal fun mouseEvent(shift: Boolean, ctrl: Boolean, alt: Boolean) =
        mouseEvent(MouseButton.PRIMARY, shift, ctrl, alt)

internal fun keyEvent(shift: Boolean, ctrl: Boolean, alt: Boolean) =
        keyEvent(KeyCode.A, shift, ctrl, alt)

internal fun mouseEvent(button: MouseButton, shift: Boolean, ctrl: Boolean, alt: Boolean): MouseEvent {
    return MouseEvent(MouseEvent.ANY, 0.0, 0.0, 0.0, 0.0, button, 1,
            shift, ctrl, alt,
            false, false, false, false, false, false, false, null)
}

internal fun keyEvent(key: KeyCode, shift: Boolean, ctrl: Boolean, alt: Boolean): KeyEvent {
    return KeyEvent(KeyEvent.ANY, "", "", key, shift, ctrl, alt, false)
}

internal fun mousePressedEvent(button: MouseButton, shift: Boolean, ctrl: Boolean, alt: Boolean): MouseEvent {
    return MouseEvent(MouseEvent.MOUSE_PRESSED, 0.0, 0.0, 0.0, 0.0, button, 1,
            shift, ctrl, alt,
            false, false, false, false, false, false, false, null)
}