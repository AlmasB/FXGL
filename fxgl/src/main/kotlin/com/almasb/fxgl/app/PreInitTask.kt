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

import com.almasb.easyio.EasyIO
import com.google.inject.Inject

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PreInitTask
@Inject constructor(private val app: com.almasb.fxgl.app.GameApplication) : Runnable {

    private val log = FXGL.getLogger(javaClass)

    override fun run() {
        EasyIO.defaultExceptionHandler = app.getExceptionHandler()
        EasyIO.defaultExecutor = app.getExecutor()

        log.debug("Injecting gameWorld & physicsWorld")
        app.gameWorld = FXGL.getInstance(com.almasb.fxgl.gameplay.GameWorld::class.java)
        app.physicsWorld = FXGL.getInstance(com.almasb.fxgl.physics.PhysicsWorld::class.java)

        app.initAchievements()

        // we call this early to process user input bindings
        // so we can correctly display them in menus
        // 1. register system actions
        SystemActions.bind(app.getInput())

        // 2. register user actions
        app.initInput()

        // 3. scan for annotated methods and register them too
        app.getInput().scanForUserActions(app)

        app.preInit()
    }
}