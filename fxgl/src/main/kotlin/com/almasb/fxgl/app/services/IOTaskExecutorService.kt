/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.Executor
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.ui.DialogService

class IOTaskExecutorService : EngineService() {

    private lateinit var dialogService: DialogService

    private val executor: Executor = Async

    fun <T> run(task: IOTask<T>): T {
        return task.run()
    }

    /**
     * Executes this task asynchronously on a background thread.
     * All callbacks will be called on that background thread.
     */
    fun <T> runAsync(task: IOTask<T>) {
        executor.execute { task.run() }
    }

    /**
     * Executes this task asynchronously on a background thread.
     * All callbacks will be called on the JavaFX thread.
     */
    fun <T> runAsyncFX(task: IOTask<T>) {
        val fxTask = task.toJavaFXTask()
        fxTask.setOnFailed {
            if (!task.hasFailAction())
                dialogService.showErrorBox(fxTask.exception ?: RuntimeException("Unknown error"))
        }

        executor.execute(fxTask)
    }

    /**
     * Executes this task asynchronously on a background thread whilst showing a progress dialog.
     * The dialog will be dismissed after task is completed, whether succeeded or failed.
     * All callbacks will be called on the JavaFX thread.
     */
    fun <T> runAsyncFXWithDialog(task: IOTask<T>, message: String) {
        val dialog = dialogService.showProgressBox(message)

        val fxTask = task.toJavaFXTask()
        fxTask.setOnSucceeded {
            dialog.close()
        }
        fxTask.setOnFailed {
            dialog.close()

            if (!task.hasFailAction())
                dialogService.showErrorBox(fxTask.exception ?: RuntimeException("Unknown error"))
        }

        executor.execute(fxTask)
    }
}