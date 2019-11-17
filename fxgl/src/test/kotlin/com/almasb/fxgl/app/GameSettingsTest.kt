/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.util.Platform
import com.almasb.fxgl.test.RunWithFX
import javafx.scene.input.KeyCode
import javafx.stage.Stage
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
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

            val stage = Async.startAsyncFX(Callable { Stage() }).await()

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

        // this only reads the default settings, so regardless of platform where test is running, the following should be Windows
        // the actual platform tests are in PlatformTest.kt
        assertThat(settings.runtimeInfo.platform, `is`(Platform.WINDOWS))
        assertTrue(settings.isWindows)
        assertFalse(settings.isLinux)
        assertFalse(settings.isMac)
    }

    @Test
    fun `Test setting of height and width from ratio`() {
        val settingsInitial = GameSettings()

        assertThat(settingsInitial.width, `is`(800))
        assertThat(settingsInitial.height, `is`(600))

        settingsInitial.width = 100;
        settingsInitial.setHeightFromRatio(0.5);
        assertThat(settingsInitial.height,  `is`(200))

        settingsInitial.height = 400;
        settingsInitial.setWidthFromRatio(1.3);
        assertThat(settingsInitial.width,  `is`(520))
    }
}
