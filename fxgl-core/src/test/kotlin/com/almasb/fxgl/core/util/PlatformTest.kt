/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PlatformTest {

    @EnabledOnOs(OS.MAC)
    @Test
    fun isMac() {
        assertThat(Platform.get(), `is`(Platform.MAC))
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    fun isWindows() {
        assertThat(Platform.get(), `is`(Platform.WINDOWS))
    }

    @EnabledOnOs(OS.LINUX)
    @Test
    fun isLinux() {
        assertThat(Platform.get(), `is`(Platform.LINUX))
    }
}