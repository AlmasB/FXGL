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
    WINDOWS, MAC, LINUX, ANDROID, IOS, BROWSER;

    val isBrowser: Boolean
        get() = this === BROWSER

    val isMobile: Boolean
        get() = this === ANDROID || this === IOS

    val isDesktop: Boolean
        get() = this === WINDOWS || this === MAC || this === LINUX

    companion object {
        @JvmStatic fun get(): Platform {
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

            if (osName.contains("iOS", ignoreCase = true)) {
                return IOS
            }

            if (osName.contains("android", ignoreCase = true)) {
                return ANDROID
            }

            return BROWSER
        }
    }
}