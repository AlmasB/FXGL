/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.animation.*
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGL.Companion.getAssetLoader
import com.almasb.fxgl.app.FXGL.Companion.getAudioPlayer
import com.almasb.fxgl.app.FXGL.Companion.getDisplay
import com.almasb.fxgl.app.FXGL.Companion.getEventBus
import com.almasb.fxgl.app.FXGL.Companion.getInput
import com.almasb.fxgl.app.FXGL.Companion.getMasterTimer
import com.almasb.fxgl.app.FXGL.Companion.getUIFactory
import com.almasb.fxgl.audio.Music
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
import com.almasb.fxgl.scene.SceneListener
import com.almasb.fxgl.texture.Texture
import javafx.animation.Interpolator
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

/* FXGL */

fun getGameWorld() = FXGL.getGameWorld()
fun getPhysicsWorld() = FXGL.getPhysicsWorld()
fun getGameScene() = FXGL.getGameScene()
fun getGameState() = FXGL.getGameState()
fun getAssetLoader() = FXGL.getAssetLoader()
fun getInput() = FXGL.getInput()

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

/**
 * @param bgmName name of the background music file to loop
 * @return the music object that is played in a loop
 */
fun loopBGM(assetName: String): Music {
    val music = getAssetLoader().loadMusic(assetName)
    getAudioPlayer().loopMusic(music)
    return music
}

/**
 * Convenience method to play music/sound given its filename.
 *
 * @param assetName name of the music file
 */
fun play(assetName: String) {
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
    val e = getGameWorld().create(entityName, data)
    e.viewComponent.opacity.value = 0.0

    fadeIn(e, Duration.ZERO, duration, EmptyRunnable)

    getGameWorld().addEntity(e)

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

/* ANIMATIONS */

// TODO: specify explicitly each argument

@JvmOverloads fun translate(n: Node,
                                from: Point2D = Point2D(n.translateX, n.translateY),
                                to: Point2D,
                                delay: Duration = Duration.ZERO,
                                duration: Duration,
                                onFinishedAction: Runnable = EmptyRunnable,
                                interpolator: Interpolator = Interpolators.LINEAR.EASE_OUT()) {

    translateAnim(n.toAnimatable(), from, to, delay, duration, onFinishedAction, interpolator)
            .also {
                val l = it.toListener()

                it.onFinished = Runnable {
                    FXGL.getGameScene().removeListener(l)
                    onFinishedAction.run()
                }

                it.start()
                FXGL.getGameScene().addListener(l)
            }
}

@JvmOverloads fun translate(e: Entity,
                                from: Point2D = Point2D(e.x, e.y),
                                to: Point2D,
                                delay: Duration = Duration.ZERO,
                                duration: Duration,
                                onFinishedAction: Runnable = EmptyRunnable,
                                interpolator: Interpolator = Interpolators.LINEAR.EASE_OUT()) {

    translateAnim(e.toAnimatable(), from, to, delay, duration, onFinishedAction, interpolator)
            .also {
                val l = it.toListener()

                it.onFinished = Runnable {
                    FXGL.getGameScene().removeListener(l)
                    onFinishedAction.run()
                }

                it.start()
                FXGL.getGameScene().addListener(l)
            }
}

@JvmOverloads fun translateAnim(n: Node,
                                from: Point2D = Point2D(n.translateX, n.translateY),
                                to: Point2D,
                                delay: Duration = Duration.ZERO,
                                duration: Duration,
                                onFinishedAction: Runnable = EmptyRunnable,
                                interpolator: Interpolator = Interpolators.LINEAR.EASE_OUT()): Animation<*> {

    return translateAnim(n.toAnimatable(), from, to, delay, duration, onFinishedAction, interpolator)
}

@JvmOverloads fun translateAnim(e: Entity,
                                from: Point2D = Point2D(e.x, e.y),
                                to: Point2D,
                                delay: Duration = Duration.ZERO,
                                duration: Duration,
                                onFinishedAction: Runnable = EmptyRunnable,
                                interpolator: Interpolator = Interpolators.LINEAR.EASE_OUT()): Animation<*> {

    return translateAnim(e.toAnimatable(), from, to, delay, duration, onFinishedAction, interpolator)
}

fun fadeIn(node: Node, duration: Duration) {
    fadeIn(node, duration, EmptyRunnable)
}

fun fadeIn(node: Node, duration: Duration, onFinishedAction: Runnable){
    fadeIn(node, Duration.ZERO, duration, onFinishedAction)
}

fun fadeIn(node: Node, delay: Duration, duration: Duration) {
    fadeIn(node, delay, duration, EmptyRunnable)
}

fun fadeIn(e: Entity, duration: Duration) {
    fadeIn(e, duration, EmptyRunnable)
}

fun fadeIn(e: Entity, duration: Duration, onFinishedAction: Runnable){
    fadeIn(e, Duration.ZERO, duration, onFinishedAction)
}

fun fadeIn(e: Entity, delay: Duration, duration: Duration) {
    fadeIn(e, delay, duration, EmptyRunnable)
}

fun fadeIn(e: Entity, delay: Duration, duration: Duration, onFinishedAction: Runnable) {
    fadeInAnim(e.toAnimatable(), 0.0, 1.0, delay, duration, onFinishedAction, Interpolators.LINEAR.EASE_OUT())
            .also {
                val l = it.toListener()

                it.onFinished = Runnable {
                    FXGL.getGameScene().removeListener(l)
                    onFinishedAction.run()
                }

                it.start()
                FXGL.getGameScene().addListener(l)
            }
}

fun fadeIn(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable) {
    fadeInAnim(node.toAnimatable(), 0.0, 1.0, delay, duration, onFinishedAction, Interpolators.LINEAR.EASE_OUT())
            .also {
                val l = it.toListener()

                it.onFinished = Runnable {
                    FXGL.getGameScene().removeListener(l)
                    onFinishedAction.run()
                }

                it.start()
                FXGL.getGameScene().addListener(l)
            }
}







//fun fadeOut(node: Node, duration: Duration): Animation<*> {
//    return fadeOut(node, duration, EmptyRunnable)
//}
//
//fun fadeOut(node: Node, duration: Duration, onFinishedAction: Runnable): Animation<*> {
//    return fadeOut(node, Duration.ZERO, duration, onFinishedAction)
//}
//
//fun fadeOut(node: Node, delay: Duration, duration: Duration): Animation<*> {
//    return fadeOut(node, delay, duration, EmptyRunnable)
//}
//
//fun fadeOut(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
//    val anim = fadeInAnim(node.toAnimatable(), delay, duration, onFinishedAction)
//
//    // fade out is reverse fade in
//    anim.isReverse = true
//    return anim
//}
//
//fun fadeInOut(node: Node, duration: Duration): Animation<*> {
//    return fadeInOut(node, duration, EmptyRunnable)
//}
//
//fun fadeInOut(node: Node, duration: Duration, onFinishedAction: Runnable): Animation<*> {
//    return fadeInOut(node, Duration.ZERO, duration, onFinishedAction)
//}
//
//fun fadeInOut(node: Node, delay: Duration, duration: Duration): Animation<*> {
//    return fadeInOut(node, delay, duration, EmptyRunnable)
//}
//
//fun fadeInOut(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
//    val anim = fadeIn(node, delay, duration, onFinishedAction)
//    anim.cycleCount = 2
//    anim.isAutoReverse = true
//    return anim
//}
//
//fun fadeOutIn(node: Node, duration: Duration): Animation<*> {
//    return fadeOutIn(node, duration, EmptyRunnable)
//}
//
//fun fadeOutIn(node: Node, duration: Duration, onFinishedAction: Runnable): Animation<*> {
//    return fadeOutIn(node, Duration.ZERO, duration, onFinishedAction)
//}
//
//fun fadeOutIn(node: Node, delay: Duration, duration: Duration): Animation<*> {
//    return fadeOutIn(node, delay, duration, EmptyRunnable)
//}
//
//fun fadeOutIn(node: Node, delay: Duration, duration: Duration, onFinishedAction: Runnable): Animation<*> {
//    val anim = fadeInOut(node, delay, duration, onFinishedAction)
//
//    // fade out in is reverse fade in out
//    anim.isReverse = true
//    return anim
//}








private fun translateAnim(a: Animatable,
                          from: Point2D,
                          to: Point2D,
                          delay: Duration,
                          duration: Duration,
                          onFinishedAction: Runnable,
                          interpolator: Interpolator): Animation<*> {

    return AnimationBuilder(duration, delay, interpolator)
            .onFinished(onFinishedAction)
            .build(AnimatedPoint2D(from, to), Consumer {
                a.xProperty().value = it.x
                a.yProperty().value = it.y
            })
}

private fun fadeInAnim(a: Animatable,
                       from: Double,
                       to: Double,
                       delay: Duration,
                       duration: Duration,
                       onFinishedAction: Runnable,
                       interpolator: Interpolator): Animation<*> {

    return AnimationBuilder(duration, delay, interpolator)
            .onFinished(onFinishedAction)
            .build(AnimatedValue(from, to), Consumer {
                a.opacityProperty().value = it
            })
}


private fun Node.toAnimatable(): Animatable {
    val n = this
    return object : Animatable {
        override fun xProperty(): DoubleProperty {
            return n.translateXProperty()
        }

        override fun yProperty(): DoubleProperty {
            return n.translateYProperty()
        }

        override fun scaleXProperty(): DoubleProperty {
            return n.scaleXProperty()
        }

        override fun scaleYProperty(): DoubleProperty {
            return n.scaleYProperty()
        }

        override fun rotationProperty(): DoubleProperty {
            return n.rotateProperty()
        }

        override fun opacityProperty(): DoubleProperty {
            return n.opacityProperty()
        }
    }
}

private fun Entity.toAnimatable(): Animatable {
    val e = this
    return object : Animatable {
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
    }
}

private fun Animation<*>.toListener(): SceneListener {
    val a = this
    return object : SceneListener {
        override fun onUpdate(tpf: Double) {
            a.onUpdate(tpf)
        }
    }
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

fun entityBuilder() = EntityBuilder()
