/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

/**
 * This API is experimental.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

fun set(varName: String, value: Any) {
    FXGL.getApp().gameState.setValue(varName, value)
}

fun geti(varName: String): Int = FXGL.getApp().gameState.getInt(varName)

fun getd(varName: String): Double = FXGL.getApp().gameState.getDouble(varName)

fun getb(varName: String): Boolean = FXGL.getApp().gameState.getBoolean(varName)

fun gets(varName: String): String = FXGL.getApp().gameState.getString(varName)

fun <T> geto(varName: String): T = FXGL.getApp().gameState.getObject(varName)