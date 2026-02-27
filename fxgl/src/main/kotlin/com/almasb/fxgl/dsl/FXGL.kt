@file:JvmName("FXGL")
/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.achievement.AchievementService
import com.almasb.fxgl.animation.AnimationBuilder
import com.almasb.fxgl.app.*
import com.almasb.fxgl.app.services.FXGLAssetLoaderService
import com.almasb.fxgl.app.services.IOTaskExecutorService
import com.almasb.fxgl.app.services.SystemBundleService
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.audio.Music
import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.core.concurrent.Async
import com.almasb.fxgl.core.concurrent.Executor
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.cutscene.CutsceneService
import com.almasb.fxgl.dev.DevService
import com.almasb.fxgl.dsl.handlers.CollectibleHandler
import com.almasb.fxgl.dsl.handlers.OneTimeCollisionHandler
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader
import com.almasb.fxgl.event.Subscriber
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.InputModifier
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.input.virtual.VirtualButton
import com.almasb.fxgl.io.FileSystemService
import com.almasb.fxgl.localization.LocalizationService
import com.almasb.fxgl.minigames.MiniGameService
import com.almasb.fxgl.net.NetService
import com.almasb.fxgl.notification.NotificationService
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.profile.DataFile
import com.almasb.fxgl.profile.SaveLoadService
import com.almasb.fxgl.quest.QuestService
import com.almasb.fxgl.scene.Scene
import com.almasb.fxgl.scene.SceneService
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.almasb.fxgl.time.Timer
import com.almasb.fxgl.ui.DialogFactoryService
import com.almasb.fxgl.ui.DialogService
import com.almasb.fxgl.ui.UIFactoryService
import javafx.animation.Interpolator
import javafx.application.Application
import javafx.beans.binding.StringExpression
import javafx.beans.property.*
import javafx.concurrent.Task
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import java.net.URL
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Represents the FXGL facade and provides access to engine subsystems
 * via DSL.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGL private constructor() { companion object {

    private lateinit var engine: Engine
    private lateinit var fxApp: FXGLApplication
    private lateinit var app: GameApplication

    @JvmStatic
    internal fun inject(e: Engine, gameApp: GameApplication, a: FXGLApplication) {
        engine = e
        fxApp = a
        app = gameApp
    }

    @JvmStatic
    internal fun extract() {
        if (this::engine.isInitialized)
            engine.stopLoopAndExitServices()

        // TODO: app.onExit() to work properly for embedded shutdown cases
    }

    private val controller = object : GameController {
        override fun gotoIntro() {
            getWindowService().gotoIntro()
        }

        override fun gotoMainMenu() {
            getWindowService().gotoMainMenu()
        }

        override fun gotoGameMenu() {
            getWindowService().gotoGameMenu()
        }

        override fun gotoLoading(loadingTask: Runnable) {
            getWindowService().gotoLoading(loadingTask)
        }

        override fun gotoLoading(loadingTask: Task<*>) {
            getWindowService().gotoLoading(loadingTask)
        }

        override fun gotoPlay() {
            getWindowService().gotoPlay()
        }

        override fun startNewGame() {
            getWindowService().startNewGame()
        }

        override fun saveGame(dataFile: DataFile) {
            getWindowService().saveGame(dataFile)
        }

        override fun loadGame(dataFile: DataFile) {
            getWindowService().loadGame(dataFile)
        }

        override fun pauseEngine() {
            engine.pauseLoop()
        }

        override fun resumeEngine() {
            engine.resumeLoop()
        }

        override fun exit() {
            fxApp.exitFXGL()
        }
    }

    @JvmStatic fun getGameController(): GameController = controller

/* STATIC ACCESSORS */

    @JvmStatic fun getVersion() = engine.settings.runtimeInfo.version

    /**
     * @return FXGL system settings
     */
    @JvmStatic fun getSettings(): ReadOnlyGameSettings = engine.settings

    @JvmStatic fun isReleaseMode() = engine.settings.applicationMode == ApplicationMode.RELEASE

    @JvmStatic fun isBrowser() = engine.settings.isBrowser
    @JvmStatic fun isDesktop() = engine.settings.isDesktop
    @JvmStatic fun isMobile() = engine.settings.isMobile

    /**
     * @return instance of the running game application
     */
    @JvmStatic fun getApp() = app

    /**
     * @return instance of the running game application cast to the actual type
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic fun <T : GameApplication> getAppCast() = getApp() as T

    @JvmStatic fun getAppWidth() = engine.settings.width

    @JvmStatic fun getAppHeight() = engine.settings.height

    @JvmStatic fun getAppCenter() = Point2D(getAppWidth() / 2.0, getAppHeight() / 2.0)

    @JvmStatic fun getFXApp(): Application = fxApp

    @JvmStatic fun getPrimaryStage(): Stage {
        val window = engine.getService(FXGLApplication.GameApplicationService::class.java).window

        return window.stage
    }

    @JvmStatic fun <T : EngineService> getService(serviceClass: Class<T>): T = engine.getService(serviceClass)

    /**
     * Note: the system bundle is saved on exit and loaded on init.
     * This bundle is meant to be used by the FXGL system only.
     * If you want to save data, use [getSaveLoadService].
     */
    @JvmStatic fun getSystemBundle() = engine.getService(SystemBundleService::class.java).bundle

    @JvmStatic fun getDevService() = engine.getService(DevService::class.java)

    @JvmStatic fun getUIFactoryService() = engine.getService(UIFactoryService::class.java)

    @JvmStatic fun getDialogFactoryService() = engine.getService(DialogFactoryService::class.java)

    @JvmStatic fun getAssetLoader() = engine.getService(FXGLAssetLoaderService::class.java)

    @JvmStatic fun getEventBus() = engine.getService(FXGLApplication.GameApplicationService::class.java).eventBus

    @JvmStatic fun getAudioPlayer() = engine.getService(AudioPlayer::class.java)

    @JvmStatic fun getDialogService() = engine.getService(DialogService::class.java)

    @JvmStatic fun getExecutor(): Executor = Async

    @JvmStatic fun getFileSystemService() = engine.getService(FileSystemService::class.java)

    @JvmStatic fun getLocalizationService() = engine.getService(LocalizationService::class.java)

    @JvmStatic fun getNotificationService() = engine.getService(NotificationService::class.java)

    @JvmStatic fun getAchievementService() = engine.getService(AchievementService::class.java)

    @JvmStatic fun getCutsceneService() = engine.getService(CutsceneService::class.java)

    @JvmStatic fun getMiniGameService() = engine.getService(MiniGameService::class.java)

    @JvmStatic fun getQuestService() = engine.getService(QuestService::class.java)

    @JvmStatic fun getSaveLoadService() = engine.getService(SaveLoadService::class.java)

    @JvmStatic fun getWindowService() = engine.getService(FXGLApplication.GameApplicationService::class.java)

    @JvmStatic fun getSceneService() = engine.getService(SceneService::class.java)

    @JvmStatic fun getNetService() = engine.getService(NetService::class.java)

    @JvmStatic fun getTaskService() = engine.getService(IOTaskExecutorService::class.java)

    /**
     * @return time per frame (in this frame)
     */
    @JvmStatic fun tpf() = engine.tpf

    @JvmStatic fun cpuNanoTime() = engine.cpuNanoTime

    @JvmStatic fun getGameWorld() = getGameScene().gameWorld
    @JvmStatic fun getWorldProperties() = getGameScene().gameWorld.properties
    @JvmStatic fun getPhysicsWorld() = getGameScene().physicsWorld
    @JvmStatic fun getGameScene() = engine.getService(FXGLApplication.GameApplicationService::class.java).gameScene

    /**
     * @return play state timer
     */
    @JvmStatic fun getGameTimer(): Timer = getGameScene().timer

    /**
     * @return 'always-on' (regardless of active scene) engine timer
     */
    @JvmStatic fun getEngineTimer(): Timer = engine.getService(SceneService::class.java).timer

    /**
     * @return play state input
     */
    @JvmStatic fun getInput(): Input = getGameScene().input

    /**
     * @return new instance on each call
     */
    @JvmStatic fun newLocalTimer() = getGameTimer().newLocalTimer()

    /**
     * @param name unique name for timer
     * @return new instance on each call
     */
    @JvmStatic fun newOfflineTimer(name: String): LocalTimer = OfflineTimer(name, getSystemBundle())















/* VARS */

    @JvmStatic fun set(varName: String, value: Any) = getWorldProperties().setValue(varName, value)

    @JvmStatic fun geti(varName: String): Int = getWorldProperties().getInt(varName)

    @JvmStatic fun getd(varName: String): Double = getWorldProperties().getDouble(varName)

    @JvmStatic fun getb(varName: String): Boolean = getWorldProperties().getBoolean(varName)

    @JvmStatic fun gets(varName: String): String = getWorldProperties().getString(varName)

    @JvmStatic fun <T> geto(varName: String): T = getWorldProperties().getObject(varName)

    @JvmStatic fun getip(varName: String): IntegerProperty = getWorldProperties().intProperty(varName)

    @JvmStatic fun getdp(varName: String): DoubleProperty = getWorldProperties().doubleProperty(varName)

    @JvmStatic fun getbp(varName: String): BooleanProperty = getWorldProperties().booleanProperty(varName)

    @JvmStatic fun getsp(varName: String): StringProperty = getWorldProperties().stringProperty(varName)

    @JvmStatic fun <T> getop(varName: String): ObjectProperty<T> = getWorldProperties().objectProperty(varName)

    @JvmStatic fun inc(varName: String, value: Int) = getWorldProperties().increment(varName, value)

    @JvmStatic fun inc(varName: String, value: Double) = getWorldProperties().increment(varName, value)

    @JvmStatic fun onIntChangeTo(varName: String, value: Int, action: Runnable): PropertyChangeListener<Int> {
        return onIntChange(varName) {
            if (it == value)
                action.run()
        }
    }

    @JvmStatic fun onIntChange(varName: String, action: Consumer<Int>): PropertyChangeListener<Int> {
        val listener = PropertyChangeListener<Int> { _, newValue -> action.accept(newValue) }

        getWorldProperties().addListener(varName, listener)

        return listener
    }

    @JvmStatic fun onDoubleChange(varName: String, action: Consumer<Double>): PropertyChangeListener<Double> {
        val listener = PropertyChangeListener<Double> { _, newValue -> action.accept(newValue) }

        getWorldProperties().addListener(varName, listener)

        return listener
    }

    @JvmStatic fun onBooleanChangeTo(varName: String, value: Boolean, action: Runnable): PropertyChangeListener<Boolean> {
        return onBooleanChange(varName) {
            if (it == value)
                action.run()
        }
    }

    @JvmStatic fun onBooleanChange(varName: String, action: Consumer<Boolean>): PropertyChangeListener<Boolean> {
        val listener = PropertyChangeListener<Boolean> { _, newValue -> action.accept(newValue) }

        getWorldProperties().addListener(varName, listener)

        return listener
    }

    @JvmStatic fun onStringChangeTo(varName: String, value: String, action: Runnable): PropertyChangeListener<String> {
        return onStringChange(varName) {
            if (it == value)
                action.run()
        }
    }

    @JvmStatic fun onStringChange(varName: String, action: Consumer<String>): PropertyChangeListener<String> {
        val listener = PropertyChangeListener<String> { _, newValue -> action.accept(newValue) }

        getWorldProperties().addListener(varName, listener)

        return listener
    }

    @JvmStatic fun <T> onObjectChange(varName: String, action: Consumer<T>): PropertyChangeListener<T> {
        val listener = PropertyChangeListener<T> { _, newValue -> action.accept(newValue) }

        getWorldProperties().addListener(varName, listener)

        return listener
    }

/* ASSET LOADING */

    @JvmStatic fun image(assetName: String): Image = getAssetLoader().loadImage(assetName)

    @JvmStatic fun image(assetName: String, width: Double, height: Double): Image = texture(assetName, width, height).image

    @JvmStatic fun image(url: URL): Image = getAssetLoader().loadImage(url)

    @JvmStatic fun image(url: URL, width: Double, height: Double): Image = texture(url, width, height).image

    @JvmStatic fun texture(assetName: String): Texture = getAssetLoader().loadTexture(assetName)

    @JvmStatic fun texture(assetName: String, width: Double, height: Double): Texture = getAssetLoader().loadTexture(assetName, width, height)

    @JvmStatic fun texture(url: URL): Texture = getAssetLoader().loadTexture(url)

    @JvmStatic fun texture(url: URL, width: Double, height: Double): Texture = getAssetLoader().loadTexture(url, width, height)

    @JvmStatic fun text(assetName: String) = getAssetLoader().loadText(assetName)

/* AUDIO */

    /**
     * @param assetName name of the background music file to loop
     * @return the music object that is played in a loop
     */
    @JvmStatic fun loopBGM(assetName: String): Music {
        val music = getAssetLoader().loadMusic(assetName)
        getAudioPlayer().loopMusic(music)
        return music
    }

    /**
     * @param url url to the background music file to loop
     * @return the music object that is played in a loop
     */
    @JvmStatic fun loopBGM(url: URL): Music {
        val music = getAssetLoader().loadMusic(url)
        getAudioPlayer().loopMusic(music)
        return music
    }

    /**
     * Convenience method to play music/sound given its filename.
     *
     * @param assetName name of the music file
     */
    @JvmStatic fun play(assetName: String) {
        when {
            assetName.endsWith(".wav") -> {
                val sound = getAssetLoader().loadSound(assetName)
                getAudioPlayer().playSound(sound)
            }
            assetName.endsWith(".mp3") -> {
                val music = getAssetLoader().loadMusic(assetName)
                getAudioPlayer().playMusic(music)
            }
            else -> {
                throw IllegalArgumentException("Unsupported audio format: $assetName")
            }
        }
    }

    /**
     * Convenience method to play music/sound given its filename.
     *
     * @param url name of the music file
     */
    @JvmStatic fun play(url: URL) {
        val assetName = url.toExternalForm()

        when {
            assetName.endsWith(".wav") -> {
                val sound = getAssetLoader().loadSound(url)
                getAudioPlayer().playSound(sound)
            }
            assetName.endsWith(".mp3") -> {
                val music = getAssetLoader().loadMusic(url)
                getAudioPlayer().playMusic(music)
            }
            else -> {
                throw IllegalArgumentException("Unsupported audio format: $assetName")
            }
        }
    }

/* INPUT */

    private var actionCounter = 0

    @JvmStatic fun onKeyDown(key: KeyCode,  action: Runnable) {
        onKeyDown(key, "action${actionCounter++}", action)
    }

    @JvmStatic fun onKeyDown(key: KeyCode, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onActionBegin() {
                action.run()
            }
        }, key)
    }

    @JvmStatic fun onKey(key: KeyCode, action: Runnable) {
        onKey(key, "action${actionCounter++}", action)
    }

    @JvmStatic fun onKey(key: KeyCode, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onAction() {
                action.run()
            }
        }, key)
    }

    @JvmStatic fun onKeyUp(key: KeyCode, action: Runnable) {
        onKeyUp(key, "action${actionCounter++}", action)
    }

    @JvmStatic fun onKeyUp(key: KeyCode, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onActionEnd() {
                action.run()
            }
        }, key)
    }

    @JvmStatic fun onBtnDownPrimary(action: Runnable) {
        onBtnDown(MouseButton.PRIMARY, action)
    }

    @JvmStatic fun onBtnDownSecondary(action: Runnable) {
        onBtnDown(MouseButton.SECONDARY, action)
    }

    @JvmStatic fun onBtnDown(btn: MouseButton, action: Runnable) {
        onBtnDown(btn, "action${actionCounter++}", action)
    }

    @JvmStatic fun onBtnDown(btn: MouseButton, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onActionBegin() {
                action.run()
            }
        }, btn)
    }

    @JvmStatic fun onBtnPrimary(action: Runnable) {
        onBtn(MouseButton.PRIMARY, action)
    }

    @JvmStatic fun onBtnSecondary(action: Runnable) {
        onBtn(MouseButton.SECONDARY, action)
    }

    @JvmStatic fun onBtn(btn: MouseButton, action: Runnable) {
        onBtn(btn, "action${actionCounter++}", action)
    }

    @JvmStatic fun onBtn(btn: MouseButton, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onAction() {
                action.run()
            }
        }, btn)
    }

    @JvmStatic fun onBtnUp(btn: MouseButton, action: Runnable) {
        onBtnUp(btn, "action${actionCounter++}", action)
    }

    @JvmStatic fun onBtnUp(btn: MouseButton, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onActionEnd() {
                action.run()
            }
        }, btn)
    }

/* GAME WORLD */

    @JvmStatic fun spawn(entityName: String): Entity = getGameWorld().spawn(entityName)

    @JvmStatic fun spawn(entityName: String, x: Double, y: Double): Entity = getGameWorld().spawn(entityName, x, y)

    @JvmStatic fun spawn(entityName: String, x: Double, y: Double, z: Double): Entity = spawn(entityName, SpawnData(x, y, z))

    @JvmStatic fun spawn(entityName: String, position: Point2D): Entity = getGameWorld().spawn(entityName, position)

    @JvmStatic fun spawn(entityName: String, position: Point3D): Entity = spawn(entityName, position.x, position.y, position.z)

    @JvmStatic fun spawn(entityName: String, data: SpawnData): Entity = getGameWorld().spawn(entityName, data)

    /**
     * Spawns given [entityName] with a fade in animation.
     */
    @JvmStatic fun spawnFadeIn(entityName: String, data: SpawnData, duration: Duration): Entity {
        val e = getGameWorld().create(entityName, data)
        e.viewComponent.opacityProperty.value = 0.0

        animationBuilder()
                .duration(duration)
                .fadeIn(e)
                .buildAndPlay()

        getGameWorld().addEntity(e)

        return e
    }

    @JvmStatic fun spawnWithScale(entityName: String, data: SpawnData, duration: Duration, interpolator: Interpolator): Entity {
        val e = getGameWorld().create(entityName, data)
        return spawnWithScale(e, duration, interpolator)
    }

    @JvmStatic @JvmOverloads fun spawnWithScale(e: Entity,
                                                duration: Duration = Duration.seconds(1.0),
                                                interpolator: Interpolator = Interpolator.LINEAR): Entity {

        e.transformComponent.scaleOrigin = Point2D(e.width / 2, e.height / 2)

        animationBuilder()
                .duration(duration)
                .interpolator(interpolator)
                .scale(e)
                .from(Point2D(0.0, 0.0))
                .to(Point2D(1.0, 1.0))
                .buildAndPlay()

        getGameWorld().addEntity(e)

        return e
    }

    @JvmStatic @JvmOverloads fun despawnWithScale(e: Entity,
                                                  duration: Duration = Duration.seconds(1.0),
                                                  interpolator: Interpolator = Interpolator.LINEAR) {
        animationBuilder()
                .duration(duration)
                .interpolator(interpolator)
                .onFinished(Runnable { getGameWorld().removeEntity(e) })
                .scale(e)
                .from(Point2D(1.0, 1.0))
                .to(Point2D(0.0, 0.0))
                .buildAndPlay()
    }

    @JvmStatic fun despawnWithDelay(e: Entity, delay: Duration) {
        runOnce({ e.removeFromWorld() }, delay)
    }

    @JvmStatic fun byID(name: String, id: Int): Optional<Entity> = getGameWorld().getEntityByID(name, id)

    @JvmStatic fun byType(vararg types: Enum<*>): List<Entity> = getGameWorld().getEntitiesByType(*types)

    /**
     * @param mapFileName name of the .json file or the .tmx file
     */
    @JvmStatic fun setLevelFromMap(mapFileName: String): Level {
        if (mapFileName.endsWith(".tmx")) {
            val level = getAssetLoader().loadLevel(mapFileName, TMXLevelLoader())
            getGameWorld().setLevel(level)
            return level
        } else {
            throw IllegalArgumentException("Unknown Tiled map format: $mapFileName")
        }
    }

/* PHYSICS */

    @JvmStatic fun onCollisionCollectible(typeCollector: Enum<*>, typeCollectible: Enum<*>, action: Consumer<Entity>) {
        getPhysicsWorld().addCollisionHandler(CollectibleHandler(typeCollector, typeCollectible, "", action))
    }

    @JvmStatic fun onCollisionOneTimeOnly(typeA: Enum<*>, typeOneTimeOnly: Enum<*>, action: BiConsumer<Entity, Entity>) {
        getPhysicsWorld().addCollisionHandler(OneTimeCollisionHandler(typeA, typeOneTimeOnly, action))
    }

    @JvmStatic fun onCollisionBegin(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
        getPhysicsWorld().addCollisionHandler(object : CollisionHandler(typeA, typeB) {
            override fun onCollisionBegin(a: Entity, b: Entity) {
                action.accept(a, b)
            }
        })
    }

    @JvmStatic fun onCollision(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
        getPhysicsWorld().addCollisionHandler(object : CollisionHandler(typeA, typeB) {
            override fun onCollision(a: Entity, b: Entity) {
                action.accept(a, b)
            }
        })
    }

    @JvmStatic fun onCollisionEnd(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
        getPhysicsWorld().addCollisionHandler(object : CollisionHandler(typeA, typeB) {
            override fun onCollisionEnd(a: Entity, b: Entity) {
                action.accept(a, b)
            }
        })
    }

/* MATH */

    @JvmStatic fun random() = FXGLMath.randomDouble()

    @JvmStatic fun random(min: Int, max: Int) = FXGLMath.random(min, max)

    @JvmStatic fun random(min: Double, max: Double) = FXGLMath.random(min, max)

    /* POOLING */
    @JvmStatic fun <T> obtain(type: Class<T>): T = Pools.obtain(type)

    @JvmStatic fun free(instance: Any) = Pools.free(instance)

/* EVENTS */

    @JvmStatic fun fire(event: Event) = getEventBus().fireEvent(event)

    @JvmStatic fun <T : Event> onEvent(eventType: EventType<T>, eventHandler: EventHandler<in T>): Subscriber =
            getEventBus().addEventHandler(eventType, eventHandler)

/* NOTIFICATIONS */

//@JvmStatic fun notify(message: String) = getNotificationService().pushNotification(message)

/* DIALOGS */

    @JvmStatic fun showMessage(message: String) = getDialogService().showMessageBox(message)

    @JvmStatic fun showMessage(message: String, callback: Runnable) = getDialogService().showMessageBox(message, callback)

    @JvmStatic fun showConfirm(message: String, callback: Consumer<Boolean>) = getDialogService().showConfirmationBox(message, callback)

/* UI */

    @JvmStatic fun addUINode(node: Node) {
        getGameScene().addUINode(node)
    }

    @JvmStatic fun addUINode(node: Node, x: Double, y: Double) {
        node.translateX = x
        node.translateY = y
        getGameScene().addUINode(node)
    }

    @JvmStatic fun removeUINode(node: Node) {
        getGameScene().removeUINode(node)
    }

    /**
     * Returns new Text initialized with property value, whose name was passed to the function
     * @param propertyName name of the property in PropertyMap
     * @param x x coordinate
     * @param y y coordinate
     * @throws IllegalArgumentException if property was not found in PropertyMap
     * @return Text()
     */
    @JvmStatic fun addVarText(propertyName: String, x: Double, y: Double) : Text {
        if (!getWorldProperties().exists(propertyName))
            throw IllegalArgumentException("Property with name '$propertyName' does not exist in PropertyMap.")

        return getUIFactoryService().newText(getPropertyValueAsStringExpression(propertyName))
                    .also { addUINode(it, x, y) }
    }

    /**
     * Returns property value as String
     * @param propertyName name of the property in PropertyMap
     * @return StringExpression value of property
     * @throws IllegalArgumentException if property has unknown type (unknown in terms of 'when' clauses)
     */
    @JvmStatic private fun getPropertyValueAsStringExpression(propertyName: String): StringExpression {
        return when (val property = getWorldProperties().getValueObservable(propertyName)) {
            is SimpleBooleanProperty -> property.asString()
            is SimpleIntegerProperty -> property.asString()
            is SimpleDoubleProperty -> property.asString()
            is SimpleStringProperty -> property
            is ObjectProperty<*> -> property.asString()
            else -> throw IllegalArgumentException("Property with name '$propertyName' has unknown type: ${property.javaClass}")
        }
    }

    @JvmStatic fun addText(message: String, x: Double, y: Double): Text {
        return getUIFactoryService().newText(message)
                .also { addUINode(it, x, y) }
    }

    @JvmStatic fun centerTextX(text: Text, minX: Double, maxX: Double) {
        text.translateX = (minX + maxX) / 2 - text.layoutBounds.width / 2
    }

    @JvmStatic fun centerTextY(text: Text, minY: Double, maxY: Double) {
        text.translateY = (minY + maxY) / 2 - text.layoutBounds.height / 2
    }

    @JvmStatic fun centerText(text: Text) {
        centerText(text, (getAppWidth() / 2).toDouble(), (getAppHeight() / 2).toDouble())
    }

    @JvmStatic fun centerText(text: Text, x: Double, y: Double) {
        text.translateX = x - text.layoutBounds.width / 2
        text.translateY = y - text.layoutBounds.height / 2
    }

    /**
     * Binds text to application center, i.e. text stays
     * centered regardless of content size.
     *
     * @param text UI object
     */
    @JvmStatic fun centerTextBind(text: Text) {
        centerTextBind(text, (getAppWidth() / 2).toDouble(), (getAppHeight() / 2).toDouble())
    }

    /**
     * Binds text to given center point, i.e. text stays
     * centered regardless of content size.
     *
     * @param text UI object
     */
    @JvmStatic fun centerTextBind(text: Text, x: Double, y: Double) {
        text.layoutBoundsProperty().addListener { _, _, bounds ->
            text.translateX = x - bounds.width / 2
            text.translateY = y - bounds.height / 2
        }
    }

/* TIMER */

    @JvmStatic fun runOnce(action: Runnable, delay: Duration) = getGameTimer().runOnceAfter(action, delay)

    @JvmStatic fun run(action: Runnable, interval: Duration) = getGameTimer().runAtInterval(action, interval)

    @JvmStatic fun run(action: Runnable, interval: Duration, limit: Int) = getGameTimer().runAtInterval(action, interval, limit)

    /* DEBUG */
    @JvmStatic fun debug(message: String) = getDevService().pushDebugMessage(message)

/* LOCALIZATION */

    @JvmStatic fun localize(key: String) = getLocalizationService().getLocalizedString(key)

    @JvmStatic fun localizedStringProperty(key: String) = getLocalizationService().localizedStringProperty(key)

/* EXTENSIONS */

    @JvmStatic fun entityBuilder() = EntityBuilder()

    @JvmStatic fun entityBuilder(data: SpawnData) = EntityBuilder(data)

    @JvmStatic fun animationBuilder() = AnimationBuilder(getGameScene())

    @JvmStatic fun animationBuilder(scene: Scene) = AnimationBuilder(scene)

    @JvmStatic fun eventBuilder() = EventBuilder()

    @JvmStatic fun onKeyBuilder(
            key: KeyCode,
            name: String) = KeyInputBuilder(getInput(), key, InputModifier.NONE, name)

    @JvmStatic @JvmOverloads fun onKeyBuilder(
            key: KeyCode,
            modifier: InputModifier = InputModifier.NONE) = KeyInputBuilder(getInput(), key, modifier)

    @JvmStatic fun onKeyBuilder(
            key: KeyCode,
            virtualButton: VirtualButton) = KeyInputBuilder(getInput(), key, virtualBtn = virtualButton)

    @JvmStatic fun onKeyBuilder(
            key: KeyCode,
            modifier: InputModifier,
            virtualButton: VirtualButton) = KeyInputBuilder(getInput(), key, modifier, virtualButton)

    /**
     * Key builder for custom input.
     * Note: for onAction() to work, [input.update()] needs to be called.
     */
    @JvmStatic @JvmOverloads fun onKeyBuilder(
            input: Input,
            key: KeyCode,
            modifier: InputModifier = InputModifier.NONE) = KeyInputBuilder(input, key, modifier)
}
}








