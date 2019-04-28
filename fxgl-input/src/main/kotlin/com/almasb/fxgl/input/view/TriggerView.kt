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
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 * Trigger view is bound to set trigger.
 * If trigger changes, the view is automatically updated.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TriggerView
@JvmOverloads constructor(trigger: Trigger,
                          var color: Color = Color.ORANGE,
                          var size: Double = 24.0) : Pane() {

    private val triggerProperty = SimpleObjectProperty<Trigger>(trigger)

    var trigger: Trigger
        get() = triggerProperty.value
        set(value) {
            triggerProperty.value = value
        }

    init {
        triggerProperty.addListener { _, _, _ ->
            updateView()
        }

        updateView()
    }

    fun triggerProperty(): ObjectProperty<Trigger> = triggerProperty

    private fun updateView() {
        val view = createView()

        if (trigger.modifier == InputModifier.NONE) {
            children.setAll(view)
            return
        }

        val modifierKey = trigger.modifier.toKeyCode()
        val modifierView = KeyView(modifierKey, color, size)

        val text = Text("+").also {
            it.fill = color
            it.font = Font.font(size)
        }

        val hbox = HBox(modifierView, text, view)

        children.setAll(hbox)
    }

    private fun createView(): Node {
        if (trigger.isKey) {
            return KeyView((trigger as KeyTrigger).key, color, size)
        } else {
            return MouseButtonView((trigger as MouseTrigger).button, color, size)
        }
    }
}