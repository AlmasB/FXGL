/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.util.Version
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.util.Duration
import java.util.function.Consumer

/**
 * Handles everything related to FXGL update.
 * First checks if the update is necessary.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class UpdaterTask : Runnable {

    private val log = FXGL.getLogger(javaClass)
    private lateinit var updateCheckTimer: LocalTimer

    /**
     * Checks for updates if necessary, blocking call.
     */
    override fun run() {
        if (shouldCheckForUpdate()) {
            checkForUpdates()
        }
    }

    /**
     * Returns true if first run or required number of days have passed.
     *
     * @return whether we need check for updates
     */
    private fun shouldCheckForUpdate(): Boolean {
        if (FXGL.getSettings().applicationMode === ApplicationMode.RELEASE)
            return false

        updateCheckTimer = FXGL.newOfflineTimer("version.check")

        val days = Duration.hours(24.0 * FXGL.getInt("version.check.days"))

        return FXGL.isFirstRun() || updateCheckTimer.elapsed(days)
    }

    /**
     * Shows a blocking JavaFX dialog, while it runs an async task
     * to connect to FXGL repo and find latest version string.
     */
    private fun checkForUpdates() {
        log.debug("Checking for updates")

        val dialog = Dialog<ButtonType>()
        with(dialog) {
            title = "FXGL Update"
            dialogPane.contentText = "Checking for updates...\n \n "
            dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        }

        // Open GitHub button
        val button = dialog.dialogPane.lookupButton(ButtonType.OK) as Button
        with(button) {
            isDisable = true
            text = "Open GitHub"
            setOnAction { e ->
                FXGL.getNet()
                        .openBrowserTask(FXGL.getString("url.github"))
                        .onFailure(Consumer { error -> log.warning("Error opening browser: $error") })
                        .execute()
            }
        }

        FXGL.getNet()
                .getLatestVersionTask()
                .onSuccess(Consumer { version ->

                    // update offline timer
                    updateCheckTimer.capture()

                    dialog.dialogPane.contentText = "Just so you know\n" +
                            "Your version:   " + Version.getAsString() + "\n" +
                            "Latest version: " + version

                    button.isDisable = false
                })
                .onFailure(Consumer { error ->

                    // not important, just log it
                    log.warning("Failed to find updates: $error")

                    dialog.dialogPane.contentText = "Failed to find updates: " + error

                    button.isDisable = false
                }).executeAsyncWithDialogFX(FXGL.getExecutor())

        // blocking call
        dialog.showAndWait()
    }
}