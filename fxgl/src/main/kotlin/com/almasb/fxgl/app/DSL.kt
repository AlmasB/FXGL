/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.app.FXGL.Companion.getApp
import com.almasb.fxgl.app.FXGL.Companion.getAssetLoader
import com.almasb.fxgl.app.FXGL.Companion.getAudioPlayer
import com.almasb.fxgl.app.FXGL.Companion.getDisplay
import com.almasb.fxgl.app.FXGL.Companion.getEventBus
import com.almasb.fxgl.app.FXGL.Companion.getInput
import com.almasb.fxgl.app.FXGL.Companion.getMasterTimer
import com.almasb.fxgl.app.FXGL.Companion.getNotificationService
import com.almasb.fxgl.core.math.FXGLMath.random
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.texture.Texture
import javafx.beans.property.*
import javafx.event.Event
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Using this API results in more concise but less readable code.
 * Use with care.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/* VARS */

fun set(varName: String, value: Any) = getApp().gameState.setValue(varName, value)

fun geti(varName: String): Int = getApp().gameState.getInt(varName)

fun getd(varName: String): Double = getApp().gameState.getDouble(varName)

fun getb(varName: String): Boolean = getApp().gameState.getBoolean(varName)

fun gets(varName: String): String = getApp().gameState.getString(varName)

fun <T> geto(varName: String): T = getApp().gameState.getObject(varName)

fun getip(varName: String): IntegerProperty = getApp().gameState.intProperty(varName)

fun getdp(varName: String): DoubleProperty = getApp().gameState.doubleProperty(varName)

fun getbp(varName: String): BooleanProperty = getApp().gameState.booleanProperty(varName)

fun getsp(varName: String): StringProperty = getApp().gameState.stringProperty(varName)

fun <T> getop(varName: String): ObjectProperty<T> = getApp().gameState.objectProperty(varName)

fun inc(varName: String, value: Int) = getApp().gameState.increment(varName, value)

fun inc(varName: String, value: Double) = getApp().gameState.increment(varName, value)

/* ASSET LOADING */

fun texture(assetName: String): Texture = getAssetLoader().loadTexture(assetName)

fun texture(assetName: String, width: Double, height: Double): Texture = getAssetLoader().loadTexture(assetName, width, height)

fun text(assetName: String) = getAssetLoader().loadText(assetName)

fun <T : Any> jsonAs(name: String, type: Class<T>): T = getAssetLoader().loadJSON(name, type)

/* AUDIO */

fun loopBGM(assetName: String) = getAudioPlayer().loopBGM(assetName)

fun play(assetName: String) {
    if (assetName.endsWith(".wav")) {
        getAudioPlayer().playSound(assetName)
    } else if (assetName.endsWith(".mp3")) {
        getAudioPlayer().playMusic(assetName)
    } else {
        throw IllegalArgumentException("Unsupported audio format: $assetName")
    }
}

/* INPUT */

fun onKeyDown(key: KeyCode, actionName: String, action: Runnable) {
    getInput().addAction(object : UserAction(actionName) {
        override fun onActionBegin() {
            action.run()
        }
    }, key)
}

fun onKey(key: KeyCode, actionName: String, action: Runnable) {
    getInput().addAction(object : UserAction(actionName) {
        override fun onAction() {
            action.run()
        }
    }, key)
}

fun onKeyUp(key: KeyCode, actionName: String, action: Runnable) {
    getInput().addAction(object : UserAction(actionName) {
        override fun onActionEnd() {
            action.run()
        }
    }, key)
}

fun onBtnDown(btn: MouseButton, actionName: String, action: Runnable) {
    getInput().addAction(object : UserAction(actionName) {
        override fun onActionBegin() {
            action.run()
        }
    }, btn)
}

fun onBtn(btn: MouseButton, actionName: String, action: Runnable) {
    getInput().addAction(object : UserAction(actionName) {
        override fun onAction() {
            action.run()
        }
    }, btn)
}

fun onBtnUp(btn: MouseButton, actionName: String, action: Runnable) {
    getInput().addAction(object : UserAction(actionName) {
        override fun onActionEnd() {
            action.run()
        }
    }, btn)
}

/* GAME WORLD */

fun spawn(entityName: String): Entity = getApp().gameWorld.spawn(entityName)

fun spawn(entityName: String, x: Double, y: Double): Entity = getApp().gameWorld.spawn(entityName, x, y)

fun spawn(entityName: String, position: Point2D): Entity = getApp().gameWorld.spawn(entityName, position)

fun spawn(entityName: String, data: SpawnData): Entity = getApp().gameWorld.spawn(entityName, data)

/* PHYSICS */

fun onCollisionBegin(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
    getApp().physicsWorld.addCollisionHandler(object : CollisionHandler(typeA, typeB) {
        override fun onCollisionBegin(a: Entity, b: Entity) {
            action.accept(a, b)
        }
    })
}

fun onCollision(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
    getApp().physicsWorld.addCollisionHandler(object : CollisionHandler(typeA, typeB) {
        override fun onCollision(a: Entity, b: Entity) {
            action.accept(a, b)
        }
    })
}

fun onCollisionEnd(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
    getApp().physicsWorld.addCollisionHandler(object : CollisionHandler(typeA, typeB) {
        override fun onCollisionEnd(a: Entity, b: Entity) {
            action.accept(a, b)
        }
    })
}

/* MATH */

fun rand() = random()

fun rand(min: Int, max: Int) = random(min, max)

/* POOLING */

fun <T> obtain(type: Class<T>): T = Pools.obtain(type)

fun free(instance: Any) = Pools.free(instance)

/* EVENTS */

fun fire(event: Event) = getEventBus().fireEvent(event)

/* NOTIFICATIONS */

fun notify(message: String) = getNotificationService().pushNotification(message)

/* DIALOGS */

fun showMessage(message: String) = getDisplay().showMessageBox(message)

fun showMessage(message: String, callback: Runnable) = getDisplay().showMessageBox(message, callback)

fun showConfirm(message: String, callback: Consumer<Boolean>) = getDisplay().showConfirmationBox(message, callback)