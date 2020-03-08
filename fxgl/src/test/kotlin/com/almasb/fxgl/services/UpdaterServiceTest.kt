/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.services

import com.almasb.fxgl.app.services.UpdaterService
import com.almasb.fxgl.net.NetService
import com.almasb.fxgl.test.InjectInTest
import org.junit.jupiter.api.Assertions
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

        InjectInTest.inject(lookup, updater, "urlPOM", "https://raw.githubusercontent.com/AlmasB/FXGL/master/pom.xml")
        InjectInTest.inject(lookup, updater, "netService", NetService())
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Get latest FXGL version`() {
        var result = ""

        updater.getLatestVersionTask()
                .onSuccess { result = it }
                .run()

        Assertions.assertTrue(result.startsWith("11."))
    }
}