/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.settings

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import javafx.scene.input.KeyCode
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameSettingsTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
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
//        settings.setApplicationMode(ApplicationMode.RELEASE)

        assertThat(settings.width, `is`(500))
        assertThat(settings.height, `is`(500))
        assertThat(settings.title, `is`("Test"))
        assertThat(settings.version, `is`("0.99"))
        assertThat(settings.introEnabled, `is`(false))
        assertThat(settings.menuEnabled, `is`(false))
        assertThat(settings.fullScreenAllowed, `is`(false))
        assertThat(settings.profilingEnabled, `is`(false))
        assertThat(settings.closeConfirmation, `is`(false))
        assertThat(settings.menuKey, `is`(KeyCode.ENTER))
        assertThat(settings.credits.list, hasItems("TestCredit1", "TestCredit2"))
        assertThat(settings.appMode, `is`(ApplicationMode.RELEASE))
    }
}