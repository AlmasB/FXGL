/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.event.FXGLEvent
import javafx.concurrent.Task
import java.io.Serializable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InitAppTask(val app: GameApplication, val data: Serializable?) : Task<Void>() {

    private val log = FXGL.getLogger(javaClass)

    constructor(app: GameApplication) : this(app, null)

    override fun call(): Void? {
        update("Initializing Assets", 0)
        app.initAssets()

        update("Initializing Game", 1)
        if (data == null)
            app.initGame()
        else
            app.loadState(data)

        update("Initializing Physics", 2)
        app.initPhysics()

        update("Initializing UI", 3)
        app.initUI()
        app.initFPSOverlay()

        update("Initialization Complete", 4)
        return null
    }

    private fun update(message: String, step: Int) {
        log.debug(message)
        updateMessage(message)
        updateProgress(step.toLong(), 4)
    }

    override fun succeeded() {
        app.getEventBus().fireEvent(FXGLEvent.initAppComplete())
        app.resume()
    }

    override fun failed() {
        Thread.getDefaultUncaughtExceptionHandler()
                .uncaughtException(Thread.currentThread(), exception ?: RuntimeException("Initialization failed"))
    }
}