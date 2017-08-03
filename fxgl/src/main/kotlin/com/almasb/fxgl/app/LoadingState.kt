/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.annotation.AddCollisionHandler
import com.almasb.fxgl.annotation.SetEntityFactory
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.saving.DataFile
import com.almasb.fxgl.scene.LoadingScene
import com.almasb.fxgl.scene.SceneFactory
import com.google.inject.Inject
import com.google.inject.Singleton
import javafx.concurrent.Task

/**
 * Initializes game aspects: assets, game, physics, UI, etc.
 * This task is rerun every time the game application is restarted.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
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
                val app = FXGL.getApp()

                annotationMap = if (app.javaClass.`package` != null) {

                    // only scan the appropriate package (package of the "App") and its subpackages
                    ReflectionUtils.findClasses(app.javaClass.`package`.name,
                            SetEntityFactory::class.java, AddCollisionHandler::class.java)
                } else {
                    log.warning("${app.javaClass.simpleName} has no package. Disabling annotations processing")

                    hashMapOf()
                }

                annotationMap.forEach { annotationClass, list ->
                    log.debug("@${annotationClass.simpleName}: ${list.map { it.simpleName }}")
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