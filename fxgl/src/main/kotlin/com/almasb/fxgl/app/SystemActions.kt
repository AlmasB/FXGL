/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
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

        if (FXGL.getSettings().applicationMode != ApplicationMode.RELEASE) {
            if (FXGL.getSettings().isDeveloperMenuEnabled) {
                input.addAction(devOptions(), KeyCode.DIGIT1)
                input.addAction(devConsole(), KeyCode.DIGIT2)
            }

            input.addAction(sysdump(), KeyCode.DIGIT9, InputModifier.CTRL)
            input.addAction(restartGame(), KeyCode.R, InputModifier.CTRL)
        }
    }

    private fun screenshot() = object : UserAction("Screenshot") {
        override fun onActionBegin() {
            //val ok = FXGL.saveScreenshot()

            //FXGL.getNotificationService().pushNotification(if (ok) FXGL.getLocalizedString("dev.screenshotSaved") else FXGL.getLocalizedString("dev.screenshotFailed"))
        }
    }

    private fun devConsole() = object : UserAction("Dev Console") {

        override fun onActionBegin() {
            if (FXGL.getSettings().applicationMode == ApplicationMode.RELEASE)
                return

            FXGL.getDevService().openConsole()
        }
    }

    private fun devOptions() = object : UserAction("Dev Options") {

        override fun onActionBegin() {
            if (FXGL.getSettings().applicationMode == ApplicationMode.RELEASE)
                return

            if (FXGL.getDevPane().isOpen) {
                FXGL.getDevPane().close()
            } else {
                FXGL.getDevPane().open()
            }
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