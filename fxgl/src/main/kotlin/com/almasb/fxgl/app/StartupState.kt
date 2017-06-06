/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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
private constructor(private val app: GameApplication) : AppState(object : FXGLScene() {}) {

    private val log = FXGLLogger.get(StartupState::class.java)

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