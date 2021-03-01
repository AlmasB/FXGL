/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.services

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.services.UpdaterService
import com.almasb.fxgl.net.NetService
import com.almasb.fxgl.test.InjectInTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.lang.invoke.MethodHandles

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UpdaterServiceTest {

    private lateinit var updater: UpdaterService

    @BeforeEach
    fun setUp() {
        updater = UpdaterService()

        val lookup = MethodHandles.lookup()
        val injectMap = mapOf(
                "appMode" to ApplicationMode.RELEASE,
                "urlPOM" to "https://raw.githubusercontent.com/AlmasB/FXGL/release/README.md",
                "netService" to NetService())

        InjectInTest.inject(lookup, updater, injectMap)
    }

    @Test
    fun `Do not check for updates if in release mode`() {
        assertFalse(updater.needCheckForUpdate())

        updater.onInit()
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Get latest FXGL version`() {
        var result = ""

        updater.getLatestVersionTask()
                .onSuccess { result = it }
                .run()

        assertTrue(result.isNotEmpty())
    }
}