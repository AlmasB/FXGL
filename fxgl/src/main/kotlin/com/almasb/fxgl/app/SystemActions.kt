/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

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

    /**
     * Binds system actions to keys and registers with the [input] service.
     */
    fun bind(input: Input) {
        input.addAction(screenshot(), KeyCode.P)
        input.addAction(devOptions(), KeyCode.DIGIT0, InputModifier.CTRL)
    }

    private fun screenshot() = object : UserAction("Screenshot") {
        override fun onActionBegin() {
            val ok = FXGL.getApp().saveScreenshot()

            FXGL.getNotificationService().pushNotification(if (ok) "Screenshot saved" else "Screenshot failed")
        }
    }

    private fun devOptions() = object : UserAction("Dev Options") {
        private var devBarOpen = false
        private var devUI: UI? = null

        override fun onActionBegin() {
            if (FXGL.getSettings().applicationMode == ApplicationMode.RELEASE)
                return

            if (devUI == null) {
                devUI = FXGL.getAssetLoader().loadUI("dev_menu_bar.fxml", DeveloperMenuBarController())
            }

            if (devBarOpen) {
                FXGL.getApp().gameScene.removeUI(devUI)
                devBarOpen = false
            } else {
                FXGL.getApp().gameScene.addUI(devUI)
                devBarOpen = true
            }
        }
    }
}