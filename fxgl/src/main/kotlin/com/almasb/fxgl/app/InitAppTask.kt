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
import com.almasb.fxgl.entity.SetEntityFactory
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.saving.DataFile
import com.almasb.fxgl.logging.SystemLogger
import com.almasb.fxgl.physics.AddCollisionHandler
import com.almasb.fxgl.physics.CollisionHandler
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import javafx.concurrent.Task

/**
 *
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
        vars.forEach { name, value -> app.gameState.setValue(name, value) }

        scanForAnnotations()

        if (dataFile === DataFile.EMPTY)
            app.initGame()
        else
            app.loadState(dataFile)

        update("Initializing Physics", 2)
        app.initPhysics()


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
        //app.getEventBus().fireEvent(FXGLEvent.initAppComplete())
        app.resume()
    }

    override fun failed() {
        Thread.getDefaultUncaughtExceptionHandler()
                .uncaughtException(Thread.currentThread(), exception ?: RuntimeException("Initialization failed"))
    }

    private fun scanForAnnotations() {
        // this ensures that we only scan the appropriate package,
        // i.e. the package of the "App" and any subpackages recursively
        // also speeds up the scanning
        val scanner = FastClasspathScanner(app.javaClass.`package`.name)

        scanner.matchClassesWithAnnotation(SetEntityFactory::class.java, {
            app.gameWorld.setEntityFactory(FXGL.getInstance(it) as EntityFactory)
        })

        scanner.matchClassesWithAnnotation(AddCollisionHandler::class.java, {
            app.physicsWorld.addCollisionHandler(FXGL.getInstance(it) as CollisionHandler)
        })

        scanner.scan()
    }
}