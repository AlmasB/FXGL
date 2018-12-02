package com.almasb.fxgl.app

import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.FXGLExecutor
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.sslogger.Logger
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.saving.*
import com.almasb.fxgl.scene.FXGLScene
import com.almasb.fxgl.scene.ProgressDialog
import com.almasb.fxgl.ui.Display
import com.almasb.fxgl.ui.ErrorDialog
import com.almasb.fxgl.ui.FXGLUIConfig
import com.almasb.fxgl.ui.FontType
import com.gluonhq.charm.down.Platform
import com.gluonhq.charm.down.Services
import com.gluonhq.charm.down.plugins.LifecycleEvent
import com.gluonhq.charm.down.plugins.LifecycleService
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import java.time.LocalDateTime
import java.util.*

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
internal class Engine(
        internal val app: GameApplication,
        internal val settings: ReadOnlyGameSettings,
        private val stage: Stage
) : GameController {

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

    private lateinit var loadState: LoadingState

    internal val loop = LoopRunner(Consumer { loop(it) })

    /* SUBSYSTEMS */

    internal val assetLoader by lazy { AssetLoader() }
    internal val eventBus by lazy { EventBus() }
    internal val audioPlayer by lazy { AudioPlayer() }
    internal val display by lazy { stateMachine.dialogState as Display }
    internal val executor by lazy { FXGLExecutor() }

    private val profileName = SimpleStringProperty("no-profile")

    /**
     * Stores the default profile data. This is used to restore default settings.
     */
    private lateinit var defaultProfile: UserProfile

    private lateinit var saveLoadManager: SaveLoadManager

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

    // TODO: run this and then test if dirs/files are created, window open, etc.
    fun startLoop() {
        saveLoadManager = SaveLoadManager(profileName.value)

        val start = System.nanoTime()

        log.debug("Registering font factories")

        settings.uiFactory.registerFontFactory(FontType.UI, assetLoader.loadFont(settings.fontUI))
        settings.uiFactory.registerFontFactory(FontType.GAME, assetLoader.loadFont(settings.fontGame))
        settings.uiFactory.registerFontFactory(FontType.MONO, assetLoader.loadFont(settings.fontMono))
        settings.uiFactory.registerFontFactory(FontType.TEXT, assetLoader.loadFont(settings.fontText))

        log.debug("Setting UI factory")

        FXGLUIConfig.setUIFactory(settings.uiFactory)

        val startupScene = settings.sceneFactory.newStartup()

        // get window up ASAP
        mainWindow = MainWindow(stage, startupScene, settings)
        mainWindow.show()

        initFatalExceptionHandler()

        // give control back to FX thread while we do heavy init stuff

        Async.start {
            IOTask.setDefaultExecutor(executor)
            IOTask.setDefaultFailAction { display.showErrorBox(it) }

            isFirstRun = !FS.exists("system/")

            if (isFirstRun) {
                createRequiredDirs()
                loadDefaultSystemData()
            } else {
                loadSystemData()
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
        val initial = StartupState(startupScene)

        val loading = LoadingState(app, sceneFactory.newLoadingScene())
        val play = PlayState(sceneFactory.newGameScene(settings.width, settings.height))

        // we need dialog state before intro and menus
        val dialog = DialogSubState()

        val intro = if (settings.isIntroEnabled) IntroState(sceneFactory.newIntro()) else AppState.EMPTY

        val mainMenu = if (settings.isMenuEnabled) MainMenuState(sceneFactory.newMainMenu()) else AppState.EMPTY

        val gameMenu = if (settings.isMenuEnabled) GameMenuState(sceneFactory.newGameMenu()) else AppState.EMPTY

        stateMachine = AppStateMachine(loading, play, dialog, intro, mainMenu, gameMenu, initial)

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
        loadState = loading

        if (FXGL.getSettings().isMenuEnabled) {
            play.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
            gameMenu.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
        } else {
            play.input.addAction(object : UserAction("Pause") {
                override fun onActionBegin() {
                    PauseMenuSubState.requestShow()
                }

                override fun onActionEnd() {
                    PauseMenuSubState.unlockSwitch()
                }
            }, FXGL.getSettings().menuKey)
        }

        log.debug("State machine initialized")
    }

    private object menuKeyHandler : EventHandler<KeyEvent> {
        private var canSwitchGameMenu = true

        private fun onMenuKey(pressed: Boolean) {
            if (!pressed) {
                canSwitchGameMenu = true
                return
            }

            if (canSwitchGameMenu) {
                // we only care if menu key was pressed in one of these states
                if (FXGL.getStateMachine().isInGameMenu()) {
                    canSwitchGameMenu = false
                    FXGL.getStateMachine().startPlay()

                } else if (FXGL.getStateMachine().isInPlay()) {
                    canSwitchGameMenu = false
                    FXGL.getStateMachine().startGameMenu()

                }
            }
        }

        override fun handle(event: KeyEvent) {
            if (event.code == FXGL.getSettings().menuKey) {
                onMenuKey(event.eventType == KeyEvent.KEY_PRESSED)
            }
        }
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

    private fun initFatalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, error -> handleFatalError(error) }
    }

    private fun attachEventHandlers() {
        FXGL.getEventBus().addEventHandler(SaveEvent.ANY, EventHandler { e ->
            settings.save(e.getProfile())
        })

        FXGL.getEventBus().addEventHandler(LoadEvent.ANY, EventHandler { e ->
            settings.load(e.getProfile())
        })
    }

    private fun runPreInit() {
        log.debug("Running preInit()")

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

    private fun generateDefaultProfile() {
        log.debug("generateDefaultProfile()")

        defaultProfile = createProfile()
    }

    private fun loop(tpf: Double) {
        stateMachine.onUpdate(tpf)
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

    // GAME CONTROLLER CALLBACKS

    override fun startNewGame() {
        log.debug("Starting new game")
        loadState.dataFile = DataFile.EMPTY
        stateMachine.startLoad()
    }

    private fun startLoadedGame(dataFile: DataFile) {
        log.debug("Starting loaded game")
        loadState.dataFile = dataFile
        stateMachine.startLoad()
    }

    override fun gotoMainMenu() {
        stateMachine.startMainMenu()
    }

    override fun gotoGameMenu() {
        stateMachine.startGameMenu()
    }

    override fun gotoPlay() {
        stateMachine.startPlay()
    }

    override fun saveGame(fileName: String) {
        doSave(fileName)
    }

    private fun doSave(saveFileName: String) {
        val dataFile = app.saveState()
        val saveFile = SaveFile(saveFileName, LocalDateTime.now())

        saveLoadManager
                .saveTask(dataFile, saveFile)
                //.onSuccess { hasSaves.value = true }
                .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.savingData")+": $saveFileName"))
    }

    override fun loadGame(saveFile: SaveFile) {
        saveLoadManager
                .loadTask(saveFile)
                .onSuccess { startLoadedGame(it) }
                .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.loading")+": ${saveFile.name}"))
    }

    override fun loadGameFromLastSave() {
        saveLoadManager
                .loadLastModifiedSaveFileTask()
                .then { saveLoadManager.loadTask(it) }
                .onSuccess { startLoadedGame(it) }
                .runAsyncFXWithDialog(ProgressDialog(FXGL.getLocalizedString("menu.loading")+"..."))
    }

    override fun saveScreenshot(): Boolean {
        return mainWindow.saveScreenshot()
    }

    override fun fixAspectRatio() {
        mainWindow.fixAspectRatio()
    }

    override fun saveProfile() {
        saveLoadManager.saveProfileTask(createProfile())
                .onFailure { error -> "Failed to save profile: ${profileName.value} - $error" }
                .run() // we execute synchronously to avoid incomplete save since we might be shutting down
    }

    /**
     * @return true if loaded successfully, false if couldn't load
     */
    override fun loadFromProfile(profile: UserProfile): Boolean {
        if (!profile.isCompatible(FXGL.getSettings().title, FXGL.getSettings().version))
            return false

        FXGL.getEventBus().fireEvent(LoadEvent(LoadEvent.LOAD_PROFILE, profile))
        return true
    }

    override fun profileNameProperty(): StringProperty {
        return profileName
    }

    override fun restoreDefaultProfileSettings() {
        log.debug("restoreDefaultSettings()")

        FXGL.getEventBus().fireEvent(LoadEvent(LoadEvent.RESTORE_SETTINGS, defaultProfile))
    }

    /**
     * @return user profile with current settings
     */
    private fun createProfile(): UserProfile {
        log.debug("Creating default profile")

        val profile = UserProfile(FXGL.getSettings().title, FXGL.getSettings().version)

        FXGL.getEventBus().fireEvent(SaveEvent(profile))

        return profile
    }

    override fun exit() {
        log.debug("Exiting FXGL")

        if (settings.isMenuEnabled) {
            //menuHandler.saveProfile()
        }

        log.debug("Shutting down background threads")
        executor.shutdownNow()

        saveSystemData()

        log.debug("Closing logger and exiting JavaFX")

        Logger.close()
        javafx.application.Platform.exit()
    }
}