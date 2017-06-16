/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

import javafx.scene.input.KeyCode

data class KeyTrigger(val key: KeyCode, private val modifier: InputModifier) : Trigger {

    constructor(key: KeyCode) : this(key, InputModifier.NONE) {

    }

    override fun getModifier() = modifier

    override fun getName() = key.getName()

    override fun isKey() = true

    override fun isButton() = false

    override fun toString() = (if (modifier == InputModifier.NONE) "" else "$modifier+") + key.getName()
}