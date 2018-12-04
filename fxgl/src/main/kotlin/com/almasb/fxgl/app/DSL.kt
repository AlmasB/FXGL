/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.animation.*
import com.almasb.fxgl.app.FXGL.Companion.getAssetLoader
import com.almasb.fxgl.app.FXGL.Companion.getAudioPlayer
import com.almasb.fxgl.app.FXGL.Companion.getDisplay
import com.almasb.fxgl.app.FXGL.Companion.getEventBus
import com.almasb.fxgl.app.FXGL.Companion.getInput
import com.almasb.fxgl.app.FXGL.Companion.getMasterTimer
import com.almasb.fxgl.app.FXGL.Companion.getUIFactory
import com.almasb.fxgl.core.math.FXGLMath
import com.almasb.fxgl.core.pool.Pools
import com.almasb.fxgl.core.util.BiConsumer
import com.almasb.fxgl.core.util.Consumer
import com.almasb.fxgl.core.util.EmptyRunnable
import com.almasb.fxgl.core.util.Optional
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.input.UserAction
import com.almasb.fxgl.physics.CollisionHandler
import com.almasb.fxgl.texture.Texture
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

fun newVar(varName: String, value: Any) = FXGL.getGameState().setValue(varName, value)

fun set(varName: String, value: Any) = FXGL.getGameState().setValue(varName, value)

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

/* ASSET LOADING */

fun image(assetName: String): Image = getAssetLoader().loadImage(assetName)

fun image(assetName: String, width: Double, height: Double): Image = texture(assetName, width, height).image

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

private var actionCounter = 0

fun onKey(key: KeyCode, action: Runnable) {
    onKey(key, "action${actionCounter++}", action)
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

fun spawn(entityName: String): Entity = FXGL.getGameWorld().spawn(entityName)

fun spawn(entityName: String, x: Double, y: Double): Entity = FXGL.getGameWorld().spawn(entityName, x, y)

fun spawn(entityName: String, position: Point2D): Entity = FXGL.getGameWorld().spawn(entityName, position)

fun spawn(entityName: String, data: SpawnData): Entity = FXGL.getGameWorld().spawn(entityName, data)

/**
 * Spawns given [entityName] with a fade in animation.
 */
fun spawnFadeIn(entityName: String, data: SpawnData, duration: Duration): Entity {
    val e = FXGL.getGameWorld().create(entityName, data)

    //fadeIn(e.view, duration).startInPlayState()

    FXGL.getGameWorld().addEntity(e)

    return e
}

fun byID(name: String, id: Int): Optional<Entity> = FXGL.getGameWorld().getEntityByID(name, id)

/**
 * @param mapFileName name of the .json file or the .tmx file
 */
fun setLevelFromMap(mapFileName: String) {
//    if (mapFileName.endsWith(".json")) {
//        setLevelFromMap(FXGL.getAssetLoader().loadJSON(mapFileName, TiledMap::class.java))
//    } else if (mapFileName.endsWith(".tmx")) {
//        setLevelFromMap(FXGL.getAssetLoader().loadTMX(mapFileName))
//    } else {
//        throw IllegalArgumentException("Unknown Tiled map format")
//    }
}

/* PHYSICS */

fun onCollisionBegin(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
    FXGL.getPhysicsWorld().addCollisionHandler(object : CollisionHandler(typeA, typeB) {
        override fun onCollisionBegin(a: Entity, b: Entity) {
            action.accept(a, b)
        }
    })
}

fun onCollision(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
    FXGL.getPhysicsWorld().addCollisionHandler(object : CollisionHandler(typeA, typeB) {
        override fun onCollision(a: Entity, b: Entity) {
            action.accept(a, b)
        }
    })
}

fun onCollisionEnd(typeA: Enum<*>, typeB: Enum<*>, action: BiConsumer<Entity, Entity>) {
    FXGL.getPhysicsWorld().addCollisionHandler(object : CollisionHandler(typeA, typeB) {
        override fun onCollisionEnd(a: Entity, b: Entity) {
            action.accept(a, b)
        }
    })
}

/* MATH */

fun random() = FXGLMath.random()

fun random(min: Int, max: Int) = FXGLMath.random(min, max)

fun random(min: Double, max: Double) = FXGLMath.random(min, max)

/* POOLING */
fun <T> obtain(type: Class<T>): T = Pools.obtain(type)

fun free(instance: Any) = Pools.free(instance)

/* EVENTS */

fun fire(event: Event) = getEventBus().fireEvent(event)

/* NOTIFICATIONS */

//fun notify(message: String) = getNotificationService().pushNotification(message)

/* DIALOGS */

fun showMessage(message: String) = getDisplay().showMessageBox(message)

fun showMessage(message: String, callback: Runnable) = getDisplay().showMessageBox(message, callback)

fun showConfirm(message: String, callback: Consumer<Boolean>) = getDisplay().showConfirmationBox(message, callback)

/* UI */

fun addUINode(node: Node) {
    FXGL.getGameScene().addUINode(node)
}

fun addUINode(node: Node, x: Double, y: Double) {
    node.translateX = x
    node.translateY = y
    FXGL.getGameScene().addUINode(node)
}

fun removeUINode(node: Node) {
    FXGL.getGameScene().removeUINode(node)
}

fun addVarText(x: Double, y: Double, varName: String): Text {
    return getUIFactory().newText(getip(varName).asString())
            .apply {
                translateX = x
                translateY = y
            }
            .also { FXGL.getGameScene().addUINode(it) }
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

fun translate(e: Entity, to: Point2D, duration: Duration) {
    translate(object : Animatable {
        override fun xProperty(): DoubleProperty {
            return e.xProperty()
        }

        override fun yProperty(): DoubleProperty {
            return e.yProperty()
        }

        override fun scaleXProperty(): DoubleProperty {
            return e.transformComponent.scaleXProperty()
        }

        override fun scaleYProperty(): DoubleProperty {
            return e.transformComponent.scaleYProperty()
        }

        override fun rotationProperty(): DoubleProperty {
            return e.transformComponent.angleProperty()
        }

        override fun opacityProperty(): DoubleProperty {
            return e.viewComponent.opacity
        }
    }, to, duration)
}

fun translate(a: Animatable, to: Point2D, duration: Duration) {
    val anim = object : Animation<Point2D>(AnimationBuilder(duration), AnimatedPoint2D(Point2D(a.xProperty().value, a.yProperty().value), to)) {

        override fun onProgress(value: Point2D) {
            a.xProperty().value = value.x
            a.yProperty().value = value.y
        }
    }

//    FXGL.getStateMachine().playState.addStateListener {
//        anim.onUpdate(it)
//    }

    anim.start()
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
    val anim = object : Animation<Point2D>(AnimationBuilder(duration, delay, onFinished = onFinishedAction), AnimatedPoint2D(from, to)) {

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
    val anim = object : Animation<Double>(AnimationBuilder(duration, delay, onFinished = onFinishedAction), AnimatedValue(0.0, 1.0)) {
        override fun onProgress(value: Double) {
            node.opacity = value
        }
    }
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
    val anim = object : Animation<Point2D>(AnimationBuilder(duration, delay, onFinished = onFinishedAction), AnimatedPoint2D(from, to)) {

        override fun onProgress(value: Point2D) {
            node.scaleX = value.x
            node.scaleY = value.y
        }
    }
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
    val anim = object : Animation<Double>(AnimationBuilder(duration, delay, onFinished = onFinishedAction), AnimatedValue(from, to)) {

        override fun onProgress(value: Double) {
            node.rotate = value
        }
    }
    return anim
}

/* TIMER */

fun runOnce(action: Runnable, delay: Duration) = getMasterTimer().runOnceAfter(action, delay)

fun run(action: Runnable, interval: Duration) = getMasterTimer().runAtInterval(action, interval)

fun run(action: Runnable, interval: Duration, limit: Int) = getMasterTimer().runAtInterval(action, interval, limit)

/* EXTENSIONS */

