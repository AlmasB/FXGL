/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.time.LocalTimer
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

        val days = Duration.hours(24.0 * FXGL.getSettings().versionCheckDays)

        return updateCheckTimer.elapsed(days)
    }

    /**
     * Connect to FXGL repo and find latest version string.
     */
    private fun checkForUpdates() {
        log.debug("Checking for updates")

        getLatestVersionTask()
                .onSuccess { latestVersion ->

                    val currentVersion = FXGL.getVersion()

                    // update offline timer
                    updateCheckTimer.capture()

                    if (currentVersion == latestVersion) {

                    } else {
                        log.info("Your current version:  $currentVersion")
                        log.info("Latest stable version: $latestVersion")
                    }
                }
                .onFailure { error ->
                    // not important, just log it
                    log.warning("Failed to find updates: $error")
                }
                .run()
    }

    private fun getLatestVersionTask() = FXGL.getNet()
            .openStreamTask(FXGL.getSettings().urlPOM)
            .thenWrap {
                it.reader().useLines {
                    it.first { "<version>" in it}
                            .trim()
                            .removeSurrounding("<version>", "</version>")
                }
            }
}