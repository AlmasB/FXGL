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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

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

    @ParameterizedTest
    @CsvSource(
            //             desktop, mobile, browser
            "Linux, LINUX, true, false, false",
            "Windows, WINDOWS, true, false, false",
            "Mac OS, MAC, true, false, false",
            "iOS, IOS, false, true, false",
            "android, ANDROID, false, true, false",
            "browser, BROWSER, false, false, true"
    )
    fun `get reports current platform based on os name`(osName: String,
                                                        platformName: String,
                                                        isDesktop: Boolean,
                                                        isMobile: Boolean,
                                                        isBrowser: Boolean) {
        val actualOSName = System.getProperty("os.name")

        System.setProperty("os.name", osName)

        val platform = Platform.get()

        assertThat(platform, `is`(Platform.valueOf(platformName)))
        assertThat(platform.isDesktop, `is`(isDesktop))
        assertThat(platform.isMobile, `is`(isMobile))
        assertThat(platform.isBrowser, `is`(isBrowser))

        System.setProperty("os.name", actualOSName)
    }
}