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

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.ui.InGameWindow
import javafx.animation.ScaleTransition
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.util.Duration

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestWindow
@JvmOverloads
constructor(title: String = "Quests", val questPane: QuestPane) : com.almasb.fxgl.ui.InGameWindow(title, WindowDecor.MINIMIZE) {

    init {
        isResizableWindow = false
        setPrefSize(questPane.prefWidth + 25, questPane.prefHeight + 32)
        setBackgroundColor(Color.TRANSPARENT)

        val scroll = ScrollPane(questPane)
        scroll.vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
        scroll.maxHeight = prefHeight
        scroll.style = "-fx-background: black;"

        val pane = Pane(scroll)

        contentPane = pane

        val handler = rightIcons[0].onAction
        rightIcons[0].setOnAction { e ->
            val st = ScaleTransition(Duration.seconds(0.2), pane)
            st.fromY = (if (isMinimized) 0 else 1).toDouble()
            st.toY = (if (isMinimized) 1 else 0).toDouble()
            st.play()

            handler.handle(e)
        }
    }
}