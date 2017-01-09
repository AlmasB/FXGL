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

import com.almasb.fxgl.service.Input
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
            val ok = FXGL.getDisplay().saveScreenshot()

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