/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.app.SystemPropertyKey.*
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.time.LocalTimer
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.util.Duration

/**
 * Handles everything related to FXGL update.
 * First checks if the update is necessary.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class UpdaterTask : Runnable {

    private val log = Logger.get(javaClass)
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
     * Returns true if first start or required number of days have passed.
     *
     * @return whether we need check for updates
     */
    private fun shouldCheckForUpdate(): Boolean {
        if (FXGL.getSettings().applicationMode === ApplicationMode.RELEASE)
            return false

        updateCheckTimer = FXGL.newOfflineTimer("version.check")

        val days = Duration.hours(24.0 * FXGL.getProperties().getInt("version.check.days"))

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
            setOnAction {
                FXGL.getNet()
                        .openBrowserTask(FXGL.getProperties().getString("url.github"))
                        .onFailure { log.warning("Error opening browser: $it") }
                        .run()
            }
        }

        FXGL.getNet()
                .getLatestVersionTask()
                .onSuccess { latestVersion ->

                    val currentVersion = FXGL.getProperties().getString(FXGL_VERSION)

                    // update offline timer
                    updateCheckTimer.capture()

                    if (currentVersion == latestVersion) {
                        dialog.close()
                    } else {
                        dialog.dialogPane.contentText = "Just so you know\n" +
                                "Your version:   $currentVersion\n" +
                                "Latest version: $latestVersion"

                        button.isDisable = false
                    }
                }
                .onFailure { error ->

                    // not important, just log it
                    log.warning("Failed to find updates: $error")

                    dialog.dialogPane.contentText = "Failed to find updates: " + error

                    button.isDisable = false
                }
                .runAsyncFX()

        // blocking call
        dialog.showAndWait()
    }
}