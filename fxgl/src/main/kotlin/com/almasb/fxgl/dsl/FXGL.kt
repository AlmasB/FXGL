@file:JvmName("FXGL")
/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.achievement.AchievementManager
import com.almasb.fxgl.app.Engine
import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.app.GameController
import com.almasb.fxgl.app.ReadOnlyGameSettings
import com.almasb.fxgl.audio.AudioPlayer
import com.almasb.fxgl.audio.Music
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.core.util.BiConsumer
import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.core.util.Optional
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader
import com.almasb.fxgl.input.Input
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.notification.NotificationService
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.time.LocalTimer
import com.almasb.fxgl.time.OfflineTimer
import com.almasb.fxgl.time.Timer
import javafx.beans.property.*
import javafx.event.Event
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.text.Text
import javafx.util.Duration

/**
 * Represents the FXGL facade and provides access to engine subsystems
 * via DSL.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class FXGL private constructor() { companion object {
    
    private lateinit var engine: Engine

    private fun inject(e: Engine) {
        engine = e
    }

    @JvmStatic fun getGameController(): GameController = engine

/* STATIC ACCESSORS */

    @JvmStatic fun getVersion() = engine.version

    /**
     * @return FXGL system settings
     */
    @JvmStatic fun getSettings(): ReadOnlyGameSettings = engine.settings

    @JvmStatic fun isBrowser() = engine.settings.isBrowser
    @JvmStatic fun isDesktop() = engine.settings.isDesktop
    @JvmStatic fun isMobile() = engine.settings.isMobile

    /**
     * @return instance of the running game application
     */
    @JvmStatic fun getApp() = engine.app

    /**
     * @return instance of the running game application cast to the actual type
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic fun <T : GameApplication> getAppCast() = engine.app as T

    @JvmStatic fun getAppWidth() = engine.settings.width

    @JvmStatic fun getAppHeight() = engine.settings.height

    /**
     * Note: the system bundle is saved on exit and loaded on init.
     * This bundle is meant to be used by the FXGL system only.
     * If you want to save global (non-gameplay) data use user profiles instead.
     *
     * @return FXGL system data bundle
     */
    @JvmStatic fun getSystemBundle() = engine.bundle

    @JvmStatic fun getDevPane() = engine.devPane

    @JvmStatic fun getUIFactory() = getSettings().uiFactory

    @JvmStatic fun getAssetLoader() = engine.assetLoader

    @JvmStatic fun getEventBus() = engine.eventBus

    @JvmStatic fun getAudioPlayer() = engine.getService(AudioPlayer::class.java)

    @JvmStatic fun getDisplay() = engine.display

    @JvmStatic fun getExecutor() = engine.executor

    @JvmStatic fun getFS() = engine.fs

    @JvmStatic fun getNotificationService() = engine.getService(NotificationService::class.java)

    @JvmStatic fun getAchievementService() = engine.getService(AchievementManager::class.java)

    /**
     * @return time per frame (in this frame)
     */
    @JvmStatic fun tpf() = engine.tpf

    @JvmStatic fun getGameState() = engine.playState.gameState
    @JvmStatic fun getGameWorld() = engine.playState.gameWorld
    @JvmStatic fun getPhysicsWorld() = engine.playState.physicsWorld
    @JvmStatic fun getGameScene() = engine.playState
    @JvmStatic fun getGameTimer(): Timer = engine.playState.timer

    /**
     * @return play state input
     */
    @JvmStatic fun getInput(): Input = engine.playState.input

    /**
     * @return play state timer
     */
    @Deprecated("Use getGameTimer()", ReplaceWith("getGameTimer()", "com.almasb.fxgl.dsl.FXGL.Companion.getGameTimer"))
    @JvmStatic fun getMasterTimer(): Timer = getGameTimer()

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

    @JvmStatic fun set(varName: String, value: Any) = getGameState().setValue(varName, value)

    @JvmStatic fun geti(varName: String): Int = getGameState().getInt(varName)

    @JvmStatic fun getd(varName: String): Double = getGameState().getDouble(varName)

    @JvmStatic fun getb(varName: String): Boolean = getGameState().getBoolean(varName)

    @JvmStatic fun gets(varName: String): String = getGameState().getString(varName)

    @JvmStatic fun <T> geto(varName: String): T = getGameState().getObject(varName)

    @JvmStatic fun getip(varName: String): IntegerProperty = getGameState().intProperty(varName)

    @JvmStatic fun getdp(varName: String): DoubleProperty = getGameState().doubleProperty(varName)

    @JvmStatic fun getbp(varName: String): BooleanProperty = getGameState().booleanProperty(varName)

    @JvmStatic fun getsp(varName: String): StringProperty = getGameState().stringProperty(varName)

    @JvmStatic fun <T> getop(varName: String): ObjectProperty<T> = getGameState().objectProperty(varName)

    @JvmStatic fun inc(varName: String, value: Int) = getGameState().increment(varName, value)

    @JvmStatic fun inc(varName: String, value: Double) = getGameState().increment(varName, value)

/* ASSET LOADING */

    @JvmStatic fun image(assetName: String): Image = getAssetLoader().loadImage(assetName)

    @JvmStatic fun image(assetName: String, width: Double, height: Double): Image = texture(assetName, width, height).image

    @JvmStatic fun texture(assetName: String): Texture = getAssetLoader().loadTexture(assetName)

    @JvmStatic fun texture(assetName: String, width: Double, height: Double): Texture = getAssetLoader().loadTexture(assetName, width, height)

    @JvmStatic fun text(assetName: String) = getAssetLoader().loadText(assetName)

/* AUDIO */

    /**
     * @param bgmName name of the background music file to loop
     * @return the music object that is played in a loop
     */
    @JvmStatic fun loopBGM(assetName: String): Music {
        val music = getAssetLoader().loadMusic(assetName)
        getAudioPlayer().loopMusic(music)
        return music
    }

    /**
     * Convenience method to play music/sound given its filename.
     *
     * @param assetName name of the music file
     */
    @JvmStatic fun play(assetName: String) {
        if (assetName.endsWith(".wav")) {
            val sound = getAssetLoader().loadSound(assetName)
            getAudioPlayer().playSound(sound)
        } else if (assetName.endsWith(".mp3")) {
            val music = getAssetLoader().loadMusic(assetName)
            getAudioPlayer().playMusic(music)
        } else {
            throw IllegalArgumentException("Unsupported audio format: $assetName")
        }
    }

/* INPUT */

    @JvmStatic fun onKeyDown(key: KeyCode, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onActionBegin() {
                action.run()
            }
        }, key)
    }

    private var actionCounter = 0

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

    @JvmStatic fun onKeyUp(key: KeyCode, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onActionEnd() {
                action.run()
            }
        }, key)
    }

    @JvmStatic fun onBtnDown(btn: MouseButton, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onActionBegin() {
                action.run()
            }
        }, btn)
    }

    @JvmStatic fun onBtn(btn: MouseButton, actionName: String, action: Runnable) {
        getInput().addAction(object : UserAction(actionName) {
            override fun onAction() {
                action.run()
            }
        }, btn)
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

    @JvmStatic fun spawn(entityName: String, position: Point2D): Entity = getGameWorld().spawn(entityName, position)

    @JvmStatic fun spawn(entityName: String, data: SpawnData): Entity = getGameWorld().spawn(entityName, data)

    /**
     * Spawns given [entityName] with a fade in animation.
     */
    @JvmStatic fun spawnFadeIn(entityName: String, data: SpawnData, duration: Duration): Entity {
        val e = getGameWorld().create(entityName, data)
        e.viewComponent.opacityProp.value = 0.0

        animationBuilder()
                .duration(duration)
                .fadeIn(e)
                .buildAndPlay()

        getGameWorld().addEntity(e)

        return e
    }

    @JvmStatic fun byID(name: String, id: Int): Optional<Entity> = getGameWorld().getEntityByID(name, id)

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

    @JvmStatic fun random() = FXGLMath.random()

    @JvmStatic fun random(min: Int, max: Int) = FXGLMath.random(min, max)

    @JvmStatic fun random(min: Double, max: Double) = FXGLMath.random(min, max)

    /* POOLING */
    @JvmStatic fun <T> obtain(type: Class<T>): T = Pools.obtain(type)

    @JvmStatic fun free(instance: Any) = Pools.free(instance)

/* EVENTS */

    @JvmStatic fun fire(event: Event) = getEventBus().fireEvent(event)

/* NOTIFICATIONS */

//@JvmStatic fun notify(message: String) = getNotificationService().pushNotification(message)

/* DIALOGS */

    @JvmStatic fun showMessage(message: String) = getDisplay().showMessageBox(message)

    @JvmStatic fun showMessage(message: String, callback: Runnable) = getDisplay().showMessageBox(message, callback)

    @JvmStatic fun showConfirm(message: String, callback: Consumer<Boolean>) = getDisplay().showConfirmationBox(message, callback)

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

    @JvmStatic fun addVarText(x: Double, y: Double, varName: String): Text {
        return getUIFactory().newText(getip(varName).asString())
                .apply {
                    translateX = x
                    translateY = y
                }
                .also { getGameScene().addUINode(it) }
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
        text.layoutBoundsProperty().addListener { o, old, bounds ->
            text.translateX = x - bounds.width / 2
            text.translateY = y - bounds.height / 2
        }
    }

/* TIMER */

    @JvmStatic fun runOnce(action: Runnable, delay: Duration) = getMasterTimer().runOnceAfter(action, delay)

    @JvmStatic fun run(action: Runnable, interval: Duration) = getMasterTimer().runAtInterval(action, interval)

    @JvmStatic fun run(action: Runnable, interval: Duration, limit: Int) = getMasterTimer().runAtInterval(action, interval, limit)

/* EXTENSIONS */

    @JvmStatic fun entityBuilder() = EntityBuilder()

    @JvmStatic fun animationBuilder() = AnimationBuilder()


}
}








