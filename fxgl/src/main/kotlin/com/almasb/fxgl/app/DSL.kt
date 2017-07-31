/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app

import com.almasb.fxgl.texture.Texture

import com.almasb.fxgl.app.FXGL.Companion.getApp
import com.almasb.fxgl.app.FXGL.Companion.getAssetLoader
import com.almasb.fxgl.app.FXGL.Companion.getAudioPlayer
import com.almasb.fxgl.core.math.FXGLMath.*
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.physics.CollisionHandler
import javafx.beans.property.*
import javafx.geometry.Point2D
import java.util.function.BiConsumer

/**
 * This API is experimental.
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

fun <T> jsonAs(name: String, type: Class<T>): T = getAssetLoader().loadJSON(name, type)

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

/* GAME WORLD */

fun spawn(entityName: String): Entity = getApp().gameWorld.spawn(entityName)

fun spawn(entityName: String, x: Double, y: Double): Entity = getApp().gameWorld.spawn(entityName, x, y)

fun spawn(entityName: String, position: Point2D): Entity = getApp().gameWorld.spawn(entityName, position)

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