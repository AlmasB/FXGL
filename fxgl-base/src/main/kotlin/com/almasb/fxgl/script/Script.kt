/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.script

/**
 * A single instance of a script with its local context.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface Script {

    fun <T> call(functionName: String, vararg args: Any): T

    fun <T> eval(script: String): T

    fun hasFunction(functionName: String): Boolean
}