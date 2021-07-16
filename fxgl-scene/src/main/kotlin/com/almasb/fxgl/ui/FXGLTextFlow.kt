/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import com.almasb.fxgl.input.Trigger
import com.almasb.fxgl.input.view.KeyView
import com.almasb.fxgl.input.view.MouseButtonView
import com.almasb.fxgl.input.view.TriggerView
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.text.TextFlow

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class FXGLTextFlow(private val uiFactoryService: UIFactoryService) : TextFlow() {

    fun append(message: String): FXGLTextFlow {
        return append(message, Color.BLACK, 22.0)
    }

    fun append(message: String, fontSize: Double): FXGLTextFlow {
        return append(message, Color.BLACK, fontSize)
    }

    fun append(message: String, color: Color): FXGLTextFlow {
        return append(message, color, 22.0)
    }

    fun append(message: String, color: Color, fontSize: Double): FXGLTextFlow {
        return append(uiFactoryService.newText(message, color, fontSize))
    }

    /**
     * Fixed key.
     */
    fun append(key: KeyCode, color: Color): FXGLTextFlow {
        val keyView = KeyView(key, color)
        return append(keyView)
    }

    fun append(key: KeyCode, color: Color, size: Double): FXGLTextFlow {
        val keyView = KeyView(key, color, size)
        return append(keyView)
    }

    /**
     * Fixed button.
     */
    fun append(btn: MouseButton, color: Color): FXGLTextFlow {
        val view = MouseButtonView(btn, color)
        view.translateY = 25.0 / 2
        return append(view)
    }

    fun append(btn: MouseButton, color: Color, size: Double): FXGLTextFlow {
        val view = MouseButtonView(btn, color, size)
        view.translateY = 25.0 / 2
        return append(view)
    }

    /**
     * Fixed trigger.
     */
    fun append(trigger: Trigger, color: Color): FXGLTextFlow {
        val view = TriggerView(trigger, color)
        return append(view)
    }

    fun append(trigger: Trigger, color: Color, size: Double): FXGLTextFlow {
        val view = TriggerView(trigger, color, size)
        return append(view)
    }

    /**
     * Bound trigger, auto updates.
     */
    fun append(triggerProperty: ReadOnlyObjectProperty<Trigger>, color: Color): FXGLTextFlow {
        val view = TriggerView(triggerProperty.value, color)
        view.triggerProperty().bind(triggerProperty)
        return append(view)
    }

    fun append(triggerProperty: ReadOnlyObjectProperty<Trigger>, color: Color, size: Double): FXGLTextFlow {
        val view = TriggerView(triggerProperty.value, color, size)
        view.triggerProperty().bind(triggerProperty)
        return append(view)
    }

    fun append(node: Node): FXGLTextFlow {
        children.add(node)
        return this
    }
}