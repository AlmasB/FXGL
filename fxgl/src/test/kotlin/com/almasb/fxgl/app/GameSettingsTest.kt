/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.app.GameApplication.FXGLApplication.app
import com.almasb.fxgl.app.MockApplication.Companion.stage
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.util.Platform
import com.almasb.fxgl.test.RunWithFX
import javafx.application.Application
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledOnOs
import org.junit.jupiter.api.condition.OS
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import java.util.concurrent.Callable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@ExtendWith(RunWithFX::class)
class GameSettingsTest {

    class MockGameApplication : GameApplication() {

        override public fun initSettings(settings: GameSettings) {
            settings.width = 500
            settings.height = 500
            settings.title = "Test"
            settings.version = "0.99"
            settings.isIntroEnabled = false
            settings.isMenuEnabled = false
            settings.isFullScreenAllowed = false
            settings.isProfilingEnabled = false
            settings.isCloseConfirmation = false
            settings.menuKey = KeyCode.ENTER
            settings.credits = Arrays.asList("TestCredit1", "TestCredit2")
            settings.applicationMode = ApplicationMode.RELEASE

            // mock
            settings.uiFactory = MockUIFactory
        }
    }

    companion object {
        lateinit var settings: ReadOnlyGameSettings

        @BeforeAll
        @JvmStatic fun before() {
            val app = MockGameApplication()
            val settingsInitial = GameSettings()
            app.initSettings(settingsInitial)

            val stage = Async.startFX(Callable { Stage() }).await()

            val engine = Engine(app, settingsInitial.toReadOnly(), stage)

            settings = engine.settings
        }
    }

    /**
     * This is linked to [MockGameApplication] and its
     * initSettings().
     */
    @Test
    fun `Test settings data`() {
        assertThat(settings.runtimeInfo.version, `is`("11.x"))
        assertThat(settings.runtimeInfo.build, `is`("?"))
        assertThat(settings.width, `is`(500))
        assertThat(settings.height, `is`(500))
        assertThat(settings.title, `is`("Test"))
        assertThat(settings.version, `is`("0.99"))
        assertThat(settings.isIntroEnabled, `is`(false))
        assertThat(settings.isMenuEnabled, `is`(false))
        assertThat(settings.isFullScreenAllowed, `is`(false))
        assertThat(settings.isProfilingEnabled, `is`(false))
        assertThat(settings.isCloseConfirmation, `is`(false))
        assertThat(settings.menuKey, `is`(KeyCode.ENTER))
        assertThat(settings.credits, hasItems("TestCredit1", "TestCredit2"))
        assertThat(settings.applicationMode, `is`(ApplicationMode.RELEASE))

        assertTrue(settings.isDesktop)
        assertFalse(settings.isBrowser)
        assertFalse(settings.isMobile)
        assertFalse(settings.isIOS)
        assertFalse(settings.isAndroid)
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    fun `on windows`() {
        assertThat(settings.runtimeInfo.platform, `is`(Platform.WINDOWS))
        assertTrue(settings.isWindows)
        assertFalse(settings.isLinux)
        assertFalse(settings.isMac)
    }

    @EnabledOnOs(OS.LINUX)
    @Test
    fun `on linux`() {
        assertThat(settings.runtimeInfo.platform, `is`(Platform.LINUX))
        assertTrue(settings.isLinux)
        assertFalse(settings.isWindows)
        assertFalse(settings.isMac)
    }

    @EnabledOnOs(OS.MAC)
    @Test
    fun `on mac`() {
        assertThat(settings.runtimeInfo.platform, `is`(Platform.MAC))
        assertTrue(settings.isMac)
        assertFalse(settings.isLinux)
        assertFalse(settings.isWindows)
    }
}