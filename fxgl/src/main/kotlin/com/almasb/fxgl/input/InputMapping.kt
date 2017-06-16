/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton

/**
 * Basic mapping of action name to its trigger.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class InputMapping {

    val actionName: String
    private val trigger: Any
    val modifier: InputModifier

    constructor(actionName: String, key: KeyCode) : this(actionName, key, InputModifier.NONE)
    constructor(actionName: String, key: KeyCode, modifier: InputModifier) {
        this.actionName = actionName
        this.trigger = key
        this.modifier = modifier
    }

    constructor(actionName: String, button: MouseButton) : this(actionName, button, InputModifier.NONE)
    constructor(actionName: String, button: MouseButton, modifier: InputModifier) {
        this.actionName = actionName
        this.trigger = button
        this.modifier = modifier
    }

    fun isKeyTrigger() = trigger is KeyCode
    fun isButtonTrigger() = trigger is MouseButton

    fun getKeyTrigger() = trigger as KeyCode
    fun getButtonTrigger() = trigger as MouseButton
}
