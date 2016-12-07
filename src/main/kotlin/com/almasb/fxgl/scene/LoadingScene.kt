/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import javafx.concurrent.Task
import javafx.scene.control.ProgressBar
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text

/**
 * Loading scene to be used during loading tasks.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
open class LoadingScene : FXGLScene() {

    private val progress = ProgressBar()
    val text = Text()

    init {
        val settings = FXGL.getSettings()

        with(progress) {
            setPrefSize(settings.width - 200.0, 10.0)
            translateX = 100.0
            translateY = settings.height - 100.0
        }

        with(text) {
            font = FXGL.getUIFactory().newFont(24.0)
            fill = Color.WHITE
            translateX = (settings.width / 2 - 100).toDouble()
            translateY = (settings.height * 4 / 5).toDouble()
        }

        contentRoot.children.addAll(
                Rectangle(settings.width.toDouble(),
                        settings.height.toDouble(),
                        Color.rgb(0, 0, 10)),
                progress, text)
    }

    /**
     * Bind to progress and text messages of given background loading task.
     *
     * @param task the loading task
     */
    fun bind(task: Task<*>) {
        progress.progressProperty().bind(task.progressProperty())
        text.textProperty().bind(task.messageProperty())
    }
}