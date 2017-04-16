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

package com.almasb.fxgl.app.state

import com.almasb.fxgl.app.*
import com.almasb.fxgl.core.logging.FXGLLogger
import com.almasb.fxgl.io.FXGLIO
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.service.Input
import com.almasb.fxgl.service.listener.FXGLListener

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object StartupState : AppState {

    private val scene = lazy { object : FXGLScene() {} }

    private val log = FXGLLogger.get(javaClass)

    override fun onEnter(prevState: State) {
        val app = FXGL.getApp()

        FXGLIO.defaultExceptionHandler = app.exceptionHandler
        FXGLIO.defaultExecutor = app.executor

        //app.initAchievements()

        // we call this early to process user input bindings
        // so we can correctly display them in menus
        // 1. register system actions
        SystemActions.bind(app.input)

        app.runPreInit()

        // 2. register user actions
        //app.initInput()

        // 3. scan for annotated methods and register them too
        app.input.scanForUserActions(app)

        //app.preInit()

        //

        if (app.settings.isProfilingEnabled) {
            val profiler = FXGL.newProfiler()

            app.addFXGLListener(object : FXGLListener {
                override fun onExit() {
                    profiler.stop()
                    profiler.print()
                }
            })

            log.debug("Injecting profiler")
            //app.profiler = profiler
            profiler.start()
        }

        app.runTask(InitEventHandlersTask::class.java)

        // intro runs async so we have to wait with a callback
        // Stage -> (Intro) -> (Menu) -> Game
        // if not enabled, call finished directly
        if (app.getSettings().isIntroEnabled()) {
            app.setState(ApplicationState.INTRO)
        } else {
            if (app.getSettings().isMenuEnabled()) {
                app.setState(ApplicationState.MAIN_MENU)
            } else {
                app.startNewGame()
            }
        }
    }

    override fun onExit() {
    }

    override fun onUpdate(tpf: Double) {
    }

    override fun scene(): FXGLScene {
        return scene.value
    }

    override fun input(): Input {
        return scene().input
    }
}