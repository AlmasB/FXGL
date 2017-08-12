/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.annotation.AddCollisionHandler
import com.almasb.fxgl.annotation.SetEntityFactory
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.ecs.GameWorld
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.event.Subscriber
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.saving.DataFile
import com.almasb.fxgl.scene.*
import com.almasb.fxgl.scene.intro.IntroFinishedEvent
import com.google.inject.Inject
import com.google.inject.Singleton
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
@Singleton
internal class StartupState
@Inject
// placeholder scene, will be replaced by next state
private constructor(private val app: GameApplication) : AppState(object : FXGLScene() {}) {

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
@Singleton
internal class IntroState
@Inject
private constructor(private val app: GameApplication,
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
@Singleton
internal class LoadingState
@Inject private constructor(private val app: GameApplication,
                            sceneFactory: SceneFactory) : AppState(sceneFactory.newLoadingScene()) {

    var dataFile = DataFile.EMPTY

    private var loadingFinished = false

    override fun onEnter(prevState: State) {

        val initTask = InitAppTask(app, dataFile)
        initTask.setOnSucceeded {
            loadingFinished = true
        }

        dataFile = DataFile.EMPTY

        (scene as LoadingScene).bind(initTask)

        FXGL.getExecutor().execute(initTask)
    }

    override fun onUpdate(tpf: Double) {
        if (loadingFinished) {
            app.stateMachine.startPlay()
            loadingFinished = false
        }
    }

    private class InitAppTask(private val app: GameApplication, private val dataFile: DataFile) : Task<Void>() {

        companion object {
            private val log = FXGL.getLogger(InitAppTask::class.java)

            private val annotationMap: Map<Class<*>, List<Class<*>>>

            init {
                annotationMap = scanForAnnotations()

                annotationMap.forEach { annotationClass, list ->
                    log.debug("@${annotationClass.simpleName}: ${list.map { it.simpleName }}")
                }
            }

            private fun scanForAnnotations(): Map<Class<*>, List<Class<*>>> {
                val app = FXGL.getApp()

                if (app.javaClass.`package` != null) {

                    val name = app.javaClass.`package`.name

                    if (name.contains("[A-Z]".toRegex())) {
                        log.warning("${app.javaClass.simpleName} package ($name) contains uppercase letters. Disabling annotations processing")

                        return hashMapOf()
                    }

                    // only scan the appropriate package (package of the "App") and its subpackages
                    return ReflectionUtils.findClasses(app.javaClass.`package`.name,
                            SetEntityFactory::class.java, AddCollisionHandler::class.java)
                } else {
                    log.warning("${app.javaClass.simpleName} has no package. Disabling annotations processing")

                    return hashMapOf()
                }
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

            annotationMap[SetEntityFactory::class.java]?.let {
                if (it.isNotEmpty())
                    app.gameWorld.setEntityFactory(FXGL.getInstance(it[0]) as EntityFactory)
            }

            if (dataFile === DataFile.EMPTY)
                app.initGame()
            else
                app.loadState(dataFile)
        }

        private fun initPhysics() {
            update("Initializing Physics", 2)
            app.initPhysics()

            annotationMap[AddCollisionHandler::class.java]?.let {
                it.forEach {
                    app.physicsWorld.addCollisionHandler(FXGL.getInstance(it) as CollisionHandler)
                }
            }
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
@Singleton
internal class PlayState
@Inject
private constructor(val gameState: GameState,
                    val gameWorld: GameWorld,
                    val physicsWorld: PhysicsWorld,
                    sceneFactory: SceneFactory) : AppState(sceneFactory.newGameScene()) {

    init {
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
    }

    val gameScene: GameScene
        get() = scene as GameScene
}

/**
 * State is active when the game is in main menu.
 */
@Singleton
internal class MainMenuState
@Inject
private constructor(sceneFactory: SceneFactory) : AppState(sceneFactory.newMainMenu(FXGL.getApp())) {

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
@Singleton
internal class GameMenuState
@Inject
private constructor(sceneFactory: SceneFactory) : AppState(sceneFactory.newGameMenu(FXGL.getApp())) {

    init {
        input.addEventHandler(KeyEvent.ANY, FXGL.getApp().menuListener as MenuEventHandler)
    }
}