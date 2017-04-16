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

package com.almasb.fxgl.app;

import com.almasb.fxgl.core.logging.FXGLLogger;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.io.FXGLIO;
import com.almasb.fxgl.scene.FXGLScene;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
class StartupState extends AppState {

    private static final Logger log = FXGLLogger.get(StartupState.class);

    @Inject
    private StartupState() {
        super(new FXGLScene() {});
    }

    @Override
    public void onEnter(State prevState) {
        GameApplication app = FXGL.getApp();

        FXGLIO.INSTANCE.setDefaultExceptionHandler(app.getExceptionHandler());
        FXGLIO.INSTANCE.setDefaultExecutor(app.getExecutor());

        //app.initAchievements()

        // we call this early to process user input bindings
        // so we can correctly display them in menus
        // 1. register system actions
        SystemActions.INSTANCE.bind(app.getInput());

        app.runPreInit();

        // 2. register user actions
        //app.initInput()

        // 3. scan for annotated methods and register them too
        app.getInput().scanForUserActions(app);

        //app.preInit()

        //

        // TODO: PROFILER
//        if (app.settings.isProfilingEnabled) {
//            val profiler = FXGL.newProfiler()
//
//            app.addFXGLListener(object : FXGLListener {
//                override fun onExit() {
//                    profiler.stop()
//                    profiler.print()
//                }
//            })
//
//            log.debug("Injecting profiler")
//            //app.profiler = profiler
//            profiler.start();
//        }

        app.runTask(InitEventHandlersTask.class);

        // intro runs async so we have to wait with a callback
        // Stage -> (Intro) -> (Menu) -> Game
        // if not enabled, call finished directly
        if (app.getSettings().isIntroEnabled()) {
            app.setState(ApplicationState.INTRO);
        } else {
            if (app.getSettings().isMenuEnabled()) {
                app.setState(ApplicationState.MAIN_MENU);
            } else {
                app.startNewGame();
            }
        }
    }
}
