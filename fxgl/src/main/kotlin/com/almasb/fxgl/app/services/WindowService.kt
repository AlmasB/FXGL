/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.services

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.MainWindow
import com.almasb.fxgl.app.ReadOnlyGameSettings
import com.almasb.fxgl.app.SystemActions
import com.almasb.fxgl.app.scene.FXGLScene
import com.almasb.fxgl.app.scene.GameScene
import com.almasb.fxgl.app.scene.LoadingScene
import com.almasb.fxgl.app.scene.PauseMenu
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.dsl.FXGL
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.localization.LocalizationService
import com.almasb.fxgl.physics.PhysicsWorld
import com.almasb.fxgl.profile.DataFile
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SceneListener
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.scene.SubScene
import com.almasb.fxgl.time.Timer
import com.almasb.fxgl.ui.DialogService
import com.almasb.fxgl.logging.Logger
import javafx.concurrent.Task
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.ImageCursor
import javafx.scene.input.KeyEvent
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.imageio.ImageIO

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class WindowService : SceneService() {

    private val log = Logger.get(javaClass)

    @Inject("settings")
    lateinit var settings: ReadOnlyGameSettings

    @Inject("mainWindow")
    internal lateinit var mainWindow: MainWindow

    private lateinit var assetLoaderService: AssetLoaderService

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

//            pauseMenu = sceneFactory.newPauseMenu()
//
//            // pause menu can only be opened from game scene so it is fine to bind to its contentRoot X
//            pauseMenu!!.contentRoot.translateXProperty().bind(gameScene.contentRoot.translateXProperty())
//
//            gameScene.input.addAction(object : UserAction("Pause") {
//                override fun onActionBegin() {
//                    pauseMenu!!.requestShow {
//                        mainWindow.pushState(pauseMenu!!)
//                    }
//                }
//
//                override fun onActionEnd() {
//                    pauseMenu!!.unlockSwitch()
//                }
//            }, settings.menuKey)

        log.debug("Application scenes initialized")
    }

    override fun onMainLoopStarting() {
        if (!settings.isExperimentalNative) {
            mainWindow.addIcons(assetLoaderService.loadImage(settings.appIcon))
            mainWindow.defaultCursor = ImageCursor(assetLoaderService.loadCursorImage("fxgl_default.png"), 7.0, 6.0)
        }

        SystemActions.bind(gameScene.input)
    }

    private fun addOverlay(scene: Scene) {
        scene.root.children += overlayRoot
    }

    private fun removeOverlay(scene: Scene) {
        scene.root.children -= overlayRoot
    }

    /**
     * @return true if can show close dialog
     */
    private fun canShowCloseDialog(): Boolean {
        // do not allow close dialog if
        // 1. a dialog is shown
        // 2. we are loading a game
        // 3. we are showing intro

        // TODO: mainWindow.currentScene === dialogScene ||
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

    private var dataFile: DataFile? = null

    fun startNewGame() {
        log.debug("Starting new game")

        mainWindow.setScene(loadScene)

        clearPreviousGame()

        loadScene.pushNewTask(GameApplication.InitAppTask())
    }

    private fun clearPreviousGame() {
        log.debug("Clearing previous game")

        gameScene.reset()
    }

    fun saveGame(dataFile: DataFile) {
        //saveLoadManager.save(dataFile)
    }

    fun loadGame(dataFile: DataFile) {
        // TODO: can we modify task.onSucceeded from this class ...
//        this.dataFile = dataFile
//
//        log.debug("Starting loaded game")
//        loadScene.pushNewTask(InitAppTask(app))
//        mainWindow.setScene(loadScene)
    }

    override fun onGameReady(vars: PropertyMap) {
//        dataFile?.let {
//            saveLoadManager.load(it)
//        }
//
//        dataFile = null
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