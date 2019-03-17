/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.core.collection.PropertyMap
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty

/**
 * Holds game CVars as JavaFX properties and allows
 * easy manipulation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameState {

    private val gameDifficulty = SimpleObjectProperty(GameDifficulty.MEDIUM)

    /**
     * @return game difficulty
     */
    fun getGameDifficulty(): GameDifficulty = gameDifficultyProperty().get()

    /**
     * @return game difficulty property
     */
    fun gameDifficultyProperty(): ObjectProperty<GameDifficulty> = gameDifficulty

    val properties = PropertyMap()

    /**
     * @return true if a property with [propertyName] exists
     */
    fun exists(propertyName: String) = properties.exists(propertyName)

    /**
     * Set a new [value] to an existing var [propertyName] or creates new var.
     */
    fun setValue(propertyName: String, value: Any) = properties.setValue(propertyName, value)

    fun increment(propertyName: String, value: Int) = properties.increment(propertyName, value)

    fun increment(propertyName: String, value: Double) = properties.increment(propertyName, value)

    fun getBoolean(propertyName: String) = properties.getBoolean(propertyName)

    fun getInt(propertyName: String) = properties.getInt(propertyName)

    fun getDouble(propertyName: String) = properties.getDouble(propertyName)

    fun getString(propertyName: String) = properties.getString(propertyName)

    fun <T> getObject(propertyName: String) = properties.getObject<T>(propertyName)

    fun booleanProperty(propertyName: String) = properties.booleanProperty(propertyName)

    fun intProperty(propertyName: String) = properties.intProperty(propertyName)

    fun doubleProperty(propertyName: String) = properties.doubleProperty(propertyName)

    fun stringProperty(propertyName: String) = properties.stringProperty(propertyName)

    fun <T> objectProperty(propertyName: String) = properties.objectProperty<T>(propertyName)

    fun <T> addListener(propertyName: String, listener: PropertyChangeListener<T>) = properties.addListener(propertyName, listener)

    fun <T> removeListener(propertyName: String, listener: PropertyChangeListener<T>) = properties.removeListener(propertyName, listener)

    fun clear() = properties.clear()
}