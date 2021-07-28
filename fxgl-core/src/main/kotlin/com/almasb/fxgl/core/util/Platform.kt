/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
enum class Platform {
    WINDOWS, MAC, LINUX, ANDROID, IOS, BROWSER, EMBEDDED;

    val isBrowser: Boolean
        get() = this === BROWSER

    val isMobile: Boolean
        get() = this === ANDROID || this === IOS

    val isDesktop: Boolean
        get() = this === WINDOWS || this === MAC || this === LINUX

    val isEmbedded: Boolean
        get() = this === EMBEDDED

    companion object {
        @JvmStatic fun get(): Platform {
            // check if running on mobile first
            val fxPlatformName = System.getProperty("javafx.platform", "")

            if (fxPlatformName.contains("ios", ignoreCase = true)) {
                return IOS
            }

            if (fxPlatformName.contains("android", ignoreCase = true)) {
                return ANDROID
            }

            // check if running on embedded
            val monoclePlatformName = System.getProperty("monocle.platform", "")
            val glassPlatformName = System.getProperty("glass.platform", "")

            if (monoclePlatformName == "EGL" && glassPlatformName == "Monocle") {
                return EMBEDDED
            }

            // if we got here then not running on mobile or embedded
            val osName = System.getProperty("os.name")

            if (osName.contains("mac", ignoreCase = true)) {
                return MAC
            }

            if (osName.contains("nux", ignoreCase = true)) {
                return LINUX
            }

            if (osName.contains("win", ignoreCase = true)) {
                return WINDOWS
            }

            return BROWSER
        }
    }
}