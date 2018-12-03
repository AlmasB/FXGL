/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.util.BiConsumer
import com.almasb.fxgl.core.util.forEach
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.event.Subscriber
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.saving.DataFile
import com.almasb.fxgl.scene.*
import com.almasb.fxgl.scene.intro.IntroFinishedEvent
import com.almasb.sslogger.Logger
import javafx.concurrent.Task
import javafx.event.EventHandler

/**
 * All app states.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/**
 * The first state.
 * Active only once.
 */
internal class StartupState
internal constructor(scene: FXGLScene) : AppState(scene) {

    private val log = Logger.get<StartupState>()

    override fun onUpdate(tpf: Double) {
        log.debug("STARTUP")

        // Start -> (Intro) -> (Menu) -> Game
        if (FXGL.getSettings().isIntroEnabled) {
            FXGL.getStateMachine().startIntro()
        } else {
            if (FXGL.getSettings().isMenuEnabled) {
                FXGL.getGameController().gotoMainMenu()
            } else {
                FXGL.getGameController().startNewGame()
            }
        }
    }
}

/**
 * Plays intro animation.
 * State is active only once.
 */
internal class IntroState
internal constructor(scene: FXGLScene) : AppState(scene) {

    private val introScene = scene as IntroScene

    private var introFinishedSubscriber: Subscriber? = null
    private var introFinished = false

    override fun onEnter(prevState: State) {
        check(prevState is StartupState) {
            "Entered IntroState from illegal state: $prevState"
        }

        introFinishedSubscriber = FXGL.getEventBus().addEventHandler(IntroFinishedEvent.ANY, EventHandler {
            introFinished = true
        })

        (scene as IntroScene).startIntro()
    }

    override fun onUpdate(tpf: Double) {
        introScene.onUpdate(tpf)

        if (introFinished) {
            if (FXGL.getSettings().isMenuEnabled) {
                FXGL.getGameController().gotoMainMenu()
            } else {
                FXGL.getGameController().startNewGame()
            }
        }
    }

    override fun onExit() {
        introFinishedSubscriber!!.unsubscribe()
        introFinishedSubscriber = null
    }
}

/**
 * State is active during game initialization.
 */
internal class LoadingState
internal constructor(private val app: GameApplication, scene: FXGLScene) : AppState(scene) {

    private var loadingFinished = false
    internal var dataFile = DataFile.EMPTY

    override fun onEnter(prevState: State) {
        val initTask = InitAppTask(app, dataFile)
        initTask.setOnSucceeded {
            loadingFinished = true
        }

        (scene as LoadingScene).bind(initTask)

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
            FXGL.getMasterTimer().clear()
        }

        private fun initGame() {
            update("Initializing Game", 0)

            val vars = hashMapOf<String, Any>()
            app.initGameVars(vars)
            forEach(vars, BiConsumer { name, value -> FXGL.getGameState().setValue(name, value) })

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

/**
 * State is active when the game is being played.
 * The state in which the player will spend most of the time.
 */
internal class PlayState
internal constructor(scene: FXGLScene) : AppState(scene) {

    val gameState: GameState
    val gameWorld: GameWorld
    val physicsWorld: PhysicsWorld

    val gameScene: GameScene
        get() = scene as GameScene

    init {
        gameState = GameState()
        gameWorld = GameWorld()
        physicsWorld = PhysicsWorld(FXGL.getAppHeight(), FXGL.getSettings().pixelsPerMeter)

        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(gameScene)
    }

    override fun onUpdate(tpf: Double) {
        // if single step is configured, then step() will be called manually
        if (FXGL.getSettings().isSingleStep)
            return

        step(tpf)
    }

    fun step(tpf: Double) {
        gameWorld.onUpdate(tpf)
        physicsWorld.onUpdate(tpf)
        gameScene.onUpdate(tpf)

        FXGL.getEventBus().onUpdate(tpf)
        FXGL.getAudioPlayer().onUpdate(tpf)

        FXGL.getApp().onUpdate(tpf)
        FXGL.getApp().onPostUpdate(tpf)
    }
}

/**
 * State is active when the game is in main menu.
 */
internal class MainMenuState
internal constructor(scene: FXGLScene) : AppState(scene) {

    private val menuScene = scene as FXGLMenu

    override fun onEnter(prevState: State) {
        menuScene.onEnter()
    }

    override fun onUpdate(tpf: Double) {
        menuScene.onUpdate(tpf)
    }

    override fun onExit() {
        menuScene.onExit()
    }
}

/**
 * State is active when the game is in game menu.
 */
internal class GameMenuState
internal constructor(scene: FXGLScene) : AppState(scene) {

    private val menuScene = scene as FXGLMenu

    override fun onEnter(prevState: State) {
        menuScene.onEnter()
    }

    override fun onUpdate(tpf: Double) {
        menuScene.onUpdate(tpf)
    }

    override fun onExit() {
        menuScene.onExit()
    }
}