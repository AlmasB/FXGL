/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.sslogger.Logger
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.ui.UI
import javafx.scene.input.KeyCode

/**
 * Default FXGL system actions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object SystemActions {

    private val log = Logger.get("DevTools")

    /**
     * Binds system actions to keys and registers with the [input] service.
     */
    fun bind(input: Input) {
        input.addAction(screenshot(), KeyCode.P)
        input.addAction(restartGame(), KeyCode.R, InputModifier.CTRL)
    }

    private fun screenshot() = object : UserAction("Screenshot") {
        override fun onActionBegin() {
            val ok = FXGL.saveScreenshot()

            //FXGL.getNotificationService().pushNotification(if (ok) FXGL.getLocalizedString("dev.screenshotSaved") else FXGL.getLocalizedString("dev.screenshotFailed"))
        }
    }

    private fun restartGame() = object : UserAction("Restart") {
        override fun onActionBegin() {
            FXGL.getApp().startNewGame()
        }
    }
}