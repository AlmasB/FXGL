/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")

package com.almasb.fxgl.app.services

import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SystemBundleServiceTest {

    @Test
    fun `Exit before init does not throw or touch the file system`() {
        val service = SystemBundleService()

        service.onExit()
    }
}
