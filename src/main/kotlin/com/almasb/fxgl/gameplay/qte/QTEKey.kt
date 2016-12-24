/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.gameplay.qte

import com.almasb.fxgl.app.FXGL
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
 * Represents a single QTE key visible on the screen.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QTEKey(val keyCode: KeyCode) : StackPane() {

    private val background = Rectangle(72.0, 72.0, Color.BLACK)
    private val text = FXGL.getUIFactory().newText(keyCode.getName(), Color.WHITE, 72.0)

    init {
        background.stroke = Color.BLACK
        background.strokeWidth = 4.0

        val border = Rectangle(72.0, 72.0, null)
        border.arcWidth = 25.0
        border.arcHeight = 25.0
        border.stroke = Color.GRAY
        border.strokeWidth = 6.0

        children.addAll(background, border, text)
    }

    fun lightUp() {
        background.fill = Color.YELLOW
        text.fill = Color.BLACK
    }
}