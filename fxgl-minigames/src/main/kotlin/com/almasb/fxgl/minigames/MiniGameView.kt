/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.minigames

import com.almasb.fxgl.input.Input
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class MiniGameView<T : MiniGame<*>>(val miniGame: T) : Parent() {

    open fun onInitInput(input: Input) {}

    open fun onUpdate(tpf: Double) {}
}