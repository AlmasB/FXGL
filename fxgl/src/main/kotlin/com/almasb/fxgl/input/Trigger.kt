/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input

interface Trigger {

    fun getModifier(): InputModifier
    fun getName(): String
    fun isKey(): Boolean
    fun isButton(): Boolean
}