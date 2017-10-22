/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui

import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.text.TextFlow

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TextFlowBuilder
private constructor() {

    companion object {
        @JvmStatic fun start(): TextFlowBuilder {
            return TextFlowBuilder()
        }
    }

    private val flow = FXGLTextFlow()

    fun append(node: Node): TextFlowBuilder {
        flow.append(node)
        return this
    }

    fun append(message: String): TextFlowBuilder {
        flow.append(message)
        return this
    }

    fun append(message: String, fontSize: Double): TextFlowBuilder {
        flow.append(message, Color.BLACK, fontSize)
        return this
    }

    fun append(key: KeyCode, color: Color): TextFlowBuilder {
        flow.append(key, color)
        return this
    }

    fun append(btn: MouseButton, color: Color): TextFlowBuilder {
        flow.append(btn, color)
        return this
    }

    fun append(message: String, color: Color): TextFlowBuilder {
        flow.append(message, color, 22.0)
        return this
    }

    fun append(message: String, color: Color, fontSize: Double): TextFlowBuilder {
        flow.append(message, color, fontSize)
        return this
    }

    fun build(): TextFlow {
        return flow
    }
}