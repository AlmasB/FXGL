/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.MouseButton

data class MouseTrigger(val button: MouseButton, private val modifier: InputModifier) : Trigger {

    constructor(button: MouseButton) : this(button, InputModifier.NONE) {

    }

    override fun getModifier() = modifier

    override fun getName() = button.toString()

    override fun isKey() = false

    override fun isButton() = true

    override fun toString() = (if (modifier == InputModifier.NONE) "" else "$modifier+") + button.toString()
}