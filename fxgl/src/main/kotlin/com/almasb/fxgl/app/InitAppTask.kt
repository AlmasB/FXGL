/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.app

import com.almasb.fxgl.entity.EntityFactory
import com.almasb.fxgl.annotation.SetEntityFactory
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.logging.SystemLogger
import com.almasb.fxgl.annotation.AddCollisionHandler
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.saving.DataFile
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import javafx.concurrent.Task
import java.util.*

/**
 * Initializes game aspects: assets, game, physics, UI, etc.
 * This task is rerun every time the game application is restarted.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InitAppTask(val app: GameApplication, val dataFile: DataFile) : Task<Void>() {

    companion object {
        private val log = FXGL.getLogger(InitAppTask::class.java)
    }

    constructor(app: GameApplication) : this(app, DataFile.EMPTY)

    override fun call(): Void? {
        val start = System.nanoTime()

        update("Initializing Assets", 0)
        app.initAssets()

        update("Initializing Game", 1)

        if (app.gameState != null) {
            log.debug("Clearing previous gameState")
            app.gameState.clear()
        }

        log.debug("Injecting gameState")
        app.gameState = GameState()

        val vars = hashMapOf<String, Any>()
        app.initGameVars(vars)
        vars.forEach { name, value -> app.gameState.put(name, value) }

        val annotationMap = scanForAnnotations()

        annotationMap[SetEntityFactory::class.java]?.let {
            if (it.isNotEmpty())
                app.gameWorld.setEntityFactory(FXGL.getInstance(it[0]) as EntityFactory)
        }

        if (dataFile === DataFile.EMPTY)
            app.initGame()
        else
            app.loadState(dataFile)

        update("Initializing Physics", 2)
        app.initPhysics()

        annotationMap[AddCollisionHandler::class.java]?.let {
            it.forEach { app.physicsWorld.addCollisionHandler(FXGL.getInstance(it) as CollisionHandler) }
        }

        update("Initializing UI", 3)
        app.initUI()

        update("Initialization Complete", 4)

        SystemLogger.infof("Game initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0)

        return null
    }

    private fun update(message: String, step: Int) {
        log.debug(message)
        updateMessage(message)
        updateProgress(step.toLong(), 4)
    }

    override fun succeeded() {
        app.resume()
    }

    override fun failed() {
        Thread.getDefaultUncaughtExceptionHandler()
                .uncaughtException(Thread.currentThread(), exception ?: RuntimeException("Initialization failed"))
    }

    private fun scanForAnnotations(): Map<Class<*>, List<Class<*>>> {
        val map = hashMapOf<Class<*>, ArrayList<Class<*>>>()

        // this ensures that we only scan the appropriate package,
        // i.e. the package of the "App" and any subpackages recursively
        // also speeds up the scanning
        val scanner = FastClasspathScanner(app.javaClass.`package`.name)

        map[SetEntityFactory::class.java] = arrayListOf()
        scanner.matchClassesWithAnnotation(SetEntityFactory::class.java, {
            log.debug("@SetEntityFactory: $it")
            map[SetEntityFactory::class.java]!!.add(it)
        })

        map[AddCollisionHandler::class.java] = arrayListOf()
        scanner.matchClassesWithAnnotation(AddCollisionHandler::class.java, {
            log.debug("@AddCollisionHandler: $it")
            map[AddCollisionHandler::class.java]!!.add(it)
        })

        scanner.scan()

        return map
    }
}