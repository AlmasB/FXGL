/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.app.GameApplication
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.core.util.BiConsumer
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.event.Subscriber
import com.almasb.fxgl.texture.Texture
import javafx.beans.property.*
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.util.Duration

/**
 * This is just top-level copy-paste from FXGL,
 * but it seems the only way to have same call-site syntax
 * for both Java and Kotlin with FXGL and FXGLForKt respectively.
 * 
 * Java users statically import com.almasb.fxgl.dsl.FXGL.*
 * Kotlin users import com.almasb.fxgl.dsl.*
 * 
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

fun getApp() = FXGL.getApp()

fun <T : GameApplication> getAppCast() = FXGL.getAppCast<T>()

fun getSettings() = FXGL.getSettings()

fun getAppWidth() = FXGL.getAppWidth()

fun getAppHeight() = FXGL.getAppHeight()

fun getUIFactory() = FXGL.getUIFactory()

fun getAssetLoader() = FXGL.getAssetLoader()

fun getEventBus() = FXGL.getEventBus()

fun getAudioPlayer() = FXGL.getAudioPlayer()

fun getDisplay() = FXGL.getDisplay()

fun getExecutor() = FXGL.getExecutor()

fun getGameWorld() = FXGL.getGameWorld()

fun getGameState() = FXGL.getGameState()

fun getGameScene() = FXGL.getGameScene()

fun getPhysicsWorld() = FXGL.getPhysicsWorld()

fun getInput() = FXGL.getInput()

fun getGameTimer() = FXGL.getGameTimer()

fun newLocalTimer() = FXGL.newLocalTimer()

fun getGameController() = FXGL.getGameController()

fun getAchievementService() = FXGL.getAchievementService()

fun getNotificationService() = FXGL.getNotificationService()

fun set(varName: String, value: Any) = FXGL.set(varName, value)

fun geti(varName: String): Int = FXGL.getGameState().getInt(varName)

fun getd(varName: String): Double = FXGL.getGameState().getDouble(varName)

fun getb(varName: String): Boolean = FXGL.getGameState().getBoolean(varName)

fun gets(varName: String): String = FXGL.getGameState().getString(varName)

fun <T> geto(varName: String): T = FXGL.getGameState().getObject(varName)

fun getip(varName: String): IntegerProperty = FXGL.getGameState().intProperty(varName)

fun getdp(varName: String): DoubleProperty = FXGL.getGameState().doubleProperty(varName)

fun getbp(varName: String): BooleanProperty = FXGL.getGameState().booleanProperty(varName)

fun getsp(varName: String): StringProperty = FXGL.getGameState().stringProperty(varName)

fun <T> getop(varName: String): ObjectProperty<T> = FXGL.getGameState().objectProperty(varName)

fun inc(varName: String, value: Int) = FXGL.getGameState().increment(varName, value)

fun inc(varName: String, value: Double) = FXGL.getGameState().increment(varName, value)

fun image(assetName: String): Image = FXGL.getAssetLoader().loadImage(assetName)

fun image(assetName: String, width: Double, height: Double): Image = texture(assetName, width, height).image

fun texture(assetName: String): Texture = FXGL.getAssetLoader().loadTexture(assetName)

fun texture(assetName: String, width: Double, height: Double): Texture = FXGL.getAssetLoader().loadTexture(assetName, width, height)

fun text(assetName: String) = FXGL.getAssetLoader().loadText(assetName)

fun loopBGM(assetName: String) = FXGL.loopBGM(assetName)

fun play(assetName: String) = FXGL.play(assetName)

fun onKeyDown(key: KeyCode, action: () -> Unit) = FXGL.onKeyDown(key, Runnable(action))

fun onKeyDown(key: KeyCode, actionName: String, action: () -> Unit) = FXGL.onKeyDown(key, actionName, Runnable(action))

fun onKey(key: KeyCode, action: () -> Unit) = FXGL.onKey(key, Runnable(action))

fun onKey(key: KeyCode, actionName: String, action: () -> Unit) = FXGL.onKey(key, actionName, Runnable(action))

fun onKeyUp(key: KeyCode, action: () -> Unit) = FXGL.onKeyUp(key, Runnable(action))

fun onKeyUp(key: KeyCode, actionName: String, action: () -> Unit) = FXGL.onKeyUp(key, actionName, Runnable(action))

fun onBtnDown(btn: MouseButton, action: () -> Unit) = FXGL.onBtnDown(btn, Runnable(action))

fun onBtnDown(btn: MouseButton, actionName: String, action: () -> Unit) = FXGL.onBtnDown(btn, actionName, Runnable(action))

fun onBtn(btn: MouseButton, action: () -> Unit) = FXGL.onBtn(btn, Runnable(action))

fun onBtn(btn: MouseButton, actionName: String, action: () -> Unit) = FXGL.onBtn(btn, actionName, Runnable(action))

fun onBtnUp(btn: MouseButton, action: () -> Unit) = FXGL.onBtnUp(btn, Runnable(action))

fun onBtnUp(btn: MouseButton, actionName: String, action: () -> Unit) = FXGL.onBtnUp(btn, actionName, Runnable(action))

fun spawn(entityName: String): Entity = FXGL.getGameWorld().spawn(entityName)

fun spawn(entityName: String, x: Double, y: Double): Entity = FXGL.getGameWorld().spawn(entityName, x, y)

fun spawn(entityName: String, position: Point2D): Entity = FXGL.getGameWorld().spawn(entityName, position)

fun spawn(entityName: String, data: SpawnData): Entity = FXGL.getGameWorld().spawn(entityName, data)

fun onCollisionBegin(typeA: Enum<*>, typeB: Enum<*>, action: (Entity, Entity) -> Unit) = FXGL.onCollisionBegin(typeA, typeB, BiConsumer(action))

fun onCollision(typeA: Enum<*>, typeB: Enum<*>, action: (Entity, Entity) -> Unit) = FXGL.onCollision(typeA, typeB, BiConsumer(action))

fun onCollisionEnd(typeA: Enum<*>, typeB: Enum<*>, action: (Entity, Entity) -> Unit) = FXGL.onCollisionEnd(typeA, typeB, BiConsumer(action))

fun random() = FXGLMath.randomDouble()

fun random(min: Int, max: Int) = FXGLMath.random(min, max)

fun random(min: Double, max: Double) = FXGLMath.random(min, max)

/* POOLING */
fun <T> obtain(type: Class<T>): T = Pools.obtain(type)

fun free(instance: Any) = Pools.free(instance)

/* EVENTS */

fun fire(event: Event) = FXGL.getEventBus().fireEvent(event)

fun <T : Event> onEvent(eventType: EventType<T>, eventHandler: (T) -> Unit): Subscriber =
        FXGL.onEvent(eventType, EventHandler { eventHandler(it) })

/* NOTIFICATIONS */

//fun notify(message: String) = getNotificationService().pushNotification(message)

/* DIALOGS */

fun showMessage(message: String) = FXGL.getDisplay().showMessageBox(message)

fun showMessage(message: String, callback: () -> Unit) = FXGL.getDisplay().showMessageBox(message, callback)

fun showConfirm(message: String, callback: (Boolean) -> Unit) = FXGL.getDisplay().showConfirmationBox(message, callback)

fun addUINode(node: Node) = FXGL.addUINode(node)

fun addUINode(node: Node, x: Double, y: Double) = FXGL.addUINode(node, x, y)

fun removeUINode(node: Node) = FXGL.removeUINode(node)

fun runOnce(action: () -> Unit, delay: Duration) = FXGL.runOnce(Runnable(action), delay)

fun run(action: () -> Unit, interval: Duration) = FXGL.run(Runnable(action), interval)

fun run(action: () -> Unit, interval: Duration, limit: Int) = FXGL.run(Runnable(action), interval, limit)

/* EXTENSIONS */

fun entityBuilder() = EntityBuilder()

fun animationBuilder() = AnimationBuilder()
