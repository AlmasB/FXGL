/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.achievement.Achievement
import com.almasb.fxgl.achievement.AchievementManager
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.util.Platform
import com.almasb.fxgl.cutscene.CutsceneService
import com.almasb.fxgl.localization.Language
import com.almasb.fxgl.minigames.MiniGameService
import com.almasb.fxgl.notification.impl.NotificationServiceProvider
import com.almasb.fxgl.notification.view.NotificationView
import com.almasb.fxgl.notification.view.XboxNotificationView
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable
import com.almasb.fxgl.ui.DialogFactory
import com.almasb.fxgl.ui.FXGLDialogFactory
import com.almasb.fxgl.ui.FXGLUIFactory
import com.almasb.fxgl.ui.UIFactory
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import java.util.*
import java.util.Collections.unmodifiableList
import kotlin.math.roundToInt

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
 * Stores FXGL runtime information.
 */
data class RuntimeInfo(
        val platform: Platform,
        val version: String,
        val build: String
)

/**
 * Data structure for variables that are initialised before the application (game) starts.
 *
 * Modifying any data after the start of the game has no effect.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameSettings(
        var runtimeInfo: RuntimeInfo = RuntimeInfo(Platform.WINDOWS, "11.x", "?"),

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
         * from the menu or programmatically.
         */
        var isFullScreenAllowed: Boolean = false,

        /**
         * Setting to true will start the game in fullscreen, provided
         * [isFullScreenAllowed] is also true.
         */
        var isFullScreenFromStart: Boolean = false,

        /**
         * If enabled, users can drag the corner of the main window
         * to resize it and the game.
         */
        var isManualResizeEnabled: Boolean = false,

        /**
         * If enabled, during resize black bars will be added to preserve the ratio.
         */
        var isPreserveResizeRatio: Boolean = false,

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

        var isDeveloperMenuEnabled: Boolean = false,

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
        var credits: List<String> = arrayListOf(),
        var enabledMenuItems: EnumSet<MenuItem> = EnumSet.noneOf(MenuItem::class.java),
        var stageStyle: StageStyle = StageStyle.DECORATED,
        var appIcon: String = "fxgl_icon.png",

        /**
         * Add extra css from /assets/ui/css/.
         */
        @get:JvmName("getCSSList")
        @set:JvmName("setCSSList")
        var cssList: List<String> = arrayListOf("fxgl_dark.css"),

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

        /**
         * Seed used to initialize the random number generator in FXGLMath.
         * Default value is -1, which means do not use the seed.
         * Any other value is supplied directly to FXGLMath random.
         */
        var randomSeed: Long = -1L,

        /* EXPERIMENTAL */

        var isExperimentalTiledLargeMap: Boolean = false,
        var isExperimentalNative: Boolean = false,

        /* CONFIGS */

        var configClass: Class<*>? = null,

        /* CUSTOMIZABLE SERVICES BELOW */

        var engineServices: MutableList<Class<out EngineService>> = arrayListOf(
                AudioPlayer::class.java,
                NotificationServiceProvider::class.java,
                AchievementManager::class.java,
                CutsceneService::class.java,
                MiniGameService::class.java
        ),

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

        var notificationViewClass: Class<out NotificationView> = XboxNotificationView::class.java,

        var achievements: List<Achievement> = arrayListOf()
) {

    fun setHeightFromRatio(ratio: Double) {
        height = (width / ratio).roundToInt()
    }

    fun setWidthFromRatio(ratio: Double) {
        width = (height * ratio).roundToInt()
    }

    fun toReadOnly(): ReadOnlyGameSettings {
        return ReadOnlyGameSettings(
                runtimeInfo,
                title,
                version,
                width,
                height,
                isFullScreenAllowed,
                isFullScreenFromStart,
                isManualResizeEnabled,
                isPreserveResizeRatio,
                isIntroEnabled,
                isMenuEnabled,
                isProfilingEnabled,
                isDeveloperMenuEnabled,
                isCloseConfirmation,
                isSingleStep,
                applicationMode,
                menuKey,
                unmodifiableList(credits),
                enabledMenuItems,
                stageStyle,
                appIcon,
                unmodifiableList(cssList),
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
                randomSeed,
                isExperimentalTiledLargeMap,
                isExperimentalNative,
                configClass,
                unmodifiableList(engineServices),
                sceneFactory,
                dialogFactory,
                uiFactory,
                notificationViewClass,
                unmodifiableList(achievements))
    }
}


/**
 * A copy of GameSettings with public getters only.
 */
class ReadOnlyGameSettings internal constructor(
        val runtimeInfo: RuntimeInfo,

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
         * from the menu or programmatically.
         */
        val isFullScreenAllowed: Boolean,

        /**
         * Setting to true will start the game in fullscreen, provided
         * [isFullScreenAllowed] is also true.
         */
        val isFullScreenFromStart: Boolean,

        /**
         * If enabled, users can drag the corner of the main window
         * to resize it and the game.
         */
        val isManualResizeEnabled: Boolean,

        val isPreserveResizeRatio: Boolean,

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

        val isDeveloperMenuEnabled: Boolean,

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

        @get:JvmName("getCSSList")
        val cssList: List<String>,

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

        val randomSeed: Long,

        /* EXPERIMENTAL */

        val isExperimentalTiledLargeMap: Boolean,
        val isExperimentalNative: Boolean,

        /* CONFIGS */

        private val configClassInternal: Class<*>?,

        /* CUSTOMIZABLE SERVICES BELOW */

        val engineServices: List<Class<out EngineService>>,

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

        val notificationViewClass: Class<out NotificationView>,

        val achievements: List<Achievement>

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

    val platform: Platform
        get() = runtimeInfo.platform

    val isDesktop: Boolean
        get() = platform.isDesktop

    val isMobile: Boolean
        get() = platform.isMobile

    val isBrowser: Boolean
        get() = platform.isBrowser

    val isWindows: Boolean
        get() = platform === Platform.WINDOWS

    val isMac: Boolean
        get() = platform === Platform.MAC

    val isLinux: Boolean
        get() = platform === Platform.LINUX

    val isIOS: Boolean
        get() = platform === Platform.IOS

    val isAndroid: Boolean
        get() = platform === Platform.ANDROID

    // DYNAMIC - can be modified at runtime

    @get:JvmName("devBBoxColorProperty")
    val devBBoxColor = SimpleObjectProperty<Color>(Color.web("#ff0000"))
    @get:JvmName("devSensorColorProperty")
    val devSensorColor = SimpleObjectProperty<Color>(Color.YELLOW)

    @get:JvmName("devShowBBoxProperty")
    val devShowBBox = SimpleBooleanProperty(false)
    @get:JvmName("devShowPositionProperty")
    val devShowPosition = SimpleBooleanProperty(false)

    /*
    Usage of below:
    1. UI objects should bi-directionally bind to these properties.
    2. Engine services should one-directionally bind to these properties and not expose their own properties.
    3. Any kind of programmatic access should modify these properties, in which case (1) and (2) are auto-updated.

    These are saved by the Settings, so engine services do not need to save their copy of these.
     */

    val language = SimpleObjectProperty(Language.ENGLISH)

    /**
     * Allows toggling fullscreen on/off from code.
     * [isFullScreenAllowed] must be true, otherwise it's no-op.
     */
    val fullScreen = SimpleBooleanProperty(isFullScreenFromStart)

    @get:JvmName("globalMusicVolumeProperty")
    val globalMusicVolumeProperty = SimpleDoubleProperty(0.5)

    /**
     * Set global music volume in the range [0..1],
     * where 0 = 0%, 1 = 100%.
     */
    var globalMusicVolume: Double
        get() = globalMusicVolumeProperty.value
        set(value) { globalMusicVolumeProperty.value = value }

    @get:JvmName("globalSoundVolumeProperty")
    val globalSoundVolumeProperty = SimpleDoubleProperty(0.5)

    /**
     * Set global sound volume in the range [0..1],
     * where 0 = 0%, 1 = 100%.
     */
    var globalSoundVolume: Double
        get() = globalSoundVolumeProperty.value
        set(value) { globalSoundVolumeProperty.value = value }

    // WRAPPERS

    val configClass: Optional<Class<*>>
        get() = Optional.ofNullable(configClassInternal)

    init {
        applySettings()
    }

    override fun save(profile: UserProfile) {
        val bundle = Bundle("menusettings")

        bundle.put("fullscreen", fullScreen.value)
        bundle.put("globalMusicVolume", globalMusicVolume)
        bundle.put("globalSoundVolume", globalSoundVolume)

        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        val bundle = profile.getBundle("menusettings")
        fullScreen.value = bundle.get("fullscreen")

        globalMusicVolume = bundle.get("globalMusicVolume")
        globalSoundVolume = bundle.get("globalSoundVolume")

        applySettings()
    }

    private fun applySettings() {
        if (randomSeed != -1L)
            FXGLMath.setRandom(Random(randomSeed))
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