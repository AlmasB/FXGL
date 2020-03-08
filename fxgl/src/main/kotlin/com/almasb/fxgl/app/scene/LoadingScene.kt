/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.scene

import com.almasb.fxgl.dsl.FXGL
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
abstract class LoadingScene : FXGLScene() {

    fun pushNewTask(task: Runnable) {
        pushNewTask(object : Task<Void?>() {
            override fun call(): Void? {
                task.run()
                return null
            }
        })
    }

    fun pushNewTask(task: Task<*>) {
        task.setOnSucceeded {
            controller.gotoPlay()
        }

        bind(task)

        FXGL.getExecutor().execute(task)
    }

    /**
     * Bind to listen for updates of given background loading task.
     *
     * @param task the loading task
     */
    protected open fun bind(task: Task<*>) { }
}

class FXGLLoadingScene : LoadingScene() {

    private val progress = ProgressBar()
    private val text = Text()

    init {
        with(progress) {
            setPrefSize(appWidth - 200.0, 10.0)
            translateX = 100.0
            translateY = appHeight - 100.0
        }

        with(text) {
            font = FXGL.getUIFactoryService().newFont(24.0)
            fill = Color.WHITE
        }

        FXGL.centerTextBind(
                text,
                appWidth / 2.0,
                appHeight * 4 / 5.0
        )

        contentRoot.children.addAll(
                Rectangle(appWidth.toDouble(), appHeight.toDouble(), Color.rgb(0, 0, 10)),
                progress,
                text
        )
    }

    override fun bind(task: Task<*>) {
        progress.progressProperty().bind(task.progressProperty())
        text.textProperty().bind(task.messageProperty())
    }
}