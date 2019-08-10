package com.almasb.fxgl.app

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.ObjectMap
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.FXGLExecutor
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.local.Local
import com.almasb.fxgl.core.reflect.ReflectionUtils.findFieldsByAnnotation
import com.almasb.fxgl.core.reflect.ReflectionUtils.inject
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.dev.DevPane
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.gameplay.GameState
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.io.FS
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.saving.*
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SceneListener
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.time.Timer
import com.almasb.fxgl.ui.Display
import com.almasb.fxgl.ui.ErrorDialog
import com.almasb.fxgl.ui.FXGLUIConfig
import com.almasb.fxgl.ui.FontType
import com.almasb.sslogger.Logger
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import java.time.LocalDateTime

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

    /**
     * @return true iff FXGL is running for the first time
     * @implNote we actually check if "system/" exists in running dir, so if it was
     *            deleted, then this method also returns true
     */
    private var isFirstRun: Boolean = false

    internal lateinit var bundle: Bundle

    private lateinit var mainWindow: MainWindow
    internal lateinit var playState: GameScene

    private lateinit var loadState: LoadingScene
    private lateinit var dialogState: DialogSubState
    private var intro: FXGLScene? = null
    private var mainMenu: FXGLScene? = null
    private var gameMenu: FXGLScene? = null

    private var pauseMenu: PauseMenu? = null

    private val loop = LoopRunner { loop(it) }

    val tpf: Double
        get() = loop.tpf

    /* SUBSYSTEMS */

    private val services = arrayListOf<EngineService>()
    private val servicesCache = ObjectMap<Class<out EngineService>, EngineService>()

    fun addService(engineService: EngineService) {
        log.debug("Adding new engine service: ${engineService.javaClass}")

        services += engineService
    }

    inline fun <reified T : EngineService> getService(serviceClass: Class<T>): T {
        if (servicesCache.containsKey(serviceClass))
            return servicesCache[serviceClass] as T

        return (services.find { it is T  }?.also { servicesCache.put(serviceClass, it) }
                ?: throw IllegalArgumentException("Engine does not have service: $serviceClass")) as T
    }

    internal val assetLoader by lazy { AssetLoader() }
    internal val eventBus by lazy { EventBus() }
    internal val display by lazy { dialogState as Display }
    internal val executor by lazy { FXGLExecutor() }
    internal val fs by lazy { FS(settings.isDesktop) }

    internal val devPane by lazy { DevPane(playState, settings) }

    /**
     * The 'always on' engine timer.
     */
    internal val engineTimer = Timer()

    /**
     * The root for the overlay group that is constantly visible and on top
     * of every other UI element. For things like notifications.
     */
    private val overlayRoot = Group()

    private val profileName = SimpleStringProperty("no-profile")

    /**
     * Stores the default profile data. This is used to restore default settings.
     */
    private lateinit var defaultProfile: UserProfile

    private lateinit var saveLoadManager: SaveLoadManager

    private val environmentVars = hashMapOf<String, Any>()

    init {
        log.debug("Initializing FXGL")

        logVersion()

        environmentVars["overlayRoot"] = overlayRoot
        environmentVars["masterTimer"] = engineTimer
        environmentVars["eventBus"] = eventBus
        environmentVars["sceneStack"] = this
    }

    private fun logVersion() {
        val jVersion = System.getProperty("java.version", "?")
        val fxVersion = System.getProperty("javafx.version", "?")

        val version = settings.runtimeInfo.version
        val build = settings.runtimeInfo.build

        log.info("FXGL-$version ($build) on ${settings.platform} (J:$jVersion FX:$fxVersion)")
        log.info("Source code and latest versions at: https://github.com/AlmasB/FXGL")
        log.info("             Join the FXGL chat at: https://gitter.im/AlmasB/FXGL")
    }

    fun startLoop() {
        saveLoadManager = SaveLoadManager(profileName.value)

        val start = System.nanoTime()

        loadLocalization()

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

            isFirstRun = !fs.exists("system/")

            if (!settings.isExperimentalNative) {
                if (isFirstRun) {
                    createRequiredDirs()
                    loadDefaultSystemData()
                } else {
                    loadSystemData()
                }
            } else {
                loadDefaultSystemData()
            }

            initAppScenes()

            attachPauseResumeListener()
            attachEventHandlers()

            // finish init on FX thread
            Async.startFX {
                // these things need to be called early before the main loop
                // so that menus can correctly display input controls, etc.
                // this is called once per application lifetime
                runPreInit()

                addOverlay(startupScene)

                mainWindow.currentSceneProperty.addListener { _, oldScene, newScene ->
                    log.debug("Removing overlay from $oldScene and adding to $newScene")

                    removeOverlay(oldScene)
                    addOverlay(newScene)
                }

                settings.javaClass.declaredMethods.filter { it.name.startsWith("is") || it.name.startsWith("get") || it.name.endsWith("Property") }.forEach {
                    environmentVars[it.name.removePrefix("get").decapitalize()] = it.invoke(settings)
                }

                log.debug("Logging environment variables")

                environmentVars.forEach { (key, value) ->
                    log.debug("$key: $value")
                }

                services.forEach { service ->
                    findFieldsByAnnotation(service, Inject::class.java).forEach { field ->
                        val injectKey = field.getDeclaredAnnotation(Inject::class.java).value

                        if (injectKey !in environmentVars) {
                            throw IllegalArgumentException("Cannot inject @Inject($injectKey). No value present for $injectKey")
                        }

                        inject(field, service, environmentVars[injectKey])
                    }
                }

                services.forEach { it.onMainLoopStarting() }

                app.onPreInit()

                log.infof("FXGL initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0)

                loop.start()
            }
        }
    }

    private fun addOverlay(scene: Scene) {
        scene.root.children += overlayRoot
    }

    private fun removeOverlay(scene: Scene) {
        scene.root.children -= overlayRoot
    }

    private fun loadLocalization() {
        log.debug("Loading localizations")

        val builtInLangs = listOf("english", "french", "german", "russian", "hungarian")

        builtInLangs.forEach {
            Local.addLanguage(it, assetLoader.loadResourceBundle("languages/$it.properties"))
        }

        settings.language.value = Local.languages.find { it.name == "english" }

        Local.selectedLanguageProperty().bind(settings.language)
    }

    private fun attachPauseResumeListener() {
        if (settings.isMobile) {
            // no-op
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

    private fun initAppScenes() {
        log.debug("Initializing application scenes")

        val sceneFactory = settings.sceneFactory

        loadState = sceneFactory.newLoadingScene()
        playState = GameScene(settings.width, settings.height,
                GameState(),
                GameWorld(),
                PhysicsWorld(settings.height, settings.pixelsPerMeter)
        )

        playState.isSingleStep = settings.isSingleStep

        // app is only updated in Game Scene
        playState.addListener(object : SceneListener {
            override fun onUpdate(tpf: Double) {
                app.onUpdate(tpf)
            }
        })

        // we need dialog state before intro and menus
        dialogState = DialogSubState(mainWindow.currentFXGLSceneProperty)

        if (settings.isIntroEnabled) {
            intro = sceneFactory.newIntro()
        }

        if (settings.isMenuEnabled) {
            mainMenu = sceneFactory.newMainMenu()
            gameMenu = sceneFactory.newGameMenu()
        }

        if (settings.isMenuEnabled) {
            val menuKeyHandler = object : EventHandler<KeyEvent> {
                private var canSwitchGameMenu = true

                private fun onMenuKey(pressed: Boolean) {
                    if (!pressed) {
                        canSwitchGameMenu = true
                        return
                    }

                    if (canSwitchGameMenu) {
                        // we only care if menu key was pressed in one of these states
                        if (mainWindow.currentScene === gameMenu) {
                            canSwitchGameMenu = false
                            gotoPlay()

                        } else if (mainWindow.currentScene === playState) {
                            canSwitchGameMenu = false
                            gotoGameMenu()
                        }
                    }
                }

                override fun handle(event: KeyEvent) {
                    if (event.code == settings.menuKey) {
                        onMenuKey(event.eventType == KeyEvent.KEY_PRESSED)
                    }
                }
            }

            playState.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
            gameMenu!!.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
        } else {

            pauseMenu = sceneFactory.newPauseMenu()

            playState.input.addAction(object : UserAction("Pause") {
                override fun onActionBegin() {
                    pauseMenu!!.requestShow {
                        mainWindow.pushState(pauseMenu!!)
                    }
                }

                override fun onActionEnd() {
                    pauseMenu!!.unlockSwitch()
                }
            }, settings.menuKey)
        }

        log.debug("Application scenes initialized")
    }

    private fun createRequiredDirs() {
        fs.createDirectoryTask("system/")
                .then { fs.writeDataTask(listOf("This directory contains FXGL system data files."), "system/Readme.txt") }
                .onFailure { e ->
                    log.warning("Failed to create system dir: $e")
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e)
                }
                .run()
    }

    private fun saveSystemData() {
        log.debug("Saving FXGL system data")

        fs.writeDataTask(bundle, "system/fxgl.bundle")
                .onFailure { log.warning("Failed to save: $it") }
                .run()
    }

    private fun loadSystemData() {
        log.debug("Loading FXGL system data")

        fs.readDataTask<Bundle>("system/fxgl.bundle")
                .onSuccess {
                    bundle = it
                    log.debug("$bundle")
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
        eventBus.addEventHandler(SaveEvent.ANY, EventHandler { e ->
            settings.save(e.getProfile())
        })

        eventBus.addEventHandler(LoadEvent.ANY, EventHandler { e ->
            settings.load(e.getProfile())
        })
    }

    private fun runPreInit() {
        log.debug("Running preInit()")

        // 2. register user actions
        app.initInput()

        SystemActions.bind(playState.input)

        generateDefaultProfile()
    }

    private fun generateDefaultProfile() {
        log.debug("generateDefaultProfile()")

        defaultProfile = createProfile()
    }

    private fun loop(tpf: Double) {
        engineTimer.update(tpf)

        mainWindow.onUpdate(tpf)

        services.forEach { it.onUpdate(tpf) }
    }

    private var handledOnce = false

    private fun handleFatalError(error: Throwable) {
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
        mainWindow.setScene(loadState)
    }

    private fun startLoadedGame(dataFile: DataFile) {
        log.debug("Starting loaded game")
        loadState.dataFile = dataFile
        mainWindow.setScene(loadState)
    }

    override fun gotoIntro() {
        mainWindow.setScene(intro!!)
    }

    override fun gotoMainMenu() {
        mainWindow.setScene(mainMenu!!)
    }

    override fun gotoGameMenu() {
        mainWindow.setScene(gameMenu!!)
    }

    override fun gotoPlay() {
        mainWindow.setScene(playState)
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
                .runAsyncFXWithDialog(ProgressDialog(Local.getLocalizedString("menu.savingData") + ": $saveFileName"))
    }

    override fun loadGame(saveFile: SaveFile) {
        saveLoadManager
                .loadTask(saveFile)
                .onSuccess { startLoadedGame(it) }
                .runAsyncFXWithDialog(ProgressDialog(Local.getLocalizedString("menu.loading") + ": ${saveFile.name}"))
    }

    override fun loadGameFromLastSave() {
        saveLoadManager
                .loadLastModifiedSaveFileTask()
                .then { saveLoadManager.loadTask(it) }
                .onSuccess { startLoadedGame(it) }
                .runAsyncFXWithDialog(ProgressDialog(Local.getLocalizedString("menu.loading") + "..."))
    }

    override fun saveScreenshot(): Boolean {
        return mainWindow.saveScreenshot()
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
        if (!profile.isCompatible(settings.title, settings.version))
            return false

        eventBus.fireEvent(LoadEvent(LoadEvent.LOAD_PROFILE, profile))
        return true
    }

    override fun profileNameProperty(): StringProperty {
        return profileName
    }

    override fun restoreDefaultProfileSettings() {
        log.debug("restoreDefaultSettings()")

        eventBus.fireEvent(LoadEvent(LoadEvent.RESTORE_SETTINGS, defaultProfile))
    }

    /**
     * @return user profile with current settings
     */
    private fun createProfile(): UserProfile {
        log.debug("Creating default profile")

        val profile = UserProfile(settings.title, settings.version)

        eventBus.fireEvent(SaveEvent(profile))

        return profile
    }

    override fun pushSubScene(subScene: SubScene) {
        mainWindow.pushState(subScene)
    }

    override fun popSubScene() {
        mainWindow.popState()
    }

    override fun onGameReady(vars: PropertyMap) {
        services.forEach { it.onGameReady(vars) }
    }

    override fun exit() {
        log.debug("Exiting FXGL")

        services.forEach { it.onExit() }

        if (settings.isMenuEnabled) {
            //menuHandler.saveProfile()
        }

        log.debug("Shutting down background threads")
        executor.shutdownNow()

        if (!settings.isExperimentalNative) {
            saveSystemData()
        }

        log.debug("Closing logger and exiting JavaFX")

        Logger.close()
        javafx.application.Platform.exit()
    }
}