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
abstract class MiniGame<T : MiniGameResult> {

    lateinit var result: T
        protected set

    var isDone: Boolean = false
        protected set

    open fun onUpdate(tpf: Double) {}
}