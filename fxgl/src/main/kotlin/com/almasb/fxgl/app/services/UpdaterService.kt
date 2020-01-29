/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.time.LocalTimer
import com.almasb.sslogger.Logger

/**
 * Handles everything related to FXGL update.
 * First checks if the update is necessary.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class UpdaterService : EngineService() {

    private val log = Logger.get(javaClass)

    private lateinit var updateCheckTimer: LocalTimer

    @Inject("urlPOM")
    private lateinit var urlPOM: String

    override fun onInit() {
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
        return true
        // TODO:
//        if (FXGL.getSettings().applicationMode === ApplicationMode.RELEASE)
//            return false
//
//        updateCheckTimer = FXGL.newOfflineTimer("version.check")
//
//        val days = Duration.hours(24.0 * FXGL.getSettings().versionCheckDays)
//
//        return updateCheckTimer.elapsed(days)
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
                    //updateCheckTimer.capture()

                    if (currentVersion == latestVersion) {
                        log.info("You are using latest FXGL version!")
                    } else {
                        log.info("Your current version:  $currentVersion")
                        log.info("Latest stable version: $latestVersion")
                    }
                }
                .onFailure { error ->
                    // not important, just log it
                    log.warning("Failed to find updates: $error")
                }
                .runAsync()
    }
    /**
     * Loads pom.xml from GitHub server's master branch and parses the "version" tag.
     *
     * @return task that returns latest stable FXGL version
     */
    fun getLatestVersionTask(): IOTask<String> = FXGL.getNetService()
            .openStreamTask(urlPOM)
            .thenWrap {
                it.reader().useLines { lines ->
                    lines.first { "<version>" in it}
                            .trim()
                            .removeSurrounding("<version>", "</version>")
                }
            }
}