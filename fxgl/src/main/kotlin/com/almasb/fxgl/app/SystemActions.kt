/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dev.DevStage
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.ui.UI
import com.almasb.sslogger.Logger
import javafx.scene.input.KeyCode

/**
 * Default FXGL system actions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object SystemActions {

    private val log = Logger.get(javaClass)

    /**
     * Binds system actions to keys and registers with the [input] service.
     */
    fun bind(input: Input) {
        input.addAction(screenshot(), KeyCode.P)
        input.addAction(devOptions(), KeyCode.DIGIT1)
        input.addAction(sysdump(), KeyCode.DIGIT9, InputModifier.CTRL)
        input.addAction(restartGame(), KeyCode.R, InputModifier.CTRL)
    }

    private fun screenshot() = object : UserAction("Screenshot") {
        override fun onActionBegin() {
            //val ok = FXGL.saveScreenshot()

            //FXGL.getNotificationService().pushNotification(if (ok) FXGL.getLocalizedString("dev.screenshotSaved") else FXGL.getLocalizedString("dev.screenshotFailed"))
        }
    }

    private fun devOptions() = object : UserAction("Dev Options") {

        private val devStage by lazy { DevStage(FXGL.getSettings()) }

        override fun onActionBegin() {
            if (FXGL.getSettings().applicationMode == ApplicationMode.RELEASE)
                return

            devStage.show()
        }
    }

    private fun sysdump() = object : UserAction("System info dump") {
        override fun onActionBegin() {
            //log.infof("Scene graph size: %d", DeveloperTools.getChildrenSize(FXGL.getApp().gameScene.root))
        }
    }

    private fun restartGame() = object : UserAction("Restart") {
        override fun onActionBegin() {
            FXGL.getGameController().startNewGame()
        }
    }
}