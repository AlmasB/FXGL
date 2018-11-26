/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.centerTextBind
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
    protected val text = Text()

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
        }

        centerTextBind(
                text,
                settings.width / 2.0,
                settings.height * 4 / 5.0
        )

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