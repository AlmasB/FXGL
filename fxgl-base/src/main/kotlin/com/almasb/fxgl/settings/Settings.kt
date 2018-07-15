/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.settings

import com.almasb.fxgl.app.ApplicationMode
import com.almasb.fxgl.app.ExceptionHandler
import com.almasb.fxgl.app.FXGLExceptionHandler
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.gameplay.achievement.AchievementStore
import com.almasb.fxgl.gameplay.notification.NotificationView
import com.almasb.fxgl.gameplay.notification.XboxNotificationView
import com.almasb.fxgl.scene.SceneFactory
import com.almasb.fxgl.ui.DialogFactory
import com.almasb.fxgl.ui.FXGLDialogFactory
import com.almasb.fxgl.ui.FXGLUIFactory
import com.almasb.fxgl.ui.UIFactory
import com.almasb.fxgl.util.Credits
import com.almasb.fxgl.util.Optional
import javafx.scene.input.KeyCode
import javafx.stage.StageStyle
import java.util.*

/**
 * Data structure for variables that are initialised before the application (game) starts.
 *
 * Modifying any data after the start of the game has no effect.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameSettings(

        /**
         * Set title of the game. This will be shown as the
         * window header if the game isn't fullscreen.
         */
        var title: String = "Untitled",

        var version: String = "0.0",

        /**
         * Set target width. If the screen width is smaller,
         * the game will automatically scale down the image
         * while maintaining the aspect ratio.
         *
         * All the game logic must use target width and height.
         */
        var width: Int = 800,

        /**
         * Set target height. If the screen height is smaller,
         * the game will automatically scale down the image
         * while maintaining the aspect ratio.
         *
         * All the game logic must use target width and height.
         */
        var height: Int = 600,

        /**
         * Setting to true will allow the game to be able to enter full screen
         * from the menu.
         */
        var isFullScreenAllowed: Boolean = false,

        /**
         * If enabled, users can drag the corner of the main window
         * to resize it and the game.
         */
        var isManualResizeEnabled: Boolean = false,

        /**
         * If set to true, the intro video/animation will
         * be played before the start of the game.
         */
        var isIntroEnabled: Boolean = false,

        /**
         * Setting to true enables main and game menu.
         */
        var isMenuEnabled: Boolean = false,

        /**
         * Setting to true will enable profiler that reports on performance
         * when FXGL exits.
         * Also shows render and performance FPS in the bottom left corner
         * when the application is run.
         */
        var isProfilingEnabled: Boolean = false,

        /**
         * Setting to false will disable asking for confirmation on exit.
         * This is useful for faster compile -> run -> exit.
         */
        var isCloseConfirmation: Boolean = false,

        var isSingleStep: Boolean = false,

        /**
         * Sets application run mode. See [ApplicationMode] for more info.
         */
        var applicationMode: ApplicationMode = ApplicationMode.DEVELOPER,

        /**
         * Set the key that will trigger in-game menu.
         */
        var menuKey: KeyCode = KeyCode.ESCAPE,

        /**
         * Set additional credits.
         */
        var credits: Credits = Credits(emptyList()),
        var enabledMenuItems: EnumSet<MenuItem> = EnumSet.noneOf(MenuItem::class.java),
        var stageStyle: StageStyle = StageStyle.DECORATED,
        var appIcon: String = "fxgl_icon.png",

        @get:JvmName("getCSS")
        @set:JvmName("setCSS")
        var css: String = "fxgl_dark.css",

        /**
         * Set font to be used in UI controls.
         * The font will be loaded from "/assets/ui/fonts".
         */
        var fontUI: String = "VarelaRound-Regular.ttf",
        var fontMono: String = "lucida_console.ttf",
        var fontText: String = "Courier-Prime.ttf",
        var fontGame: String = "Abel-Regular.ttf",

        var soundNotification: String = "core/notification.wav",
        var soundMenuBack: String = "menu/back.wav",
        var soundMenuPress: String = "menu/press.wav",
        var soundMenuSelect: String = "menu/select.wav",

        var pixelsPerMeter: Double = 50.0,

        /**
         * Set how many real seconds are in 24 game hours, default = 60.
         */
        var secondsIn24h: Int = 60,

        /* EXPERIMENTAL */

        var isExperimentalTiledLargeMap: Boolean = false,

        /* CONFIGS */

        var configClass: Class<*>? = null,
        var achievementStoreClass: Class<out AchievementStore>? = null,

        /* CUSTOMIZABLE SERVICES BELOW */

        /**
         * Provide a custom scene factory.
         */
        var sceneFactory: SceneFactory = SceneFactory(),

        /**
         * Provide a custom dialog factory.
         */
        var dialogFactory: DialogFactory = FXGLDialogFactory(),

        /**
         * Provide a custom UI factory.
         */
        @get:JvmName("getUIFactory")
        @set:JvmName("setUIFactory")
        var uiFactory: UIFactory = FXGLUIFactory(),

        /**
         * Provide a custom notification service.
         */
        var notificationViewFactory: Class<out NotificationView> = XboxNotificationView::class.java,

        /**
         * Provide a custom exception handler.
         */
        var exceptionHandler: ExceptionHandler = FXGLExceptionHandler()
) {

    fun toReadOnly(): ReadOnlyGameSettings {
        return ReadOnlyGameSettings(
                title,
                version,
                width,
                height, isFullScreenAllowed, isManualResizeEnabled, isIntroEnabled, isMenuEnabled, isProfilingEnabled, isCloseConfirmation, isSingleStep, applicationMode, menuKey, credits, enabledMenuItems, stageStyle, appIcon, css, fontUI, fontMono, fontText, fontGame, soundNotification, soundMenuBack, soundMenuPress, soundMenuSelect, pixelsPerMeter, secondsIn24h, isExperimentalTiledLargeMap)
    }
}


/**
 * A copy of GameSettings with public getters only.
 */
class ReadOnlyGameSettings(
        /**
         * Set title of the game. This will be shown as the
         * window header if the game isn't fullscreen.
         */
        val title: String = "Untitled",

        val version: String = "0.0",

        /**
         * Set target width. If the screen width is smaller,
         * the game will automatically scale down the image
         * while maintaining the aspect ratio.
         *
         * All the game logic must use target width and height.
         */
        val width: Int = 800,

        /**
         * Set target height. If the screen height is smaller,
         * the game will automatically scale down the image
         * while maintaining the aspect ratio.
         *
         * All the game logic must use target width and height.
         */
        val height: Int = 600,

        /**
         * Setting to true will allow the game to be able to enter full screen
         * from the menu.
         */
        val isFullScreenAllowed: Boolean = false,

        /**
         * If enabled, users can drag the corner of the main window
         * to resize it and the game.
         */
        val isManualResizeEnabled: Boolean = false,

        /**
         * If set to true, the intro video/animation will
         * be played before the start of the game.
         */
        val isIntroEnabled: Boolean = false,

        /**
         * Setting to true enables main and game menu.
         */
        val isMenuEnabled: Boolean = false,

        /**
         * Setting to true will enable profiler that reports on performance
         * when FXGL exits.
         * Also shows render and performance FPS in the bottom left corner
         * when the application is run.
         */
        val isProfilingEnabled: Boolean = false,

        /**
         * Setting to false will disable asking for confirmation on exit.
         * This is useful for faster compile -> run -> exit.
         */
        val isCloseConfirmation: Boolean = false,

        val isSingleStep: Boolean = false,

        /**
         * Sets application run mode. See [ApplicationMode] for more info.
         */
        val applicationMode: ApplicationMode = ApplicationMode.DEVELOPER,

        /**
         * Set the key that will trigger in-game menu.
         */
        val menuKey: KeyCode = KeyCode.ESCAPE,

        /**
         * Set additional credits.
         */
        val credits: Credits = Credits(emptyList()),
        val enabledMenuItems: EnumSet<MenuItem> = EnumSet.noneOf(MenuItem::class.java),
        val stageStyle: StageStyle = StageStyle.DECORATED,
        val appIcon: String = "fxgl_icon.png",

        @get:JvmName("getCSS")
        val css: String = "fxgl_dark.css",

        /**
         * Set font to be used in UI controls.
         * The font will be loaded from "/assets/ui/fonts".
         */
        val fontUI: String = "varelaRound-Regular.ttf",
        val fontMono: String = "lucida_console.ttf",
        val fontText: String = "Courier-Prime.ttf",
        val fontGame: String = "Abel-Regular.ttf",

        val soundNotification: String = "core/notification.wav",
        val soundMenuBack: String = "menu/back.wav",
        val soundMenuPress: String = "menu/press.wav",
        val soundMenuSelect: String = "menu/select.wav",

        val pixelsPerMeter: Double = 50.0,

        /**
         * Set how many real seconds are in 24 game hours, default = 60.
         */
        val secondsIn24h: Int = 60,

        /* EXPERIMENTAL */

        val isExperimentalTiledLargeMap: Boolean = false,

        /* CONFIGS */

        private val configClassInternal: Class<*>? = null,
        private val achievementStoreClassInternal: Class<out AchievementStore>? = null,

        /* CUSTOMIZABLE SERVICES BELOW */

        /**
         * Provide a custom scene factory.
         */
        val sceneFactory: SceneFactory = SceneFactory(),

        /**
         * Provide a custom dialog factory.
         */
        val dialogFactory: DialogFactory = FXGLDialogFactory(),

        /**
         * Provide a custom UI factory.
         */
        @get:JvmName("getUIFactory")
        val uiFactory: UIFactory = FXGLUIFactory(),

        /**
         * Provide a custom notification service.
         */
        val notificationViewFactory: Class<out NotificationView> = XboxNotificationView::class.java,

        /**
         * Provide a custom exception handler.
         */
        private val exceptionHandlerInternal: ExceptionHandler = FXGLExceptionHandler()
) {

    private val exceptionHandlerWrapper: ExceptionHandler = object : ExceptionHandler {
        private val log = Logger.get("ExceptionHandler")

        override fun accept(e: Throwable) {
            log.warning("Caught Exception: ", e)
            exceptionHandlerInternal.accept(e)
        }
    }

    val exceptionHandler = exceptionHandlerWrapper

    val configClass: Optional<Class<*>>
        get() = Optional.ofNullable(configClassInternal)

    val achievementStoreClass: Optional<Class<out AchievementStore>>
        get() = Optional.ofNullable(achievementStoreClassInternal)

    override fun toString(): String {
        return "Title: " + title + '\n'.toString() +
                "Version: " + version + '\n'.toString() +
                "Width: " + width + '\n'.toString() +
                "Height: " + height + '\n'.toString() +
                "Fullscreen: " + isFullScreenAllowed + '\n'.toString() +
                "Intro: " + isIntroEnabled + '\n'.toString() +
                "Menus: " + isMenuEnabled + '\n'.toString() +
                "Profiling: " + isProfilingEnabled + '\n'.toString() +
                "Single step:" + isSingleStep + '\n'.toString() +
                "App Mode: " + applicationMode + '\n'.toString() +
                "Menu Key: " + menuKey + '\n'.toString() +
                "Stage Style: " + stageStyle + '\n'.toString() +
                "Scene Factory: " + sceneFactory.javaClass + '\n'.toString() +
                "Dialog Factory: " + dialogFactory.javaClass + '\n'.toString() +
                "UI Factory: " + uiFactory.javaClass + '\n'.toString() +
                "Notification Service: " + notificationViewFactory + '\n'.toString() +
                "Exception Handler: " + exceptionHandlerInternal.javaClass
    }
}