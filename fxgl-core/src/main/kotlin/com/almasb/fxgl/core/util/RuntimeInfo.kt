/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.util

/**
 * Stores FXGL runtime information.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
data class RuntimeInfo(
        val platform: Platform,
        val version: String,
        val build: String
)