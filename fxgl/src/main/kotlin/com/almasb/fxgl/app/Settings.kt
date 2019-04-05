/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.local.Language
import com.almasb.fxgl.core.util.Optional
import com.almasb.fxgl.notification.view.NotificationView
import com.almasb.fxgl.notification.view.XboxNotificationView
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable
import com.almasb.fxgl.ui.DialogFactory
import com.almasb.fxgl.ui.FXGLDialogFactory
import com.almasb.fxgl.ui.FXGLUIFactory
import com.almasb.fxgl.ui.UIFactory
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import java.util.*

enum class MenuItem {

    /**
     * Enables CONTINUE, SAVE, LOAD.
     */
    SAVE_LOAD,

    /**
     * Enables EXTRA -> CREDITS, TROPHIES
     */
    EXTRA,

    /**
     * Enables ONLINE (multiplayer).
     */
    ONLINE
}

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
        var credits: List<String> = emptyList(),
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
        //var achievementStoreClass: Class<out AchievementStore>? = null,

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

        var notificationViewClass: Class<out NotificationView> = XboxNotificationView::class.java
) {

    fun toReadOnly(): ReadOnlyGameSettings {
        return ReadOnlyGameSettings(
                title,
                version,
                width,
                height,
                isFullScreenAllowed,
                isManualResizeEnabled,
                isIntroEnabled,
                isMenuEnabled,
                isProfilingEnabled,
                isCloseConfirmation,
                isSingleStep,
                applicationMode,
                menuKey,
                Collections.unmodifiableList(credits),
                enabledMenuItems,
                stageStyle,
                appIcon,
                css,
                fontUI,
                fontMono,
                fontText,
                fontGame,
                soundNotification,
                soundMenuBack,
                soundMenuPress,
                soundMenuSelect,
                pixelsPerMeter,
                secondsIn24h,
                isExperimentalTiledLargeMap,
                configClass,
                //achievementStoreClass,
                sceneFactory,
                dialogFactory,
                uiFactory,
                notificationViewClass)
    }
}


/**
 * A copy of GameSettings with public getters only.
 */
class ReadOnlyGameSettings internal constructor(
        /**
         * Set title of the game. This will be shown as the
         * window header if the game isn't fullscreen.
         */
        val title: String,

        val version: String,

        /**
         * Set target width. If the screen width is smaller,
         * the game will automatically scale down the image
         * while maintaining the aspect ratio.
         *
         * All the game logic must use target width and height.
         */
        val width: Int,

        /**
         * Set target height. If the screen height is smaller,
         * the game will automatically scale down the image
         * while maintaining the aspect ratio.
         *
         * All the game logic must use target width and height.
         */
        val height: Int,

        /**
         * Setting to true will allow the game to be able to enter full screen
         * from the menu.
         */
        val isFullScreenAllowed: Boolean,

        /**
         * If enabled, users can drag the corner of the main window
         * to resize it and the game.
         */
        val isManualResizeEnabled: Boolean,

        /**
         * If set to true, the intro video/animation will
         * be played before the start of the game.
         */
        val isIntroEnabled: Boolean,

        /**
         * Setting to true enables main and game menu.
         */
        val isMenuEnabled: Boolean,

        /**
         * Setting to true will enable profiler that reports on performance
         * when FXGL exits.
         * Also shows render and performance FPS in the bottom left corner
         * when the application is run.
         */
        val isProfilingEnabled: Boolean,

        /**
         * Setting to false will disable asking for confirmation on exit.
         * This is useful for faster compile -> run -> exit.
         */
        val isCloseConfirmation: Boolean,

        val isSingleStep: Boolean,

        /**
         * Sets application run mode. See [ApplicationMode] for more info.
         */
        val applicationMode: ApplicationMode,

        /**
         * Set the key that will trigger in-game menu.
         */
        val menuKey: KeyCode,

        /**
         * Set additional credits.
         */
        val credits: List<String>,
        val enabledMenuItems: EnumSet<MenuItem>,
        val stageStyle: StageStyle,
        val appIcon: String,

        @get:JvmName("getCSS")
        val css: String,

        /**
         * Set font to be used in UI controls.
         * The font will be loaded from "/assets/ui/fonts".
         */
        val fontUI: String,
        val fontMono: String,
        val fontText: String,
        val fontGame: String,

        val soundNotification: String,
        val soundMenuBack: String,
        val soundMenuPress: String,
        val soundMenuSelect: String,

        val pixelsPerMeter: Double,

        /**
         * Set how many real seconds are in 24 game hours, default = 60.
         */
        val secondsIn24h: Int,

        /* EXPERIMENTAL */

        val isExperimentalTiledLargeMap: Boolean,

        /* CONFIGS */

        private val configClassInternal: Class<*>?,
        //private val achievementStoreClassInternal: Class<out AchievementStore>?,

        /* CUSTOMIZABLE SERVICES BELOW */

        /**
         * Provide a custom scene factory.
         */
        val sceneFactory: SceneFactory,

        /**
         * Provide a custom dialog factory.
         */
        val dialogFactory: DialogFactory,

        /**
         * Provide a custom UI factory.
         */
        @get:JvmName("getUIFactory")
        val uiFactory: UIFactory,

        val notificationViewClass: Class<out NotificationView>
) : UserProfileSavable {

    /* STATIC - cannot be modified at runtime */

    /**
     * where to look for latest stable project POM
     */
    val urlPOM = "https://raw.githubusercontent.com/AlmasB/FXGL/master/pom.xml"

    /**
     * project GitHub repo
     */
    val urlGithub = "https://github.com/AlmasB/FXGL"

    /**
     * link to Heroku leaderboard server
     */
    val urlLeaderboard = "http://fxgl-top.herokuapp.com/"

    /**
     * link to google forms feedback
     */
    val urlGoogleForms = "https://goo.gl/forms/6wrMnOBxTE1fEpOy2"

    /**
     * how often to check for updates
     */
    val versionCheckDays = 7

    /**
     * profiles are saved in this directory
     */
    val profileDir = "profiles/"

    /**
     * profile data is saved as this file
     */
    val profileName = "user.profile"

    /**
     * save files are saved in this directory
     */
    val saveDir = "saves/"

    val saveFileExt = ".sav"

    val dataFileExt = ".dat"

    // DYNAMIC - can be modified at runtime

    @get:JvmName("devBBoxColorProperty")
    val devBBoxColor = SimpleObjectProperty<Color>(Color.web("#ff0000"))
    @get:JvmName("devSensorColorProperty")
    val devSensorColor = SimpleObjectProperty<Color>(Color.YELLOW)

    @get:JvmName("devShowBBoxProperty")
    val devShowBBox = SimpleBooleanProperty(false)
    @get:JvmName("devShowPositionProperty")
    val devShowPosition = SimpleBooleanProperty(false)

    val language = SimpleObjectProperty<Language>()
    val fullScreen = SimpleBooleanProperty(false)

    // WRAPPERS

    val configClass: Optional<Class<*>>
        get() = Optional.ofNullable(configClassInternal)

    override fun save(profile: UserProfile) {
        val bundle = Bundle("menusettings")

        bundle.put("fullscreen", fullScreen.value)

        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        val bundle = profile.getBundle("menusettings")
        fullScreen.value = bundle.get("fullscreen")
    }

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
                "UI Factory: " + uiFactory.javaClass + '\n'.toString()
    }
}