package com.almasb.fxgl.app

import com.almasb.fxgl.asset.AssetLoader
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.gameplay.Gameplay
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.net.FXGLNet
import com.almasb.fxgl.saving.LoadEvent
import com.almasb.fxgl.saving.SaveEvent
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.settings.ReadOnlyGameSettings
import com.almasb.fxgl.ui.ErrorDialog
import com.gluonhq.charm.down.Platform
import com.gluonhq.charm.down.Services
import com.gluonhq.charm.down.plugins.LifecycleEvent
import com.gluonhq.charm.down.plugins.LifecycleService
import javafx.event.EventHandler
import javafx.stage.Stage
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class Engine(
        internal val app: GameApplication,
        internal val settings: ReadOnlyGameSettings,
        private val stage: Stage
) {

    private val log = Logger.get(javaClass)

    internal val version: String

    /**
     * @return true iff FXGL is running for the first time
     * @implNote we actually check if "system/" exists in running dir, so if it was
     *            deleted, then this method also returns true
     */
    private var isFirstRun: Boolean = false

    internal lateinit var bundle: Bundle

    internal lateinit var mainWindow: MainWindow
    internal lateinit var stateMachine: AppStateMachine
    internal lateinit var playState: PlayState

    internal val menuHandler: MenuEventHandler by lazy {

        require(settings.isMenuEnabled) { "Menus are not enabled" }

        MenuEventHandler(app)
    }

    internal val loop = LoopRunner(Consumer { loop(it) })

    //private var profiler: Profiler? = null

    internal val gameConfig by lazy {
        settings.configClass
                .map { FXGL.getAssetLoader().loadKV("config.kv").to(it) }
                .orElseThrow { IllegalStateException("No config class. You can set it via settings.setConfigClass()") }
    }

    /* SUBSYSTEMS */

    internal val assetLoader by lazy { AssetLoader() }
    internal val eventBus by lazy { EventBus() }
    internal val audioPlayer by lazy { AudioPlayer() }
    internal val display by lazy { FXGLDisplay() }
    internal val executor by lazy { FXGLExecutor() }
    internal val net by lazy { FXGLNet() }
    internal val gameplay by lazy { Gameplay() }
    internal val notificationService: Any by lazy { TODO() }

    init {
        log.debug("Initializing FXGL")

        version = loadVersion()

        logVersion()
    }

    private fun loadVersion(): String {
        return ResourceBundle.getBundle("com.almasb.fxgl.app.system").getString("fxgl.version")
    }

    private fun logVersion() {
        val platform = "${Platform.getCurrent()}" + if (FXGL.isBrowser()) " BROWSER" else ""

        log.info("FXGL-$version on $platform")
        log.info("Source code and latest versions at: https://github.com/AlmasB/FXGL")
        log.info("             Join the FXGL chat at: https://gitter.im/AlmasB/FXGL")
    }

    internal fun startLoop() {
        val start = System.nanoTime()

        val startupScene = settings.sceneFactory.newStartup()

        // get window up ASAP
        mainWindow = MainWindow(stage, startupScene, settings)
        mainWindow.show()

        initFatalExceptionHandler()

        // give control back to FX thread while we do heavy init stuff

        Async.start {
            IOTask.setDefaultExecutor(executor)
            IOTask.setDefaultFailAction(settings.exceptionHandler)

            isFirstRun = !FS.exists("system/")

            if (isFirstRun) {
                createRequiredDirs()
                loadDefaultSystemData()
            } else {
                loadSystemData()
            }

            if (FXGL.isDesktop()) {
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
                    FXGL.getScene().root.children.add(state.view)
                }
            }

            override fun entered(state: State) {}

            override fun beforeExit(state: State) {}

            override fun exited(state: State) {
                if (state is SubState) {
                    FXGL.getScene().root.children.remove(state.view)
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
        Thread.setDefaultUncaughtExceptionHandler { _, error -> FXGL.handleFatalError(error) }
    }

    private fun attachEventHandlers() {
        //getEventBus().addEventHandler(NotificationEvent.ANY, EventHandler { e -> getAudioPlayer().onNotificationEvent(e) })
        //getEventBus().addEventHandler(AchievementEvent.ANY, EventHandler { e -> getNotificationService().onAchievementEvent(e) })

        FXGL.getEventBus().addEventHandler(SaveEvent.ANY, EventHandler { e ->
            settings.save(e.getProfile())
            FXGL.getAudioPlayer().save(e.getProfile())
            FXGL.getInput().save(e.getProfile())
            FXGL.getGameplay().save(e.getProfile())
        })

        FXGL.getEventBus().addEventHandler(LoadEvent.ANY, EventHandler { e ->
            settings.load(e.getProfile())
            FXGL.getAudioPlayer().load(e.getProfile())
            FXGL.getInput().load(e.getProfile())
            FXGL.getGameplay().load(e.getProfile())
        })

        FXGL.getEventBus().scanForHandlers(app)
    }

    private fun runPreInit() {
        log.debug("Running preInit()")

        if (FXGL.getSettings().isProfilingEnabled) {
            //profiler = Profiler()
        }

        initAchievements()

        if (FXGL.isDesktop()) {
            // 1. register system actions
            SystemActions.bind(FXGL.getInput())
        }

        // 2. register user actions
        app.initInput()

        // 3. scan for annotated methods and register them too
        FXGL.getInput().scanForUserActions(app)

        generateDefaultProfile()

        app.preInit()
    }

    /**
     * Finds all @SetAchievementStore classes and registers achievements.
     */
    private fun initAchievements() {
//        getSettings().achievementStoreClass.ifPresent { storeClass ->
//            val storeObject = ReflectionUtils.newInstance<AchievementStore>(storeClass as Class<AchievementStore>)
//            storeObject.initAchievements(getGameplay().achievementManager)
//        }
    }

    private fun generateDefaultProfile() {
        if (FXGL.getSettings().isMenuEnabled) {
            menuHandler.generateDefaultProfile()
        }
    }

    private fun loop(tpf: Double) {
        val frameStart = System.nanoTime()

        stateMachine.onUpdate(tpf)

        if (FXGL.getSettings().isProfilingEnabled) {
            val frameTook = System.nanoTime() - frameStart

            //profiler?.update(loop.fps, frameTook)
            //profiler?.render(getGameScene().profilerText)
        }
    }

    /**
     * Can be used when settings.setSingleStep() is true.
     */
    protected fun stepLoop() {
        playState.step(loop.tpf)
    }

    private var handledOnce = false

    internal fun handleFatalError(error: Throwable) {
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
        loop.stop()

        // assume we are running on JavaFX Application thread
        // block with error dialog so that user can read the error
        ErrorDialog(error).showAndWait()

        if (loop.isStarted) {
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

    internal fun exit() {
        log.debug("Exiting FXGL")

        if (settings.isMenuEnabled) {
            menuHandler.saveProfile()
        }

        log.debug("Shutting down background threads")
        executor.shutdownNow()

        //engine.profiler?.print()

        saveSystemData()

        log.debug("Closing logger and exiting JavaFX")

        Logger.close()
        javafx.application.Platform.exit()
    }

}