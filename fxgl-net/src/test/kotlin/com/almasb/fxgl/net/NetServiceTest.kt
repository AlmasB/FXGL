/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.net

import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class NetServiceTest {

    private lateinit var net: NetService

    @BeforeEach
    fun setUp() {
        net = NetService()
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    fun `Open stream to URL`() {
        val numLines = net.openStreamTask("https://raw.githubusercontent.com/AlmasB/FXGL/master/README.md")
                .run()
                .bufferedReader()
                .lines()
                .count()

        assertThat(numLines, greaterThan(0L))
    }
}