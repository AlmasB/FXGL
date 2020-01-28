/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import com.almasb.fxgl.test.InjectInTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.lang.invoke.MethodHandles

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NetServiceTest {

    private lateinit var net: NetService

    @BeforeEach
    fun setUp() {
        net = NetService()

        val lookup = MethodHandles.lookup()

        InjectInTest.inject(lookup, net, "urlPOM", "https://raw.githubusercontent.com/AlmasB/FXGL/master/pom.xml")
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Get latest FXGL version`() {
        var result = ""

        net.getLatestVersionTask()
                .onSuccess { result = it }
                .run()

        assertTrue(result.startsWith("11."))
    }
}