/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.ui

import com.almasb.fxgl.test.RunWithFX
import javafx.scene.text.Font
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class FontFactoryTest {

    @Test
    fun `Create font using factory`() {
        val fontFactory = FontFactory(Font.font(12.0))

        val font = fontFactory.newFont(14.0)

        assertThat(font.size, `is`(14.0))
    }
}