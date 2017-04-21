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

import com.almasb.fxgl.core.logging.FXGLLogger
import com.almasb.fxgl.scene.FXGLScene
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Singleton
internal class StartupState
@Inject
// placeholder scene, will be replaced by next state
private constructor() : AppState(object : FXGLScene() {}) {

    private val log = FXGLLogger.get(StartupState::class.java)

    override fun onUpdate(tpf: Double) {
        log.debug("STARTUP")

        val app = FXGL.getApp()

        // Start -> (Intro) -> (Menu) -> Game
        if (app.settings.isIntroEnabled) {
            app.startIntro()
        } else {
            if (app.settings.isMenuEnabled) {
                app.startMainMenu()
            } else {
                app.startNewGame()
            }
        }
    }
}