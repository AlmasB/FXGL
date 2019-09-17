/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.saving.DataFile
import com.almasb.sslogger.Logger
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

    private var loadingFinished = false
    internal var dataFile = DataFile.EMPTY

    init {
        val settings = FXGL.getSettings()

        with(progress) {
            setPrefSize(settings.width - 200.0, 10.0)
            translateX = 100.0
            translateY = settings.height - 100.0
        }

        with(text) {
            if (!settings.isExperimentalNative) {
                font = FXGL.getUIFactory().newFont(24.0)
            }

            fill = Color.WHITE
        }

        FXGL.centerTextBind(
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
    open fun bind(task: Task<*>) {
        progress.progressProperty().bind(task.progressProperty())
        text.textProperty().bind(task.messageProperty())
    }

    override fun onCreate() {
        val initTask = InitAppTask(FXGL.getApp(), dataFile)
        initTask.setOnSucceeded {
            loadingFinished = true
        }

        bind(initTask)

        FXGL.getExecutor().execute(initTask)
    }

    override fun onUpdate(tpf: Double) {
        if (loadingFinished) {
            FXGL.getGameController().gotoPlay()
            loadingFinished = false
        }
    }

    /**
     * Clears previous game.
     * Initializes game, physics and UI.
     * This task is rerun every time the game application is restarted.
     */
    private class InitAppTask(private val app: GameApplication,
                              private val dataFile: DataFile) : Task<Void>() {

        companion object {
            private val log = Logger.get<InitAppTask>()
        }

        override fun call(): Void? {
            val start = System.nanoTime()

            clearPreviousGame()

            initGame()
            initPhysics()
            initUI()
            initComplete()

            log.infof("Game initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0)

            return null
        }

        private fun clearPreviousGame() {
            log.debug("Clearing previous game")
            FXGL.getGameWorld().clear()
            FXGL.getPhysicsWorld().clear()
            FXGL.getPhysicsWorld().clearCollisionHandlers()
            FXGL.getGameScene().clear()
            FXGL.getGameState().clear()
            FXGL.getGameTimer().clear()
        }

        private fun initGame() {
            update("Initializing Game", 0)

            val vars = hashMapOf<String, Any>()
            app.initGameVars(vars)

            vars.forEach { (name, value) ->
                FXGL.getGameState().setValue(name, value)
            }

            if (dataFile === DataFile.EMPTY) {
                app.initGame()
            } else {
                app.loadState(dataFile)
            }
        }

        private fun initPhysics() {
            update("Initializing Physics", 1)
            app.initPhysics()
        }

        private fun initUI() {
            update("Initializing UI", 2)
            app.initUI()
        }

        private fun initComplete() {
            update("Initialization Complete", 3)
            FXGL.getGameController().onGameReady(FXGL.getGameState().properties)
        }

        private fun update(message: String, step: Int) {
            log.debug(message)
            updateMessage(message)
            updateProgress(step.toLong(), 3)
        }

        override fun failed() {
            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), exception ?: RuntimeException("Initialization failed"))
        }
    }
}