/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.asset.AssetLoader
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.devtools.profiling.Profiler
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.gameplay.Gameplay
import com.almasb.fxgl.gameplay.achievement.AchievementEvent
import com.almasb.fxgl.gameplay.achievement.AchievementStore
import com.almasb.fxgl.gameplay.notification.NotificationEvent
import com.almasb.fxgl.gameplay.notification.NotificationServiceProvider
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.net.FXGLNet
import com.almasb.fxgl.saving.LoadEvent
import com.almasb.fxgl.saving.SaveEvent
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.settings.GameSettings
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.almasb.fxgl.time.Timer
import com.almasb.fxgl.ui.ErrorDialog
import com.almasb.fxgl.ui.FXGLDisplay
import com.almasb.fxgl.util.Consumer
import com.gluonhq.charm.down.Platform
import com.gluonhq.charm.down.Services
import com.gluonhq.charm.down.plugins.LifecycleEvent
import com.gluonhq.charm.down.plugins.LifecycleService
import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.event.EventHandler
import javafx.stage.Stage
import java.util.*
import java.util.concurrent.Callable

/**
 * Represents the entire FXGL infrastructure and enables access
 * to various subsystems.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGL
private constructor(
        private val app: GameApplication,
        private val settings: ReadOnlyGameSettings,
        private val stage: Stage
) {

    private val version: String

    private var isFirstRun: Boolean = false

    private lateinit var bundle: Bundle

    private lateinit var mainWindow: MainWindow
    private lateinit var stateMachine: AppStateMachine
    private lateinit var playState: PlayState

    private val _menuHandler: MenuEventHandler by lazy {

        require(settings.isMenuEnabled) { "Menus are not enabled" }

        MenuEventHandler(app)
    }

    private val loop = LoopRunner(Consumer { loop(it) })

    private var profiler: Profiler? = null

    private val _gameConfig by lazy {
        settings.configClass
                .map { getAssetLoader().loadKV("config.kv").to(it) }
                .orElseThrow { IllegalStateException("No config class. You can set it via settings.setConfigClass()") }
    }

    /* SUBSYSTEMS */

    private val _assetLoader by lazy { AssetLoader() }
    private val _eventBus by lazy { EventBus() }
    private val _audioPlayer by lazy { AudioPlayer() }
    private val _display by lazy { FXGLDisplay() }
    private val _executor by lazy { FXGLExecutor() }
    private val _net by lazy { FXGLNet() }
    private val _gameplay by lazy { Gameplay() }
    private val _notificationService by lazy { NotificationServiceProvider() }

    init {
        version = loadVersion()

        logVersion()
    }

    private fun initLoopStartSequence() {
        log.debug("Initializing FXGL")

        val start = System.nanoTime()

        val startupScene = settings.sceneFactory.newStartup()

        // get window up ASAP
        mainWindow = MainWindow(stage, startupScene, settings)
        mainWindow.show()

        initFatalExceptionHandler()

        // give control back to FX thread while we do heavy init stuff

        Async.start {
            IOTask.setDefaultExecutor(_executor)
            IOTask.setDefaultFailAction(settings.exceptionHandler)

            isFirstRun = !FS.exists("system/")

            if (isFirstRun) {
                createRequiredDirs()
                loadDefaultSystemData()
            } else {
                loadSystemData()
            }

            if (isDesktop()) {
                runUpdaterAsync()
            }

            initStateMachine(startupScene)

            attachPauseResumeListener()
            attachEventHandlers()

            // finish init on FX thread
            Async.startFX {
                mainWindow.addKeyHandler {
                    stateMachine.currentState.input.onKeyEvent(it)
                }

                mainWindow.addMouseHandler {
                    stateMachine.currentState.input.onMouseEvent(it)
                }

                // reroute any events to current state input
                mainWindow.addGlobalHandler {
                    stateMachine.currentState.input.fireEvent(it)
                }

                // these things need to be called early before the main loop
                // so that menus can correctly display input controls, etc.
                // this is called once per application lifetime
                runPreInit()

                log.infof("FXGL initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0)

                loop.start()
            }
        }
    }

    private fun loadVersion(): String {
        return ResourceBundle.getBundle("com.almasb.fxgl.app.system").getString("fxgl.version")
    }

    private fun logVersion() {
        val platform = "${Platform.getCurrent()}" + if (isBrowser()) " BROWSER" else ""

        log.info("FXGL-$version on $platform")
        log.info("Source code and latest versions at: https://github.com/AlmasB/FXGL")
        log.info("             Join the FXGL chat at: https://gitter.im/AlmasB/FXGL")
    }

    private fun attachPauseResumeListener() {
        if (FXGL.isMobile()) {
            Services.get(LifecycleService::class.java).ifPresent { service ->
                service.addListener(LifecycleEvent.PAUSE) { loop.pause() }
                service.addListener(LifecycleEvent.RESUME) { loop.resume() }
            }
        } else {
            stage.iconifiedProperty().addListener { _, _, isMinimized ->
                if (isMinimized) {
                    loop.pause()
                } else {
                    loop.resume()
                }
            }
        }
    }

    private fun initStateMachine(startupScene: FXGLScene) {
        log.debug("Initializing state machine and application states")

        val sceneFactory = settings.sceneFactory

        // STARTUP is default
        val initial = StartupState(app, startupScene)

        val loading = LoadingState(app, sceneFactory.newLoadingScene())
        val play = PlayState(sceneFactory.newGameScene(app.width, app.height))

        // reasonable hack to trigger dialog state init before intro and menus
        DialogSubState.view

        val intro = if (settings.isIntroEnabled) IntroState(app, sceneFactory.newIntro()) else AppState.EMPTY

        val mainMenu = if (settings.isMenuEnabled) MainMenuState(sceneFactory.newMainMenu(app)) else AppState.EMPTY

        val gameMenu = if (settings.isMenuEnabled) GameMenuState(sceneFactory.newGameMenu(app)) else AppState.EMPTY

        stateMachine = AppStateMachine(loading, play, DialogSubState, intro, mainMenu, gameMenu, initial)

        stateMachine.addListener(object : StateChangeListener {
            override fun beforeEnter(state: State) {
                if (state is AppState) {
                    mainWindow.setScene(state.scene)
                } else if (state is SubState) {
                    getScene().root.children.add(state.view)
                }
            }

            override fun entered(state: State) {}

            override fun beforeExit(state: State) {}

            override fun exited(state: State) {
                if (state is SubState) {
                    getScene().root.children.remove(state.view)
                }
            }
        })

        playState = stateMachine.playState as PlayState
    }

    private fun createRequiredDirs() {
        FS.createDirectoryTask("system/")
                .then { FS.writeDataTask(listOf("This directory contains FXGL system data files."), "system/Readme.txt") }
                .onFailure { e ->
                    log.warning("Failed to create system dir: $e")
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
                }
                .run()
    }

    private fun saveSystemData() {
        log.debug("Saving FXGL system data")

        FS.writeDataTask(bundle, "system/fxgl.bundle")
                .onFailure { log.warning("Failed to save: $it") }
                .run()
    }

    private fun loadSystemData() {
        log.debug("Loading FXGL system data")

        FS.readDataTask<Bundle>("system/fxgl.bundle")
                .onSuccess {
                    bundle = it
                    bundle.log()
                }
                .onFailure {
                    log.warning("Failed to load: $it")
                    loadDefaultSystemData()
                }
                .run()
    }

    private fun loadDefaultSystemData() {
        log.debug("Loading default FXGL system data")

        // populate with default info
        bundle = Bundle("FXGL")
    }

    private fun runUpdaterAsync() {
        Async.start { UpdaterTask().run() }
    }

    private fun initFatalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, error -> handleFatalError(error) }
    }

    private fun attachEventHandlers() {
        getEventBus().addEventHandler(NotificationEvent.ANY, EventHandler { e -> getAudioPlayer().onNotificationEvent(e) })
        getEventBus().addEventHandler(AchievementEvent.ANY, EventHandler { e -> getNotificationService().onAchievementEvent(e) })

        getEventBus().addEventHandler(SaveEvent.ANY, EventHandler { e ->
            settings.save(e.getProfile())
            getAudioPlayer().save(e.getProfile())
            getInput().save(e.getProfile())
            getGameplay().save(e.getProfile())
        })

        getEventBus().addEventHandler(LoadEvent.ANY, EventHandler { e ->
            settings.load(e.getProfile())
            getAudioPlayer().load(e.getProfile())
            getInput().load(e.getProfile())
            getGameplay().load(e.getProfile())
        })

        getEventBus().scanForHandlers(app)
    }

    private fun runPreInit() {
        log.debug("Running preInit()")

        if (getSettings().isProfilingEnabled) {
            profiler = Profiler()
        }

        initAchievements()

        if (FXGL.isDesktop()) {
            // 1. register system actions
            SystemActions.bind(getInput())
        }

        // 2. register user actions
        app.initInput()

        // 3. scan for annotated methods and register them too
        getInput().scanForUserActions(app)

        generateDefaultProfile()

        app.preInit()
    }

    /**
     * Finds all @SetAchievementStore classes and registers achievements.
     */
    private fun initAchievements() {
        getSettings().achievementStoreClass.ifPresent { storeClass ->
            val storeObject = ReflectionUtils.newInstance<AchievementStore>(storeClass as Class<AchievementStore>)
            storeObject.initAchievements(getGameplay().achievementManager)
        }
    }

    private fun generateDefaultProfile() {
        if (getSettings().isMenuEnabled) {
            _menuHandler.generateDefaultProfile()
        }
    }

    private fun loop(tpf: Double) {
        val frameStart = System.nanoTime()

        stateMachine.onUpdate(tpf)

        if (getSettings().isProfilingEnabled) {
            val frameTook = System.nanoTime() - frameStart

            profiler?.update(loop.fps, frameTook)
            profiler?.render(getGameScene().profilerText)
        }
    }

    /**
     * Can be used when settings.setSingleStep() is true.
     */
    protected fun stepLoop() {
        playState.step(loop.tpf)
    }

    companion object {

        private lateinit var engine: FXGL

        private val log = Logger.get("FXGL")

        private var configured = false

        @JvmStatic fun configure(app: GameApplication, settings: ReadOnlyGameSettings, stage: Stage) {
            if (configured)
                return

            configured = true

            engine = FXGL(app, settings, stage)

            log.debug("FXGL started")
        }

        @JvmStatic protected fun startLoop() {
            engine.initLoopStartSequence()
        }

        @JvmStatic fun exit() {
            log.debug("Exiting FXGL")

            if (getSettings().isMenuEnabled) {
                engine._menuHandler.saveProfile()
            }

            log.debug("Shutting down background threads")
            getExecutor().shutdownNow()

            engine.profiler?.print()

            engine.saveSystemData()

            log.debug("Closing logger and exiting JavaFX")

            Logger.close()
            javafx.application.Platform.exit()
        }

        private var handledOnce = false

        @JvmStatic fun handleFatalError(error: Throwable) {
            if (handledOnce) {
                // just ignore to avoid spamming dialogs
                return
            }

            handledOnce = true

            if (Logger.isConfigured()) {
                log.fatal("Uncaught Exception:", error)
                log.fatal("Application will now exit")
            } else {
                println("Uncaught Exception:")
                error.printStackTrace()
                println("Application will now exit")
            }

            // stop main loop from running as we cannot continue
            engine.loop.stop()

            // assume we are running on JavaFX Application thread
            // block with error dialog so that user can read the error
            ErrorDialog(error).showAndWait()

            if (engine.loop.isStarted) {
                // exit normally
                exit()
            } else {
                if (Logger.isConfigured()) {
                    Logger.close()
                }

                // we failed during launch, so abnormal exit
                System.exit(-1)
            }
        }

        internal fun getScene(): FXGLScene {
            return engine.mainWindow.getCurrentScene()
        }

        internal fun fixAspectRatio() {
            engine.mainWindow.fixAspectRatio()
        }

        @JvmStatic fun saveScreenshot() = engine.mainWindow.saveScreenshot()

        /* STATIC ACCESSORS */

        @JvmStatic fun getVersion() = engine.version

        // cheap hack for now
        @JvmStatic fun isBrowser() = System.getProperty("fxgl.isBrowser", "false") == "true"

        // javafxports doesn't have "web" option, so will incorrectly default to desktop, hence the extra check
        @JvmStatic fun isDesktop() = !isBrowser() && Platform.isDesktop()
        @JvmStatic fun isMobile() = isAndroid() || isIOS()
        @JvmStatic fun isAndroid() = Platform.isAndroid()
        @JvmStatic fun isIOS() = Platform.isIOS()

        /**
         * @return FXGL system settings
         */
        @JvmStatic fun getSettings(): ReadOnlyGameSettings = if (configured) engine.settings else GameSettings().toReadOnly()

        @Suppress("UNCHECKED_CAST")
        @JvmStatic fun <T> getGameConfig(): T = engine._gameConfig as T

        /**
         * @return instance of the running game application
         */
        @JvmStatic fun getApp() = engine.app

        @JvmStatic fun getAppWidth() = getApp().width

        @JvmStatic fun getAppHeight() = getApp().height

        /**
         * @return instance of the running game application cast to the actual type
         */
        @Suppress("UNCHECKED_CAST")
        @JvmStatic fun <T : GameApplication> getAppCast() = getApp() as T

        @JvmStatic fun getStateMachine() = engine.stateMachine

        /**
         * Note: the system bundle is saved on exit and loaded on init.
         * This bundle is meant to be used by the FXGL system only.
         * If you want to save global (non-gameplay) data use user profiles instead.
         *
         * @return FXGL system data bundle
         */
        @JvmStatic fun getSystemBundle() = engine.bundle

        /**
         * @return true iff FXGL is running for the first time
         * @implNote we actually check if "system/" exists in running dir, so if it was
         *            deleted, then this method also returns true
         */
        @JvmStatic fun isFirstRun() = engine.isFirstRun

        @JvmStatic fun getMenuHandler() = engine._menuHandler

        @JvmStatic fun getExceptionHandler() = getSettings().exceptionHandler
        @JvmStatic fun getUIFactory() = getSettings().uiFactory

        @JvmStatic fun getNotificationService() = engine._notificationService

        @JvmStatic fun getAssetLoader() = engine._assetLoader

        @JvmStatic fun getEventBus() = engine._eventBus

        @JvmStatic fun getAudioPlayer() = engine._audioPlayer

        @JvmStatic fun getDisplay() = engine._display

        @JvmStatic fun getExecutor() = engine._executor

        @JvmStatic fun getNet() = engine._net

        @JvmStatic fun getGameplay() = engine._gameplay

        /**
         * @return time per frame (in this frame)
         */
        @JvmStatic fun tpf() = engine.loop.tpf

        @JvmStatic fun getGameState() = engine.playState.gameState
        @JvmStatic fun getGameWorld() = engine.playState.gameWorld
        @JvmStatic fun getPhysicsWorld() = engine.playState.physicsWorld
        @JvmStatic fun getGameScene() = engine.playState.gameScene

        /**
         * @return play state input
         */
        @JvmStatic fun getInput(): Input = engine.playState.input

        /**
         * @return play state timer
         */
        @JvmStatic fun getMasterTimer(): Timer = engine.playState.timer

        /**
         * @return new instance on each call
         */
        @JvmStatic fun newLocalTimer() = getMasterTimer().newLocalTimer()

        /**
         * @param name unique name for timer
         * @return new instance on each call
         */
        @JvmStatic fun newOfflineTimer(name: String): LocalTimer = OfflineTimer(name, getSystemBundle())

        // TODO: do these belong here?

        /**
         * @return a string translated to the language used by FXGL game
         */
        @JvmStatic fun getLocalizedString(key: String): String {
            val langName = getSettings().language.value.resourceBundleName()

            val bundle = getAssetLoader().loadResourceBundle("languages/$langName.properties")

            try {
                return bundle.getString(key)
            } catch (e: Exception) {
                log.warning("$key is not localized for language ${getSettings().language.value}")
                return "MISSING!"
            }
        }

        /**
         * @return binding to a string translated to the language used by FXGL game
         */
        @JvmStatic fun localizedStringProperty(key: String): StringBinding {
            return Bindings.createStringBinding(Callable { getLocalizedString(key) }, getSettings().language)
        }
    }
}