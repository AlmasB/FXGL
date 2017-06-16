/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.core.collection.ObjectMap
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import java.util.*

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

    private val properties = ObjectMap<String, Any>(32)

    /**
     * @return true if a property with [propertyName] exists
     */
    fun exists(propertyName: String) = properties.containsKey(propertyName)

    /**
     * Ensure that property with such name exists first using [exists].
     *
     * @return type of a property with [propertyName]
     */
    fun getType(propertyName: String): Class<*> {
        return get(propertyName).javaClass
    }

    /**
     * @return all existing properties in the form (propertyName, rawValue)
     */
    fun getProperties(): Map<String, String> {
        val map = HashMap<String, String>()

        properties.forEach { map.put(it.key, it.value.toString()) }

        return map
    }

    fun put(propertyName: String, value: Any) {
        if (exists(propertyName))
            throw IllegalArgumentException("Property $propertyName already exists")

        val property = when (value) {
            is Boolean -> SimpleBooleanProperty(value)
            is Int -> SimpleIntegerProperty(value)
            is Double-> SimpleDoubleProperty(value)
            is String -> SimpleStringProperty(value)
            else -> SimpleObjectProperty(value)
        }

        properties.put(propertyName, property)
    }

    fun setValue(propertyName: String, value: Any) {
        when (value) {
            is Boolean -> booleanProperty(propertyName).value = value
            is Int -> intProperty(propertyName).value = value
            is Double -> doubleProperty(propertyName).value = value
            is String -> stringProperty(propertyName).value = value
            else -> objectProperty<Any>(propertyName).value = value
        }
    }

    fun increment(propertyName: String, value: Int) {
        intProperty(propertyName).value += value
    }

    fun increment(propertyName: String, value: Double) {
        doubleProperty(propertyName).value += value
    }

    fun getBoolean(propertyName: String) = booleanProperty(propertyName).value

    fun getInt(propertyName: String) = intProperty(propertyName).value

    fun getDouble(propertyName: String) = doubleProperty(propertyName).value

    fun getString(propertyName: String) = stringProperty(propertyName).value

    fun <T> getObject(propertyName: String) = objectProperty<T>(propertyName).value

    fun booleanProperty(propertyName: String) = get(propertyName) as BooleanProperty

    fun intProperty(propertyName: String) = get(propertyName) as IntegerProperty

    fun doubleProperty(propertyName: String) = get(propertyName) as DoubleProperty

    fun stringProperty(propertyName: String) = get(propertyName) as StringProperty

    @Suppress("UNCHECKED_CAST")
    fun <T> objectProperty(propertyName: String) = get(propertyName) as ObjectProperty<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> addListener(propertyName: String, listener: PropertyChangeListener<T>) {
        (get(propertyName) as ObservableValue<T>).addListener { o, prev, now -> listener.onChange(prev, now) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> addListenerKt(propertyName: String, listener: (T, T) -> Unit) {
        (get(propertyName) as ObservableValue<T>).addListener { o, prev, now -> listener.invoke(prev, now) }
    }

    fun clear() {
        properties.clear()
    }

    private fun get(propertyName: String) = properties.get(propertyName)
            ?: throw IllegalArgumentException("Property $propertyName does not exist")
}