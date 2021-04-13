/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.virtual.VirtualButton
import javafx.scene.input.KeyCode

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class KeyInputBuilder {

    companion object {
        private var counter = 0
    }

    private var onActionBegin: Runnable = EmptyRunnable
    private var onAction: Runnable = EmptyRunnable
    private var onActionEnd: Runnable = EmptyRunnable

    constructor(input: Input, key: KeyCode, modifier: InputModifier = InputModifier.NONE, virtualBtn: VirtualButton, name: String = "AUTO-${counter++}") {
        input.addAction(object : UserAction(name) {
            override fun onActionBegin() {
                onActionBegin.run()
            }

            override fun onAction() {
                onAction.run()
            }

            override fun onActionEnd() {
                onActionEnd.run()
            }
        }, key, modifier, virtualBtn)
    }

    constructor(input: Input, key: KeyCode, modifier: InputModifier = InputModifier.NONE, name: String = "AUTO-${counter++}") {
        input.addAction(object : UserAction(name) {
            override fun onActionBegin() {
                onActionBegin.run()
            }

            override fun onAction() {
                onAction.run()
            }

            override fun onActionEnd() {
                onActionEnd.run()
            }
        }, key, modifier)
    }

    fun onActionBegin(onActionBegin: Runnable) = this.also { it.onActionBegin = onActionBegin }
    fun onAction(onAction: Runnable) = this.also { it.onAction = onAction }
    fun onActionEnd(onActionEnd: Runnable) = this.also { it.onActionEnd = onActionEnd }
}