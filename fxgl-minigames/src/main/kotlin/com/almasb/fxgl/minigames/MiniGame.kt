/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface MiniGame<T : MiniGameResult> {

    val result: T

    val isDone: Boolean

    fun onUpdate(tpf: Double)
}