/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.settings

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import com.almasb.fxgl.app.MockService
import com.almasb.fxgl.scene.menu.MenuStyle
import javafx.scene.input.KeyCode
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameSettingsTest {

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    @Test
    fun `Settings are unmodifiable`() {
        val settings = FXGL.getSettings()
        assertFalse(settings is GameSettings)
    }

    /**
     * This is linked to [com.almasb.fxgl.app.MockGameApplication] and its
     * initSettings().
     */
    @Test
    fun `Test settings data`() {
        val settings = FXGL.getSettings()

//        settings.setWidth(500)
//        settings.setHeight(500)
//        settings.setTitle("Test")
//        settings.setVersion("0.99")
//        settings.setIntroEnabled(false)
//        settings.setMenuEnabled(false)
//        settings.setFullScreen(false)
//        settings.setProfilingEnabled(false)
//        settings.setCloseConfirmation(false)
//        settings.setMenuKey(KeyCode.ENTER)
//        settings.setMenuStyle(MenuStyle.CCTR)
//        settings.setCredits(Credits(Arrays.asList("TestCredit1", "TestCredit2")))
//        settings.setApplicationMode(ApplicationMode.DEBUG)

        assertThat(settings.width, `is`(500))
        assertThat(settings.height, `is`(500))
        assertThat(settings.title, `is`("Test"))
        assertThat(settings.version, `is`("0.99"))
        assertThat(settings.introEnabled, `is`(false))
        assertThat(settings.menuEnabled, `is`(false))
        assertThat(settings.fullScreen, `is`(false))
        assertThat(settings.profilingEnabled, `is`(false))
        assertThat(settings.closeConfirmation, `is`(false))
        assertThat(settings.menuKey, `is`(KeyCode.ENTER))
        assertThat(settings.menuStyle, `is`(MenuStyle.CCTR))
        assertThat(settings.credits.list, hasItems("TestCredit1", "TestCredit2"))
        assertThat(settings.appMode, `is`(ApplicationMode.DEBUG))
    }

//    @Test
//    fun `Test custom services`() {
//        val value = FXGL.getInstance(MockService::class.java).test()
//        assertThat(value, `is`("Test"))
//    }
}