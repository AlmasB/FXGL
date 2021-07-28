/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.achievement.Achievement
import com.almasb.fxgl.achievement.AchievementService
import com.almasb.fxgl.app.scene.SceneFactory
import com.almasb.fxgl.app.services.*
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.serialization.SerializableType
import com.almasb.fxgl.core.util.Platform
import com.almasb.fxgl.cutscene.CutsceneService
import com.almasb.fxgl.dev.DevService
import com.almasb.fxgl.gameplay.GameDifficulty
import com.almasb.fxgl.io.FileSystemService
import com.almasb.fxgl.localization.Language
import com.almasb.fxgl.localization.Language.Companion.ENGLISH
import com.almasb.fxgl.localization.Language.Companion.FRENCH
import com.almasb.fxgl.localization.Language.Companion.GERMAN
import com.almasb.fxgl.localization.Language.Companion.HUNGARIAN
import com.almasb.fxgl.localization.Language.Companion.RUSSIAN
import com.almasb.fxgl.localization.LocalizationService
import com.almasb.fxgl.minigames.MiniGameService
import com.almasb.fxgl.net.NetService
import com.almasb.fxgl.notification.impl.NotificationServiceProvider
import com.almasb.fxgl.notification.view.NotificationView
import com.almasb.fxgl.notification.view.XboxNotificationView
import com.almasb.fxgl.physics.CollisionDetectionStrategy
import com.almasb.fxgl.profile.SaveLoadService
import com.almasb.fxgl.ui.FXGLDialogFactoryServiceProvider
import com.almasb.fxgl.ui.FXGLUIFactoryServiceProvider
import javafx.beans.property.*
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
 * Stores cursor information.
 */
data class CursorInfo(
        val imageName: String,
        val hotspotX: Double,
        val hotspotY: Double
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
         * If true, during resize the game will auto-scale to maintain consistency across all displays.
         * If false, during resize, only the window will change size, which allows different displays
         * to have different views.
         * For example, editor type apps may wish to set this to false to maximize "usable" space.
         */
        var isScaleAffectedOnResize: Boolean = true,

        /**
         * If set to true, the intro video/animation will
         * be played before the start of the game.
         */
        var isIntroEnabled: Boolean = false,

        /**
         * Setting to true enables the main menu.
         */
        var isMainMenuEnabled: Boolean = false,

        /**
         * Setting to true enables the game menu.
         */
        var isGameMenuEnabled: Boolean = true,

        var isUserProfileEnabled: Boolean = false,

        /**
         * Setting to true will enable profiler that reports on performance
         * when FXGL exits.
         * Also shows render and performance FPS in the bottom left corner
         * when the application is run.
         */
        var isProfilingEnabled: Boolean = false,

        var isDeveloperMenuEnabled: Boolean = false,

        var isClickFeedbackEnabled: Boolean = false,

        /**
         * If true, entity builder will preload entities on a background thread to speed up
         * entity building.
         * Default: true.
         */
        var isEntityPreloadEnabled: Boolean = true,

        /**
         * If true, allows FXGL to make write calls to the file system, for example
         * to create log files.
         * In cases where running from a directory that requires elevated privileges,
         * it is recommended to disable this setting.
         */
        var isFileSystemWriteAllowed: Boolean = true,

        /**
         * Setting to false will disable asking for confirmation on exit.
         * This is useful for faster compile -> run -> exit.
         */
        var isCloseConfirmation: Boolean = false,

        var isSingleStep: Boolean = false,

        var isPauseMusicWhenMinimized: Boolean = true,

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

        var collisionDetectionStrategy: CollisionDetectionStrategy = CollisionDetectionStrategy.BRUTE_FORCE,

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

        /**
         * Number of ticks per second computed by the engine.
         * This value can be the same as or less than the display refresh rate.
         * Default value is -1, which means "match display refresh rate".
         */
        var ticksPerSecond: Int = -1,

        /**
         * How fast the 3D mouse movements are (example, rotating the camera).
         */
        var mouseSensitivity: Double = 0.2,

        var defaultLanguage: Language = ENGLISH,

        var defaultCursor: CursorInfo = CursorInfo("fxgl_default_cursor.png", 7.0, 6.0),

        var isNative: Boolean = false,

        /**
         * Set this to true if this is a 3D game.
         */
        var is3D: Boolean = false,

        /* EXPERIMENTAL */

        var isExperimentalTiledLargeMap: Boolean = false,

        /* CONFIGS */

        var configClass: Class<*>? = null,

        /* CUSTOMIZABLE SERVICES BELOW */

        var engineServices: MutableList<Class<out EngineService>> = arrayListOf(
                // this is the order in which services will be initialized
                // by design, the order of services should not matter,
                // however some services can depend on others, so no-dep ones should come first
                FXGLAssetLoaderService::class.java,
                FXGLApplication.GameApplicationService::class.java,
                FXGLDialogService::class.java,
                IOTaskExecutorService::class.java,
                FileSystemService::class.java,
                LocalizationService::class.java,
                SystemBundleService::class.java,
                SaveLoadService::class.java,
                FXGLUIFactoryServiceProvider::class.java,
                FXGLDialogFactoryServiceProvider::class.java,
                AudioPlayer::class.java,
                NotificationServiceProvider::class.java,
                AchievementService::class.java,
                CutsceneService::class.java,
                MiniGameService::class.java,
                NetService::class.java,
                UpdaterService::class.java,
                DevService::class.java
        ),

        /**
         * Provide a custom scene factory.
         */
        var sceneFactory: SceneFactory = SceneFactory(),

        var notificationViewClass: Class<out NotificationView> = XboxNotificationView::class.java,

        var achievements: List<Achievement> = arrayListOf(),

        var supportedLanguages: List<Language> = arrayListOf(
                ENGLISH, FRENCH, GERMAN, RUSSIAN, HUNGARIAN
        )
) {

    fun addEngineService(service: Class<out EngineService>) {
        engineServices.add(service)
    }

    fun removeEngineService(service: Class<out EngineService>) {
        engineServices.remove(service)
    }

    fun setEngineServiceProvider(oldService: Class<out EngineService>, newService: Class<out EngineService>) {
        engineServices.removeIf { oldService.isAssignableFrom(it) }
        addEngineService(newService)
    }

    fun setHeightFromRatio(ratio: Double) {
        height = (width / ratio).roundToInt()
    }

    fun setWidthFromRatio(ratio: Double) {
        width = (height * ratio).roundToInt()
    }

    fun toReadOnly(userAppClass: Class<*> = GameApplication::class.java): ReadOnlyGameSettings {
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
                isScaleAffectedOnResize,
                isIntroEnabled,
                isMainMenuEnabled,
                isGameMenuEnabled,
                isUserProfileEnabled,
                isProfilingEnabled,
                isDeveloperMenuEnabled,
                isClickFeedbackEnabled,
                isEntityPreloadEnabled,
                isFileSystemWriteAllowed,
                isCloseConfirmation,
                isSingleStep,
                isPauseMusicWhenMinimized,
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
                collisionDetectionStrategy,
                secondsIn24h,
                randomSeed,
                ticksPerSecond,
                userAppClass,
                mouseSensitivity,
                defaultLanguage,
                defaultCursor,
                isNative,
                is3D,
                isExperimentalTiledLargeMap,
                configClass,
                unmodifiableList(engineServices),
                sceneFactory,
                notificationViewClass,
                unmodifiableList(achievements),
                unmodifiableList(supportedLanguages.sortedBy { it.name }))
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
         * If true, during resize the game will auto-scale to maintain consistency across all displays.
         * If false, during resize, only the window will change size, which allows different displays
         * to have different views.
         * For example, editor type apps may wish to set this to false to maximize "usable" space.
         */
        var isScaleAffectedOnResize: Boolean = true,

        /**
         * If set to true, the intro video/animation will
         * be played before the start of the game.
         */
        val isIntroEnabled: Boolean,

        /**
         * Setting to true enables the main menu.
         */
        var isMainMenuEnabled: Boolean,

        /**
         * Setting to true enables the game menu.
         */
        var isGameMenuEnabled: Boolean,

        val isUserProfileEnabled: Boolean,

        /**
         * Setting to true will enable profiler that reports on performance
         * when FXGL exits.
         * Also shows render and performance FPS in the bottom left corner
         * when the application is run.
         */
        val isProfilingEnabled: Boolean,

        val isDeveloperMenuEnabled: Boolean,

        val isClickFeedbackEnabled: Boolean,

        /**
         * If true, entity builder will preload entities on a background thread to speed up
         * entity building.
         * Default: true.
         */
        val isEntityPreloadEnabled: Boolean,

        /**
         * If true, allows FXGL to make write calls to the file system, for example
         * to create log files.
         * In cases where running from a directory that requires elevated privileges,
         * it is recommended to disable this setting.
         */
        val isFileSystemWriteAllowed: Boolean,

        /**
         * Setting to false will disable asking for confirmation on exit.
         * This is useful for faster compile -> run -> exit.
         */
        val isCloseConfirmation: Boolean,

        val isSingleStep: Boolean,

        val isPauseMusicWhenMinimized: Boolean,

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

        val collisionDetectionStrategy: CollisionDetectionStrategy,

        /**
         * Set how many real seconds are in 24 game hours, default = 60.
         */
        val secondsIn24h: Int,

        val randomSeed: Long,

        val ticksPerSecond: Int,

        val userAppClass: Class<*>,

        /**
         * How fast the 3D mouse movements are (example, rotating the camera).
         */
        mouseSensitivity: Double,

        private val defaultLanguage: Language,

        val defaultCursor: CursorInfo,

        /**
         * Are running on mobile, or natively (AOT-compiled) on desktop.
         */
        val isNative: Boolean,

        val is3D: Boolean,

        /* EXPERIMENTAL */

        val isExperimentalTiledLargeMap: Boolean,

        /* CONFIGS */

        private val configClassInternal: Class<*>?,

        /* CUSTOMIZABLE SERVICES BELOW */

        val engineServices: List<Class<out EngineService>>,

        /**
         * Provide a custom scene factory.
         */
        val sceneFactory: SceneFactory,

        val notificationViewClass: Class<out NotificationView>,

        val achievements: List<Achievement>,

        val supportedLanguages: List<Language>

) : SerializableType {

    /* STATIC - cannot be modified at runtime */

    /**
     * where to look for latest stable project POM
     */
    val urlPOM = "https://raw.githubusercontent.com/AlmasB/FXGL/release/README.md"

    /**
     * project GitHub repo
     */
    val urlGithub = "https://github.com/AlmasB/FXGL"

    /**
     * link to Heroku leaderboard server
     */
    val urlLeaderboard = "http://fxgl-top.herokuapp.com/"

    /**
     * how often to check for updates
     */
    val versionCheckDays = 7

    /**
     * profiles are saved in this directory
     */
    val profileDir = "profiles/"

    val saveFileExt = "sav"

    val platform: Platform
        get() = runtimeInfo.platform

    val isDesktop: Boolean
        get() = platform.isDesktop

    val isMobile: Boolean
        get() = platform.isMobile

    val isEmbedded: Boolean
        get() = platform.isEmbedded

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

    @get:JvmName("devEnableDebugCameraProperty")
    val devEnableDebugCamera = SimpleBooleanProperty(false)

    /*
    Usage of below:
    1. UI objects should bi-directionally bind to these properties.
    2. Engine services should one-directionally bind to these properties and not expose their own properties.
    3. Any kind of programmatic access should modify these properties, in which case (1) and (2) are auto-updated.

    These are saved by the Settings, so engine services do not need to save their copy of these.
     */

    val language = SimpleObjectProperty(defaultLanguage)

    /**
     * Allows toggling fullscreen on/off from code.
     * [isFullScreenAllowed] must be true, otherwise it's no-op.
     */
    val fullScreen = SimpleBooleanProperty(isFullScreenFromStart)

    internal val scaledWidthProp = ReadOnlyDoubleWrapper()
    internal val scaledHeightProp = ReadOnlyDoubleWrapper()

    /**
     * @return actual width of the scene root
     */
    fun actualWidthProperty() = scaledWidthProp.readOnlyProperty

    /**
     * @return actual height of the scene root
     */
    fun actualHeightProperty() = scaledHeightProp.readOnlyProperty

    val actualWidth: Double
        get() = scaledWidthProp.value

    val actualHeight: Double
        get() = scaledHeightProp.value

    private val appWidthProp = ReadOnlyDoubleWrapper(width.toDouble()).readOnlyProperty
    private val appHeightProp = ReadOnlyDoubleWrapper(height.toDouble()).readOnlyProperty

    /**
     * @return a convenience property that auto-sets to target (app) width if auto-scaling is enabled
     * and uses actual javafx scene width if not
     */
    fun prefWidthProperty(): ReadOnlyDoubleProperty {
        return if (isScaleAffectedOnResize) appWidthProp else actualWidthProperty()
    }

    /**
     * @return a convenience property that auto-sets to target (app) height if auto-scaling is enabled
     * and uses actual javafx scene height if not
     */
    fun prefHeightProperty(): ReadOnlyDoubleProperty {
        return if (isScaleAffectedOnResize) appHeightProp else actualHeightProperty()
    }

    val profileName = SimpleStringProperty("DEFAULT")

    private val gameDifficultyProp = SimpleObjectProperty(GameDifficulty.MEDIUM)

    fun gameDifficultyProperty(): ObjectProperty<GameDifficulty> = gameDifficultyProp

    var gameDifficulty: GameDifficulty
        get() = gameDifficultyProp.value
        set(value) { gameDifficultyProp.value = value }

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

    private val mouseSensitivityProp = SimpleDoubleProperty(mouseSensitivity)

    var mouseSensitivity: Double
        get() = mouseSensitivityProp.value
        set(value) { mouseSensitivityProp.value = value }

    // WRAPPERS

    val configClass: Optional<Class<*>>
        get() = Optional.ofNullable(configClassInternal)

    init {
        applySettings()
    }

    override fun write(bundle: Bundle) {
        bundle.put("fullscreen", fullScreen.value)
        bundle.put("globalMusicVolume", globalMusicVolume)
        bundle.put("globalSoundVolume", globalSoundVolume)
    }

    override fun read(bundle: Bundle) {
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
                "Profiling: " + isProfilingEnabled + '\n'.toString() +
                "Single step:" + isSingleStep + '\n'.toString() +
                "App Mode: " + applicationMode + '\n'.toString() +
                "Menu Key: " + menuKey + '\n'.toString() +
                "Stage Style: " + stageStyle + '\n'.toString() +
                "Scene Factory: " + sceneFactory.javaClass + '\n'.toString()
    }
}