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
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.IOTask
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.core.util.PauseMenuBGGen
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.dsl.animationBuilder
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.input.InputSequence
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.localization.LocalizationService
import com.almasb.fxgl.logging.Logger
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.profile.DataFile
import com.almasb.fxgl.profile.SaveLoadHandler
import com.almasb.fxgl.profile.SaveLoadService
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SceneListener
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.texture.toImage
import com.almasb.fxgl.time.Timer
import com.almasb.fxgl.ui.DialogService
import com.almasb.fxgl.ui.FontType
import javafx.application.Application
import javafx.beans.property.SimpleDoubleProperty
import javafx.concurrent.Task
import javafx.embed.swing.SwingFXUtils
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
import javax.imageio.ImageIO
import kotlin.system.measureNanoTime

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

class FXGLApplication : Application() {

    companion object {
        private val log = Logger.get(FXGLApplication::class.java)

        lateinit var app: GameApplication
        private lateinit var settings: ReadOnlyGameSettings

        private lateinit var engine: Engine
        private lateinit var mainWindow: MainWindow

        @JvmStatic fun launchFX(app: GameApplication, settings: ReadOnlyGameSettings, args: Array<String>) {
            this.app = app
            this.settings = settings

            Application.launch(FXGLApplication::class.java, *args)
        }

        @JvmStatic fun customLaunchFX(app: GameApplication, settings: ReadOnlyGameSettings, stage: Stage) {
            this.app = app
            this.settings = settings

            FXGLApplication().start(stage)
        }
    }

    /**
     * This is the main entry point as run by the JavaFX platform.
     */
    override fun start(stage: Stage) {

        // any exception on the JavaFX thread will be caught and reported
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            handleFatalError(e)
        }

        log.debug("Initializing FXGL")

        engine = Engine(settings)

        // after this call, all FXGL.* calls (apart from those accessing services) are valid
        FXGL.inject(engine, app, this)

        val startupScene = settings.sceneFactory.newStartup()

        // get window up ASAP
        mainWindow = MainWindow(stage, startupScene, settings)
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
                .onSuccess { engine.startLoop() }
                .onFailure { handleFatalError(it) }
                .toJavaFXTask()

        Async.execute(task)
    }

    private fun postServicesInit() {
        initPauseResumeHandler()
        initSaveLoadHandler()
        initAndLoadLocalization()
        initAndRegisterFontFactories()

        // onGameUpdate is only updated in Game Scene
        FXGL.getGameScene().addListener(object : SceneListener {
            override fun onUpdate(tpf: Double) {
                engine.onGameUpdate(tpf)
            }
        })
    }

    private fun initPauseResumeHandler() {
        if (!settings.isMobile) {
            mainWindow.iconifiedProperty().addListener { _, _, isMinimized ->
                if (isMinimized) {
                    engine.pauseLoop()
                } else {
                    engine.resumeLoop()
                }
            }
        }
    }

    private fun initSaveLoadHandler() {
        FXGL.getSaveLoadService().addHandler(object : SaveLoadHandler {
            override fun onSave(data: DataFile) {
                var bundle = Bundle("FXGLServices")
                engine.write(bundle)

                data.putBundle(bundle)
            }

            override fun onLoad(data: DataFile) {
                var bundle = data.getBundle("FXGLServices")
                engine.read(bundle)
            }
        })
    }

    private fun initAndLoadLocalization() {
        log.debug("Loading localizations");

        settings.supportedLanguages.forEach { lang ->
            val pMap = FXGL.getAssetLoader().loadPropertyMap("languages/" + lang.name.toLowerCase() + ".lang")
            FXGL.getLocalizationService().addLanguageData(lang, pMap.toStringMap())
        }

        FXGL.getLocalizationService().selectedLanguageProperty().bind(settings.language)
    }

    private fun initAndRegisterFontFactories() {
        log.debug("Registering font factories with UI factory")

        val uiFactory = FXGL.getUIFactoryService()

        uiFactory.registerFontFactory(FontType.UI, FXGL.getAssetLoader().loadFont(settings.fontUI))
        uiFactory.registerFontFactory(FontType.GAME, FXGL.getAssetLoader().loadFont(settings.fontGame))
        uiFactory.registerFontFactory(FontType.MONO, FXGL.getAssetLoader().loadFont(settings.fontMono))
        uiFactory.registerFontFactory(FontType.TEXT, FXGL.getAssetLoader().loadFont(settings.fontText))
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

        if (mainWindow != null) {
            mainWindow.showFatalError(error, Runnable {
                exitFXGL()
            })
        } else {
            exitFXGL()
        }
    }

    fun exitFXGL() {
        log.debug("Exiting FXGL");

        if (engine != null && !isError)
            engine.stopLoopAndExitServices();

        log.debug("Shutting down background threads");
        Async.shutdownNow();

        log.debug("Closing logger and exiting JavaFX");
        Logger.close();
        javafx.application.Platform.exit();
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

        override val appWidth
            get() = settings.width

        override val appHeight
            get() = settings.height

        /**
         * Always-on timer.
         */
        override val timer = Timer()

        internal lateinit var gameScene: GameScene
        private lateinit var loadScene: LoadingScene

        private var intro: FXGLScene? = null
        private var mainMenu: SubScene? = null
        private var gameMenu: SubScene? = null

        internal val window: MainWindow
            get() = mainWindow

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
            gameScene = GameScene(settings.width, settings.height,
                    GameWorld(),
                    PhysicsWorld(settings.height, settings.pixelsPerMeter)
            )

            gameScene.isSingleStep = settings.isSingleStep

            if (settings.isClickFeedbackEnabled) {
                addClickFeedbackHandler()
            }

            if (settings.isIntroEnabled) {
                intro = sceneFactory.newIntro()
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

                            } else if (mainWindow.currentScene === gameScene) {
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

                gameScene.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
                gameMenu!!.input.addEventHandler(KeyEvent.ANY, menuKeyHandler)
            }

            log.debug("Application scenes initialized")
        }

        private fun addClickFeedbackHandler() {
            gameScene.input.addEventHandler(MouseEvent.MOUSE_PRESSED, EventHandler {
                val circle = Circle(gameScene.input.mouseXUI, gameScene.input.mouseYUI, 5.0, null)
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

            if (!settings.isExperimentalNative) {
                mainWindow.addIcons(assetLoaderService.loadImage(settings.appIcon))
                mainWindow.defaultCursor = ImageCursor(assetLoaderService.loadCursorImage("fxgl_default.png"), 7.0, 6.0)
            }

            SystemActions.bind(mainWindow.input)

            mainWindow.input.addAction(object : UserAction("") {
                private val subScene = object : SubScene() {
                    private val view by lazy {
                        ImageView(PauseMenuBGGen.generate().toImage()).also {
                            it.scaleX = 4.0
                            it.scaleY = 4.0
                            it.translateX = appWidth / 2.0
                            it.translateY = appHeight / 2.0
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

            gameScene.reset()
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

        override fun onGameReady(vars: PropertyMap) {
        }

        fun gotoIntro() {
            mainWindow.setScene(intro!!)
        }

        private val dummyScene by lazy {
            object : FXGLScene() {}
        }

        fun gotoMainMenu() {
            // since mainMenu is a subscene we need an actual scene before it
            mainWindow.setScene(dummyScene)
            mainWindow.pushState(mainMenu!!)
        }

        fun gotoGameMenu() {
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
            mainWindow.setScene(gameScene)
        }

        /**
         * Saves a screenshot of the current scene into a ".png" file,
         * named by title + version + time.
         */
        fun saveScreenshot(): Boolean {
            val fxImage = mainWindow.takeScreenshot()
            val img = SwingFXUtils.fromFXImage(fxImage, null)

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



