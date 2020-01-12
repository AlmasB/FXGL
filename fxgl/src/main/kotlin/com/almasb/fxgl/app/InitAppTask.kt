/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dsl.FXGL
import com.almasb.sslogger.Logger
import javafx.concurrent.Task

/**
 * Clears previous game.
 * Initializes game, physics and UI.
 * This task is rerun every time the game application is restarted.
 */
internal class InitAppTask(private val app: GameApplication) : Task<Void>() {

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
        FXGL.getWorldProperties().clear()
        FXGL.getGameTimer().clear()
    }

    private fun initGame() {
        update("Initializing Game", 0)

        val vars = hashMapOf<String, Any>()
        app.initGameVars(vars)

        vars.forEach { (name, value) ->
            FXGL.getWorldProperties().setValue(name, value)
        }

        app.initGame()
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
        FXGL.getGameController().onGameReady(FXGL.getWorldProperties())
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