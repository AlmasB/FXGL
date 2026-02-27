/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.Interpolators
import com.almasb.fxgl.app.scene.FXGLScene
import com.almasb.fxgl.app.scene.GameScene
import com.almasb.fxgl.app.scene.LoadingScene
import com.almasb.fxgl.app.services.FXGLAssetLoaderService
import com.almasb.fxgl.core.Updatable
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.util.PauseMenuBGGen
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.animationBuilder
import com.almasb.fxgl.dsl.getGameController
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputSequence
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.localization.LocalizationService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.profile.DataFile
import com.almasb.fxgl.profile.SaveLoadHandler
import com.almasb.fxgl.profile.SaveLoadService
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.texture.toBufferedImage
import com.almasb.fxgl.texture.toImage
import com.almasb.fxgl.time.Timer
import com.almasb.fxgl.ui.DialogService
import com.almasb.fxgl.ui.FontType
import com.gluonhq.attach.lifecycle.LifecycleEvent
import com.gluonhq.attach.lifecycle.LifecycleService
import javafx.application.Application
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.ImageCursor
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode.*
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Stage
import javafx.util.Duration
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO
import kotlin.collections.HashMap
import kotlin.system.measureNanoTime

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class FXGLApplication : Application() {

    companion object {
        private val log = Logger.get(FXGLApplication::class.java)

        private lateinit var app: GameApplication
        private lateinit var settings: ReadOnlyGameSettings

        private lateinit var engine: Engine
        private lateinit var mainWindow: MainWindow

        @JvmStatic fun launchFX(app: GameApplication, settings: ReadOnlyGameSettings, args: Array<String>) {
            this.app = app
            this.settings = settings

            Application.launch(FXGLApplication::class.java, *args)
        }

        @JvmStatic fun embeddedLaunchFX(app: GameApplication, settings: ReadOnlyGameSettings): FXGLPane {
            this.app = app
            this.settings = settings

            return FXGLApplication().embeddedStart()
        }
    }

    /**
     * This is the main entry point when run inside an existing JavaFX application.
     */
    private fun embeddedStart(): FXGLPane {
        val pane = FXGLPane(settings.width.toDouble(), settings.height.toDouble())

        startImpl {
            EmbeddedPaneWindow(pane, settings.sceneFactory.newStartup(settings.width, settings.height), settings)
        }

        return pane
    }

    /**
     * This is the main entry point as run by the JavaFX platform.
     */
    override fun start(stage: Stage) {
        startImpl {
            PrimaryStageWindow(stage, settings.sceneFactory.newStartup(settings.width, settings.height), settings)
        }
    }

    private fun startImpl(windowSupplier: () -> MainWindow) {
        // any exception on the JavaFX thread will be caught and reported
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            handleFatalError(e)
        }

        log.debug("Initializing FXGL")

        engine = Engine(settings)

        // after this call, all FXGL.* calls (apart from those accessing services) are valid
        FXGL.inject(engine, app, this)

        // get window up ASAP
        mainWindow = windowSupplier()
        mainWindow.show()

        // start initialization of services on a background thread
        // then start the loop on the JavaFX thread
        val task = IOTask.ofVoid {
            val time = measureNanoTime {
                engine.initServices()
                postServicesInit()
            }

            log.infof("FXGL initialization took: %.3f sec", time / 1000000000.0)
        }
                .onSuccess {
                    engine.startLoop()
                    setFirstSceneAfterStartup()
                }
                .onFailure { handleFatalError(it) }
                .toJavaFXTask()

        Async.execute(task)
    }

    private fun postServicesInit() {
        // fonts take a (relatively) long time to load, so load them in parallel
        Async.startAsync {
            log.debug("Loading fonts")

            val uiFactory = FXGL.getUIFactoryService()

            val fontUI = FXGL.getAssetLoader().loadFont(settings.fontUI)
            val fontGame = FXGL.getAssetLoader().loadFont(settings.fontGame)
            val fontMono = FXGL.getAssetLoader().loadFont(settings.fontMono)
            val fontText = FXGL.getAssetLoader().loadFont(settings.fontText)

            // but register them on the JavaFX thread
            Async.startAsyncFX {
                log.debug("Registering font factories with UI factory")

                uiFactory.registerFontFactory(FontType.UI, fontUI)
                uiFactory.registerFontFactory(FontType.GAME, fontGame)
                uiFactory.registerFontFactory(FontType.MONO, fontMono)
                uiFactory.registerFontFactory(FontType.TEXT, fontText)
            }
        }

        initPauseResumeHandler()
        initSaveLoadHandler()
        initAndLoadLocalization()

        // onGameUpdate is only updated in Game Scene
        FXGL.getGameScene().addListener(Updatable { tpf -> engine.onGameUpdate(tpf) })
    }

    private fun initPauseResumeHandler() {
        if (settings.isMobile) {
            initPauseResumeHandlerMobile()
        } else {
            initPauseResumeHandlerDesktop()
        }
    }

    private fun initPauseResumeHandlerMobile() {
        val serviceWrapper = LifecycleService.create()

        if (serviceWrapper.isEmpty) {
            log.warning("Attach LifecycleService is not present")
        } else {
            val service = serviceWrapper.get()

            log.debug("Init pause/resume handlers via Attach LifecycleService")

            service.addListener(LifecycleEvent.PAUSE) {
                engine.pauseLoop()
            }

            service.addListener(LifecycleEvent.RESUME) {
                engine.resumeLoop()
            }
        }
    }

    private fun initPauseResumeHandlerDesktop() {
        mainWindow.iconifiedProperty().addListener { _, _, isMinimized ->
            if (isMinimized) {
                engine.pauseLoop()
            } else {
                engine.resumeLoop()
            }
        }
    }

    private fun initSaveLoadHandler() {
        FXGL.getSaveLoadService().addHandler(object : SaveLoadHandler {
            override fun onSave(data: DataFile) {
                val bundle = Bundle("FXGLServices")
                engine.write(bundle)

                data.putBundle(bundle)
            }

            override fun onLoad(data: DataFile) {
                val bundle = data.getBundle("FXGLServices")
                engine.read(bundle)
            }
        })
    }

    private fun initAndLoadLocalization() {
        log.debug("Loading default localization")

        val defaultLang = settings.language.value

        val langData = FXGL.getAssetLoader().loadPropertyMap("languages/" + defaultLang.name.lowercase() + ".lang")

        FXGL.getLocalizationService().addLanguageData(defaultLang, langData.toStringMap())

        settings.supportedLanguages.filter { it != defaultLang }.forEach { lang ->
            FXGL.getLocalizationService().addLanguageDataLazy(lang) {
                FXGL.getAssetLoader()
                        .loadPropertyMap("languages/" + lang.name.lowercase() + ".lang")
                        .toStringMap()
            }
        }

        FXGL.getLocalizationService().selectedLanguageProperty().bind(settings.language)
    }

    private fun setFirstSceneAfterStartup() {
        // Start -> (Intro) -> (Menu) -> Game
        if (settings.isIntroEnabled) {
            getGameController().gotoIntro()
        } else {
            if (settings.isMainMenuEnabled) {
                getGameController().gotoMainMenu()
            } else {
                getGameController().startNewGame()
            }
        }
    }

    private var isError = false

    private fun handleFatalError(error: Throwable) {
        if (isError) {
            // just ignore to avoid spamming dialogs
            return
        }

        isError = true

        // stop main loop from running as we cannot continue
        engine.stopLoop()

        log.fatal("Uncaught Exception:", error)
        log.fatal("Application will now exit")

        mainWindow.showFatalError(error, Runnable {
            exitFXGL()
        })
    }

    fun exitFXGL() {
        log.debug("Exiting FXGL")

        if (!isError) {
            engine.stopLoopAndExitServices()
            app.onExit()
        }

        Async.shutdownNow()

        mainWindow.close()

        Logger.close()
    }

    /**
     * Clears previous game.
     * Initializes game, physics and UI.
     * This task is rerun every time the game application is restarted.
     */
    class InitAppTask : Task<Void?>() {

        override fun call(): Void? {
            val time = measureNanoTime {

                log.debug("Initializing game");
                updateMessage("Initializing game");

                initGame();
                app.initPhysics();
                app.initUI();

                engine.onGameReady(FXGL.getWorldProperties());
            }

            log.infof("Game initialization took: %.3f sec", time / 1000000000.0);

            return null
        }

        private fun initGame() {
            val vars = HashMap<String, Any>()
            app.initGameVars(vars)

            vars.forEach { name, value ->
                FXGL.getWorldProperties().setValue(name, value)
            }

            engine.onVarsInitialized(FXGL.getWorldProperties())

            app.initGame();
        }

        override fun failed() {
            throw RuntimeException("Initialization failed", exception)
        }
    }

    class GameApplicationService : SceneService() {

        private val log = Logger.get(javaClass)

        private lateinit var assetLoaderService: FXGLAssetLoaderService
        private lateinit var saveLoadService: SaveLoadService

        private lateinit var localService: LocalizationService
        private lateinit var dialogService: DialogService

        /**
         * The root for the overlay group that is constantly visible and on top
         * of every other UI element. For things like notifications.
         */
        override val overlayRoot = Group()

        override fun prefWidthProperty(): ReadOnlyDoubleProperty = settings.prefWidthProperty()

        override fun prefHeightProperty(): ReadOnlyDoubleProperty = settings.prefHeightProperty()

        override val eventBus = EventBus()

        override val input: Input
            get() = mainWindow.input

        /**
         * Always-on timer.
         */
        override val timer = Timer()

        override val worldProperties: PropertyMap
            get() = gameScene.gameWorld.properties

        override val currentScene: Scene
            get() = mainWindow.currentScene

        private lateinit var gameSceneRef: GameScene
        private lateinit var loadScene: LoadingScene

        private var intro: FXGLScene? = null
        private var mainMenu: SubScene? = null
        private var gameMenu: SubScene? = null

        override val introScene: Optional<Scene>
            get() = Optional.ofNullable(intro)

        override val loadingScene: Optional<Scene>
            get() = Optional.ofNullable(loadScene)

        override val mainMenuScene: Optional<Scene>
            get() = Optional.ofNullable(mainMenu)

        override val gameMenuScene: Optional<Scene>
            get() = Optional.ofNullable(gameMenu)

        override val gameScene: GameScene
            get() = gameSceneRef

        internal val window: MainWindow
            get() = mainWindow

        override fun isInHierarchy(scene: Scene): Boolean {
            return mainWindow.isInHierarchy(scene)
        }

        override fun onInit() {
            settings.cssList.forEach {
                log.debug("Applying CSS: $it")
                mainWindow.addCSS(assetLoaderService.loadCSS(it))
            }

            mainWindow.onClose = {
                if (settings.isCloseConfirmation) {
                    if (canShowCloseDialog()) {
                        showConfirmExitDialog()
                    }
                } else {
                    FXGL.getGameController().exit()
                }
            }

            mainWindow.currentSceneProperty.addListener { _, oldScene, newScene ->
                log.debug("Removing overlay from $oldScene and adding to $newScene")

                removeOverlay(oldScene)
                addOverlay(newScene)
            }

            initAppScenes()
        }

        override fun onGameUpdate(tpf: Double) {
            app.onUpdate(tpf)
        }

        override fun onUpdate(tpf: Double) {
            timer.update(tpf)
            mainWindow.update(tpf)
        }

        private fun initAppScenes() {
            log.debug("Initializing application scenes")

            val sceneFactory = settings.sceneFactory

            loadScene = sceneFactory.newLoadingScene()
            gameSceneRef = GameScene(settings.width, settings.height,
                    GameWorld(),
                    PhysicsWorld(settings.height, settings.pixelsPerMeter, settings.collisionDetectionStrategy),
                    settings.is3D
            )

            gameSceneRef.isSingleStep = settings.isSingleStep

            if (settings.isClickFeedbackEnabled) {
                addClickFeedbackHandler()
            }

            if (settings.isIntroEnabled) {
                intro = sceneFactory.newIntro()
                        .also {
                            it.onFinished = Runnable {
                                setSceneAfterIntro()
                            }
                        }
            }

            if (settings.isMainMenuEnabled) {
                mainMenu = sceneFactory.newMainMenu()
            }

            if (settings.isGameMenuEnabled) {
                gameMenu = sceneFactory.newGameMenu()

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
                                popSubScene()

                            } else if (mainWindow.currentScene === gameSceneRef) {
                                canSwitchGameMenu = false
                                pushSubScene(gameMenu!!)
                            }
                        }
                    }

                    override fun handle(event: KeyEvent) {
                        if (event.code == settings.menuKey) {
                            onMenuKey(event.eventType == KeyEvent.KEY_PRESSED)
                        }
                    }
                }

                gameSceneRef.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
                gameMenu!!.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
            }

            log.debug("Application scenes initialized")
        }

        private fun setSceneAfterIntro() {
            if (settings.isMainMenuEnabled) {
                getGameController().gotoMainMenu()
            } else {
                getGameController().startNewGame()
            }
        }

        private fun addClickFeedbackHandler() {
            gameSceneRef.input.addEventHandler(MouseEvent.MOUSE_PRESSED, EventHandler {
                val circle = Circle(gameSceneRef.input.mouseXUI, gameSceneRef.input.mouseYUI, 5.0, null)
                circle.stroke = Color.GOLD
                circle.strokeWidth = 2.0
                circle.opacityProperty().bind(SimpleDoubleProperty(1.0).subtract(circle.radiusProperty().divide(35.0)))

                overlayRoot.children += circle

                animationBuilder()
                        .interpolator(Interpolators.SMOOTH.EASE_IN())
                        .onFinished(Runnable { overlayRoot.children -= circle })
                        .duration(Duration.seconds(0.33))
                        .animate(circle.radiusProperty())
                        .to(35.0)
                        .buildAndPlay()
            })
        }

        override fun onMainLoopStarting() {
            // these things need to be called early before the main loop
            // so that menus can correctly display input controls, etc.
            app.initInput()
            app.onPreInit()

            if (!settings.isNative) {
                mainWindow.addIcons(assetLoaderService.loadImage(settings.appIcon))

                val cursorInfo = settings.defaultCursor

                mainWindow.defaultCursor = ImageCursor(
                        assetLoaderService.loadImage(cursorInfo.imageName),
                        cursorInfo.hotspotX,
                        cursorInfo.hotspotY
                )
            }

            SystemActions.bind(mainWindow.input)

            mainWindow.input.addAction(object : UserAction("") {
                private val subScene = object : SubScene() {
                    private val view by lazy {
                        ImageView(PauseMenuBGGen.generate().toImage()).also {
                            it.scaleX = 4.0
                            it.scaleY = 4.0
                            it.translateX = prefWidth / 2.0
                            it.translateY = prefHeight / 2.0
                        }
                    }

                    override fun onCreate() {
                        contentRoot.children.setAll(view)

                        timer.runOnceAfter({ popSubScene() }, Duration.seconds(3.0))
                    }
                }
                override fun onActionBegin() {
                    pushSubScene(subScene)
                }
            }, InputSequence(F, X, G, L, A, L, M, A, S, B))
        }

        private fun addOverlay(scene: Scene) {
            if (scene is FXGLScene) {
                scene.contentRoot.children += overlayRoot
            } else {
                scene.root.children += overlayRoot
            }
        }

        private fun removeOverlay(scene: Scene) {
            if (scene is FXGLScene) {
                scene.contentRoot.children -= overlayRoot
            } else {
                scene.root.children -= overlayRoot
            }
        }

        /**
         * @return true if can show close dialog
         */
        private fun canShowCloseDialog(): Boolean {
            // do not allow close dialog if
            // 1. a dialog is shown
            // 2. we are loading a game
            // 3. we are showing intro

            // mainWindow.currentScene === dialogScene ||
            val isNotOK =  mainWindow.currentScene === loadScene
                    || (settings.isIntroEnabled && mainWindow.currentScene === intro)

            return !isNotOK
        }

        private fun showConfirmExitDialog() {
            dialogService.showConfirmationBox(localService.getLocalizedString("dialog.exitGame")) { yes ->
                if (yes)
                    FXGL.getGameController().exit()
            }
        }

        fun startNewGame() {
            log.debug("Starting new game")

            mainWindow.setScene(loadScene)

            clearPreviousGame()

            loadScene.pushNewTask(InitAppTask())
        }

        private fun clearPreviousGame() {
            log.debug("Clearing previous game")

            gameSceneRef.reset()
            engine.resetServices()
        }

        fun saveGame(dataFile: DataFile) {
            saveLoadService.save(dataFile)
        }

        fun loadGame(dataFile: DataFile) {
            log.debug("Starting loaded game")
            mainWindow.setScene(loadScene)

            clearPreviousGame()

            loadScene.pushNewTask(Runnable {
                InitAppTask().run()
                saveLoadService.load(dataFile)
            })
        }

        fun gotoIntro() {
            mainWindow.setScene(intro!!)
        }

        private val dummyScene by lazy {
            object : FXGLScene() {
                override fun toString(): String {
                    return "FXGLMainMenuDummyScene"
                }
            }
        }

        fun gotoMainMenu() {
            if (!settings.isMainMenuEnabled) {
                log.warning("Ignoring gotoMainMenu() because main menu is not enabled")
                return
            }

            // since mainMenu is a subscene we need an actual scene before it
            mainWindow.setScene(dummyScene)
            mainWindow.pushState(mainMenu!!)
        }

        fun gotoGameMenu() {
            if (!settings.isGameMenuEnabled) {
                log.warning("Ignoring gotoGameMenu() because game menu is not enabled")
                return
            }

            // current scene should be gameScene
            mainWindow.pushState(gameMenu!!)
        }

        fun gotoLoading(loadingTask: Runnable) {
            loadScene.pushNewTask(loadingTask)
            mainWindow.setScene(loadScene)
        }

        fun gotoLoading(loadingTask: Task<*>) {
            loadScene.pushNewTask(loadingTask)
            mainWindow.setScene(loadScene)
        }

        fun gotoPlay() {
            mainWindow.setScene(gameSceneRef)
        }

        /**
         * Saves a screenshot of the current scene into a ".png" file,
         * named by title + version + time.
         */
        fun saveScreenshot(): Boolean {
            val fxImage = mainWindow.takeScreenshot()
            val img = toBufferedImage(fxImage)

            var fileName = "./" + settings.title + settings.version + LocalDateTime.now()
            fileName = fileName.replace(":", "_")

            try {
                val name = if (fileName.endsWith(".png")) fileName else "$fileName.png"

                Files.newOutputStream(Paths.get(name)).use {
                    return ImageIO.write(img, "png", it)
                }
            } catch (e: Exception) {
                log.warning("saveScreenshot($fileName.png) failed: $e")
                return false
            }
        }

        override fun pushSubScene(subScene: SubScene) {
            mainWindow.pushState(subScene)
        }

        override fun popSubScene() {
            mainWindow.popState()
        }
    }
}



