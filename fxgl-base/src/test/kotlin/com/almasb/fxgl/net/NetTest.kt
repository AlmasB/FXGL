/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.app.FXGL
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NetTest {

    private lateinit var net: Net

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGL.getProperties().setValue("url.pom", "https://raw.githubusercontent.com/AlmasB/FXGL/master/pom.xml")
        }
    }

    @BeforeEach
    fun setUp() {
        net = FXGLNet()
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Get latest FXGL version`() {
        var result = ""

        net.latestVersionTask
                .onSuccess { result = it }
                .run()

        assertTrue(result.matches("[0-9].[0-9].[0-9]".toRegex()))
    }
}