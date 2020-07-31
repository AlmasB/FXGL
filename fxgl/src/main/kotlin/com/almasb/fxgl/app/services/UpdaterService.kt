/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.RuntimeInfo
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.net.NetService

/**
 * Checks if there is a newer version of FXGL than the one being used.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class UpdaterService : EngineService() {

    private val log = Logger.get(javaClass)

    private lateinit var netService: NetService

    private lateinit var taskService: IOTaskExecutorService

    @Inject("applicationMode")
    private lateinit var appMode: ApplicationMode

    @Inject("runtimeInfo")
    private lateinit var runtimeInfo: RuntimeInfo

    @Inject("urlPOM")
    private lateinit var urlPOM: String

    override fun onInit() {
        if (needCheckForUpdate()) {
            checkForUpdates()
        }
    }

    internal fun needCheckForUpdate(): Boolean {
        return appMode != ApplicationMode.RELEASE && !runtimeInfo.version.contains("project.version")
    }

    /**
     * Connect to FXGL repo and find latest version string.
     */
    private fun checkForUpdates() {
        log.debug("Checking for updates")

        val task = getLatestVersionTask()
                .onSuccess { latestVersion ->
                    if (runtimeInfo.version != latestVersion) {
                        log.info("Your current version:  ${runtimeInfo.version}")
                        log.info("Latest stable version: $latestVersion")
                    }
                }
                .onFailure { error ->
                    // not important, just log it
                    log.warning("Failed to find updates: $error")
                }

        taskService.runAsync(task)
    }
    /**
     * Loads pom.xml from GitHub server's master branch and parses the "version" tag.
     *
     * @return task that returns latest stable FXGL version
     */
    internal fun getLatestVersionTask(): IOTask<String> = netService
            .openStreamTask(urlPOM)
            .thenWrap {
                it.reader().useLines { lines ->
                    lines.first { "<version>" in it }
                            .trim()
                            .removeSurrounding("<version>", "</version>")
                }
            }
}