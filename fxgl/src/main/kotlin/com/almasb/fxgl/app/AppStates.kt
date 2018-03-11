/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.SetEntityFactory
import com.almasb.fxgl.event.SetEventHandler
import com.almasb.fxgl.event.Subscriber
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.AddCollisionHandler
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.scene.*
import com.almasb.fxgl.scene.intro.IntroFinishedEvent
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent

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
internal constructor(private val app: GameApplication) : AppState(/* placeholder scene */ object : FXGLScene() {}) {

    private val log = Logger.get(StartupState::class.java)

    override fun onUpdate(tpf: Double) {
        log.debug("STARTUP")

        // Start -> (Intro) -> (Menu) -> Game
        if (app.settings.isIntroEnabled) {
            app.stateMachine.startIntro()
        } else {
            if (app.settings.isMenuEnabled) {
                app.stateMachine.startMainMenu()
            } else {
                app.startNewGame()
            }
        }
    }
}

/**
 * Plays intro animation.
 * State is active only once.
 */
internal class IntroState
internal constructor(private val app: GameApplication,
                    sceneFactory: SceneFactory) : AppState(sceneFactory.newIntro()) {

    private var introFinishedSubscriber: Subscriber? = null
    private var introFinished = false

    override fun onEnter(prevState: State) {
        if (prevState is StartupState) {
            introFinishedSubscriber = FXGL.getEventBus().addEventHandler(IntroFinishedEvent.ANY, EventHandler {
                introFinished = true
            })

            (scene as IntroScene).startIntro()

        } else {
            throw IllegalArgumentException("Entered IntroState from illegal state: " + prevState)
        }
    }

    override fun onUpdate(tpf: Double) {
        if (introFinished) {
            if (FXGL.getSettings().isMenuEnabled) {
                app.stateMachine.startMainMenu()
            } else {
                FXGL.getApp().startNewGame()
            }
        }
    }

    override fun onExit() {
        introFinishedSubscriber!!.unsubscribe()
        introFinishedSubscriber = null
    }
}

/**
 * Initializes game aspects: assets, game, physics, UI, etc.
 * This task is rerun every time the game application is restarted.
 */
internal class LoadingState
internal constructor(private val app: GameApplication,
                            sceneFactory: SceneFactory) : AppState(sceneFactory.newLoadingScene()) {

    private var loadingFinished = false

    override fun onEnter(prevState: State) {
        val initTask = InitAppTask(app)
        initTask.setOnSucceeded {
            loadingFinished = true
        }

        (scene as LoadingScene).bind(initTask)

        FXGL.getExecutor().execute(initTask)
    }

    override fun onUpdate(tpf: Double) {
        if (loadingFinished) {
            app.stateMachine.startPlay()
            loadingFinished = false
        }
    }

    private class InitAppTask(private val app: GameApplication) : Task<Void>() {

        companion object {
            private val log = Logger.get(InitAppTask::class.java)

            init {
                log.warning("@SetEntityFactory is deprecated since 0.4.4!")
                log.warning("@AddCollisionHandler is deprecated since 0.4.4!")
            }
        }

        override fun call(): Void? {
            val start = System.nanoTime()

            clearPreviousGame()

            initAssets()
            initGame()
            initPhysics()
            initUI()
            initComplete()

            log.infof("Game initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0)

            return null
        }

        private fun clearPreviousGame() {
            log.debug("Clearing previous game")
            app.gameWorld.clear()
            app.physicsWorld.clear()
            app.physicsWorld.clearCollisionHandlers()
            app.gameScene.clear()
            app.gameState.clear()
            app.masterTimer.clear()
        }

        private fun initAssets() {
            update("Initializing Assets", 0)
            app.initAssets()
        }

        private fun initGame() {
            update("Initializing Game", 1)

            val vars = hashMapOf<String, Any>()
            app.initGameVars(vars)
            vars.forEach { name, value -> app.gameState.put(name, value) }

            // we just created new game state vars, so inform achievement manager about new vars
            app.gameplay.achievementManager.rebindAchievements()

            app.internalInitGame()
        }

        private fun initPhysics() {
            update("Initializing Physics", 2)
            app.initPhysics()
        }

        private fun initUI() {
            update("Initializing UI", 3)
            app.initUI()
        }

        private fun initComplete() {
            update("Initialization Complete", 4)
        }

        private fun update(message: String, step: Int) {
            log.debug(message)
            updateMessage(message)
            updateProgress(step.toLong(), 4)
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
internal constructor(sceneFactory: SceneFactory) : AppState(sceneFactory.newGameScene()) {

    val gameState: GameState
    val gameWorld: GameWorld
    val physicsWorld: PhysicsWorld

    val gameScene: GameScene
        get() = scene as GameScene

    init {
        gameState = GameState()
        gameWorld = GameWorld()
        physicsWorld = PhysicsWorld(FXGL.getAppHeight(), FXGL.getDouble("physics.ppm"))

        gameWorld.addWorldListener(physicsWorld)
        gameWorld.addWorldListener(gameScene)

        if (FXGL.getSettings().isMenuEnabled) {
            input.addEventHandler(KeyEvent.ANY, FXGL.getApp().menuListener as MenuEventHandler)
        } else {
            input.addAction(object : UserAction("Pause") {
                override fun onActionBegin() {
                    PauseMenuSubState.requestShow()
                }

                override fun onActionEnd() {
                    PauseMenuSubState.unlockSwitch()
                }
            }, FXGL.getSettings().menuKey)
        }
    }

    override fun onUpdate(tpf: Double) {
        gameWorld.onUpdate(tpf)
        physicsWorld.onUpdate(tpf)
        gameScene.onUpdate(tpf)

        FXGL.getEventBus().onUpdate(tpf)
        FXGL.getAudioPlayer().onUpdate(tpf)

        FXGL.getApp().onUpdate(tpf)
        FXGL.getApp().onPostUpdate(tpf)

        FXGL.getGameplay().stats.onUpdate(tpf)
    }
}

/**
 * State is active when the game is in main menu.
 */
internal class MainMenuState
internal constructor(sceneFactory: SceneFactory) : AppState(sceneFactory.newMainMenu(FXGL.getApp())) {

    override fun onEnter(prevState: State) {
        if (prevState is StartupState
                || prevState is IntroState
                || prevState is GameMenuState) {

            val menuHandler = FXGL.getApp().menuListener as MenuEventHandler

            if (!menuHandler.isProfileSelected())
                menuHandler.showProfileDialog()
        } else {
            throw IllegalArgumentException("Entered MainMenu from illegal state: " + prevState)
        }
    }
}

/**
 * State is active when the game is in game menu.
 */
internal class GameMenuState
internal constructor(sceneFactory: SceneFactory) : AppState(sceneFactory.newGameMenu(FXGL.getApp())) {

    init {
        input.addEventHandler(KeyEvent.ANY, FXGL.getApp().menuListener as MenuEventHandler)
    }
}