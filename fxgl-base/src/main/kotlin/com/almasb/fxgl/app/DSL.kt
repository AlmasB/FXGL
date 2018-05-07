/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.AnimatedPoint2D
import com.almasb.fxgl.animation.AnimatedValue
import com.almasb.fxgl.animation.Animation
import com.almasb.fxgl.app.FXGL.Companion.getApp
import com.almasb.fxgl.app.FXGL.Companion.getAssetLoader
import com.almasb.fxgl.app.FXGL.Companion.getAudioPlayer
import com.almasb.fxgl.app.FXGL.Companion.getDisplay
import com.almasb.fxgl.app.FXGL.Companion.getEventBus
import com.almasb.fxgl.app.FXGL.Companion.getInput
import com.almasb.fxgl.app.FXGL.Companion.getMasterTimer
import com.almasb.fxgl.app.FXGL.Companion.getNotificationService
import com.almasb.fxgl.app.FXGL.Companion.getUIFactory
import com.almasb.fxgl.core.math.FXGLMath.random
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.core.reflect.ReflectionUtils
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.texture.Texture
import com.almasb.fxgl.util.*
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
 * Using this API results in more concise but less readable code.
 * Use with care.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

/* VARS */

fun newVar(varName: String, value: Any) = getApp().gameState.setValue(varName, value)

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

fun image(assetName: String): Image = getAssetLoader().loadImage(assetName)

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

/**
 * Spawns given [entityName] with a fade in animation.
 */
fun spawnFadeIn(entityName: String, data: SpawnData, duration: Duration): Entity {
    val e = getApp().gameWorld.create(entityName, data)

    fadeIn(e.view, duration).startInPlayState()

    getApp().gameWorld.addEntity(e)

    return e
}

fun byID(name: String, id: Int): Optional<Entity> = getApp().gameWorld.getEntityByID(name, id)

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

// TODO:
//fun fire(event: EntityEvent, eventType: String) = getEventBus().fireEntityEvent(event, eventType)

/* NOTIFICATIONS */

fun notify(message: String) = getNotificationService().pushNotification(message)

/* DIALOGS */

fun showMessage(message: String) = getDisplay().showMessageBox(message)

fun showMessage(message: String, callback: Runnable) = getDisplay().showMessageBox(message, callback)

fun showConfirm(message: String, callback: Consumer<Boolean>) = getDisplay().showConfirmationBox(message, callback)

/* UI */

fun addVarText(x: Double, y: Double, varName: String): Text {
    return getUIFactory().newText(getip(varName).asString())
            .apply {
                translateX = x
                translateY = y
            }
            .also { getApp().gameScene.addUINode(it) }
}

fun centerTextX(text: Text, minX: Double, maxX: Double) {
    text.translateX = (minX + maxX) / 2 - text.layoutBounds.width / 2
}

fun centerTextY(text: Text, minY: Double, maxY: Double) {
    text.translateY = (minY + maxY) / 2 - text.layoutBounds.height / 2
}

fun centerText(text: Text) {
    centerText(text, (FXGL.getAppWidth() / 2).toDouble(), (FXGL.getAppHeight() / 2).toDouble())
}

fun centerText(text: Text, x: Double, y: Double) {
    text.translateX = x - text.layoutBounds.width / 2
    text.translateY = y - text.layoutBounds.height / 2
}

/**
 * Binds text to application center, i.e. text stays
 * centered regardless of content size.
 *
 * @param text UI object
 */
fun centerTextBind(text: Text) {
    centerTextBind(text, (FXGL.getAppWidth() / 2).toDouble(), (FXGL.getAppHeight() / 2).toDouble())
}

/**
 * Binds text to given center point, i.e. text stays
 * centered regardless of content size.
 *
 * @param text UI object
 */
fun centerTextBind(text: Text, x: Double, y: Double) {
    text.layoutBoundsProperty().addListener { o, old, bounds ->
        text.translateX = x - bounds.width / 2
        text.translateY = y - bounds.height / 2
    }
}

fun translate(node: Node, to: Point2D, duration: Duration): Animation<*> {
    return translate(node, Point2D(node.translateX, node.translateY), to, Duration.ZERO, duration)
}

fun translate(node: Node, from: Point2D, to: Point2D, duration: Duration): Animation<*> {
    return translate(node, from, to, Duration.ZERO, duration)
}

fun translate(node: Node, from: Point2D, to: Point2D, delay: Duration, duration: Duration): Animation<*> {
    return translate(node, from, to, delay, duration, EmptyRunnable)
}

fun translate(node: Node, from: Point2D, to: Point2D, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    val anim = object : Animation<Point2D>(delay, duration, 1, AnimatedPoint2D(from, to)) {

        override fun onProgress(value: Point2D) {
            node.translateX = value.x
            node.translateY = value.y
        }
    }
    anim.onFinished = onFinishedAction
    return anim
}

fun fadeIn(node: Node, duration: Duration): Animation<*> {
    return fadeIn(node, duration, EmptyRunnable)
}

fun fadeIn(node: Node, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    return fadeIn(node, Duration.ZERO, duration, onFinishedAction)
}

fun fadeIn(node: Node, delay: Duration, duration: Duration): Animation<*> {
    return fadeIn(node, delay, duration, EmptyRunnable)
}

fun fadeIn(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    val anim = object : Animation<Double>(delay, duration, 1, AnimatedValue(0.0, 1.0)) {
        override fun onProgress(value: Double) {
            node.opacity = value
        }
    }
    anim.onFinished = onFinishedAction
    return anim
}

fun fadeOut(node: Node, duration: Duration): Animation<*> {
    return fadeOut(node, duration, EmptyRunnable)
}

fun fadeOut(node: Node, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    return fadeOut(node, Duration.ZERO, duration, onFinishedAction)
}

fun fadeOut(node: Node, delay: Duration, duration: Duration): Animation<*> {
    return fadeOut(node, delay, duration, EmptyRunnable)
}

fun fadeOut(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    val anim = fadeIn(node, delay, duration, onFinishedAction)

    // fade out is reverse fade in
    anim.isReverse = true
    return anim
}

fun fadeInOut(node: Node, duration: Duration): Animation<*> {
    return fadeInOut(node, duration, EmptyRunnable)
}

fun fadeInOut(node: Node, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    return fadeInOut(node, Duration.ZERO, duration, onFinishedAction)
}

fun fadeInOut(node: Node, delay: Duration, duration: Duration): Animation<*> {
    return fadeInOut(node, delay, duration, EmptyRunnable)
}

fun fadeInOut(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    val anim = fadeIn(node, delay, duration, onFinishedAction)
    anim.cycleCount = 2
    anim.isAutoReverse = true
    return anim
}

fun fadeOutIn(node: Node, duration: Duration): Animation<*> {
    return fadeOutIn(node, duration, EmptyRunnable)
}

fun fadeOutIn(node: Node, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    return fadeOutIn(node, Duration.ZERO, duration, onFinishedAction)
}

fun fadeOutIn(node: Node, delay: Duration, duration: Duration): Animation<*> {
    return fadeOutIn(node, delay, duration, EmptyRunnable)
}

fun fadeOutIn(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    val anim = fadeInOut(node, delay, duration, onFinishedAction)

    // fade out in is reverse fade in out
    anim.isReverse = true
    return anim
}

fun scale(node: Node, to: Point2D, duration: Duration): Animation<*> {
    return scale(node, Point2D(node.scaleX, node.scaleY), to, Duration.ZERO, duration)
}

fun scale(node: Node, from: Point2D, to: Point2D, duration: Duration): Animation<*> {
    return scale(node, from, to, Duration.ZERO, duration)
}

fun scale(node: Node, from: Point2D, to: Point2D, delay: Duration, duration: Duration): Animation<*> {
    return scale(node, from, to, delay, duration, EmptyRunnable)
}

fun scale(node: Node, from: Point2D, to: Point2D, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    val anim = object : Animation<Point2D>(delay, duration, 1, AnimatedPoint2D(from, to)) {

        override fun onProgress(value: Point2D) {
            node.scaleX = value.x
            node.scaleY = value.y
        }
    }
    anim.onFinished = onFinishedAction
    return anim
}

fun rotate(node: Node, to: Double, duration: Duration): Animation<*> {
    return rotate(node, node.rotate, to, Duration.ZERO, duration)
}

fun rotate(node: Node, from: Double, to: Double, duration: Duration): Animation<*> {
    return rotate(node, from, to, Duration.ZERO, duration)
}

fun rotate(node: Node, from: Double, to: Double, delay: Duration, duration: Duration): Animation<*> {
    return rotate(node, from, to, delay, duration, EmptyRunnable)
}

fun rotate(node: Node, from: Double, to: Double, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
    val anim = object : Animation<Double>(delay, duration, 1, AnimatedValue(from, to)) {

        override fun onProgress(value: Double) {
            node.rotate = value
        }
    }
    anim.onFinished = onFinishedAction
    return anim
}

/* TIMER */

fun runOnce(action: Runnable, delay: Duration) = getMasterTimer().runOnceAfter(action, delay)

fun run(action: Runnable, interval: Duration) = getMasterTimer().runAtInterval(action, interval)

fun run(action: Runnable, interval: Duration, limit: Int) = getMasterTimer().runAtInterval(action, interval, limit)

/* EXTENSIONS */

/**
 * Calls [func], if exception occurs in [func] the root cause is thrown.
 */
fun <T> tryCatchRoot(func: Supplier<T>): T {
    try {
        return func.get()
    } catch (e: Exception) {
        throw ReflectionUtils.getRootCause(e)
    }
}

/**
 * Calls [func], if exception occurs in [func] the root cause is thrown.
 */
fun <T> tryCatchRoot(func: () -> T): T {
    try {
        return func.invoke()
    } catch (e: Exception) {
        throw ReflectionUtils.getRootCause(e)
    }
}