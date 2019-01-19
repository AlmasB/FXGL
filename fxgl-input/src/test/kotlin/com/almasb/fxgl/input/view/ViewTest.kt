/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input.view

import com.almasb.fxgl.input.KeyTrigger
import com.almasb.fxgl.input.MouseTrigger
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ViewTest {

    @Test
    fun `Key view`() {
        val view = KeyView(KeyCode.A, Color.WHITE, 15.0)
    }

    @Test
    fun `MouseButton view`() {
        val view = MouseButtonView(MouseButton.PRIMARY, Color.BLUE, 20.0)
    }

    @Test
    fun `Trigger view`() {
        val view = TriggerView(KeyTrigger(KeyCode.C))
        val nodes = arrayListOf(view.children)

        assertTrue(nodes.isNotEmpty())

        view.trigger = MouseTrigger(MouseButton.SECONDARY)

        assertTrue(view.children.isNotEmpty())

        nodes.forEach {
            assertThat(view.children, not(hasItem(it)))
        }
    }
}