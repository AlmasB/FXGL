/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.core.collection.ObjectMap
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
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

    /**
     * Value types are one of
     *
     * SimpleIntegerProperty,
     * SimpleDoubleProperty,
     * SimpleBooleanProperty,
     * SimpleStringProperty,
     * SimpleObjectProperty
     */
    private val properties = ObjectMap<String, Any>(32)

    /**
     * @return true if a property with [propertyName] exists
     */
    fun exists(propertyName: String) = properties.containsKey(propertyName)

    /**
     * Ensure that property with such name exists first using [exists].
     *
     * @return type of a property with [propertyName], e.g. SimpleIntegerProperty, SimpleStringProperty
     */
    fun getType(propertyName: String): Class<*> {
        return get(propertyName).javaClass
    }

    /**
     * @return all existing properties in the form (propertyName, rawValue)
     */
    fun getProperties(): Map<String, String> {
        return properties.associateBy({ it.key }, { rawValue(it.value).toString() })
    }

    private fun rawValue(valueWrapper: Any): Any {
        return when (valueWrapper) {
            is ObservableValue<*> -> valueWrapper.value
            else -> throw IllegalArgumentException("Unsupported value wrapper type: $valueWrapper")
        }
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

    /**
     * We use ListenerKey to capture both property name and property listener.
     * We can then use property name to find all remaining JavaFX listeners
     * in case they weren't explicitly removed.
     */
    private val listeners = hashMapOf<ListenerKey, ChangeListener<*> >()

    @Suppress("UNCHECKED_CAST")
    fun <T> addListener(propertyName: String, listener: PropertyChangeListener<T>) {
        val internalListener = ChangeListener<T> { _, prev, now -> listener.onChange(prev, now) }

        val key = ListenerKey(propertyName, listener)

        listeners[key] = internalListener

        (get(propertyName) as ObservableValue<T>).addListener(internalListener)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> removeListener(propertyName: String, listener: PropertyChangeListener<T>) {
        val key = ListenerKey(propertyName, listener)

        (get(propertyName) as ObservableValue<T>).removeListener(listeners[key] as ChangeListener<in T>)
        listeners.remove(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun clear() {
        listeners.forEach { key, listener ->
            // clean up all non-removed JavaFX listeners
            (get(key.propertyName) as ObservableValue<Any>).removeListener(listener as ChangeListener<Any>)
        }
        listeners.clear()

        properties.clear()
    }

    private fun get(propertyName: String) = properties.get(propertyName)
            ?: throw IllegalArgumentException("Property $propertyName does not exist")

    private class ListenerKey(val propertyName: String,
                              val propertyListener: PropertyChangeListener<*>) {

        override fun hashCode(): Int {
            return Objects.hash(propertyName, propertyListener)
        }

        override fun equals(other: Any?): Boolean {
            if (other !is ListenerKey)
                return false

            return propertyName == other.propertyName && propertyListener === other.propertyListener
        }
    }
}