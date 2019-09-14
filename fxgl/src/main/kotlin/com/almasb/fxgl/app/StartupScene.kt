/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dsl.getGameController
import com.almasb.fxgl.dsl.getSettings
import com.almasb.sslogger.Logger

/**
 * This is the default startup scene which is shown while FXGL is in startup state.
 *
 * As soon as the main loop starts, this scene will be switched instantly, so
 * there is no need to do animation in this state.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class StartupScene : FXGLScene() {

    private val log = Logger.get(javaClass)

    override fun onUpdate(tpf: Double) {
        log.debug("STARTUP")

        // Start -> (Intro) -> (Menu) -> Game
        if (getSettings().isIntroEnabled) {
            getGameController().gotoIntro()
        } else {
            if (getSettings().isMenuEnabled) {
                getGameController().gotoMainMenu()
            } else {
                getGameController().startNewGame()
            }
        }
    }
}