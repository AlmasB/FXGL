/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import java.util.*

/**
 * Maps property (variable) names to observable JavaFX properties.
 *
 * Value types are one of
 * SimpleIntegerProperty,
 * SimpleDoubleProperty,
 * SimpleBooleanProperty,
 * SimpleStringProperty,
 * SimpleObjectProperty.
 *
 * Null values are not allowed.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PropertyMap {

    private val properties = hashMapOf<String, Any>()

    fun keys(): Set<String> = properties.keys

    /**
     * @return true if a property with [propertyName] exists
     */
    fun exists(propertyName: String) = properties.containsKey(propertyName)

    fun <T> getValueOptional(propertyName: String): Optional<T> {
        try {
            return Optional.ofNullable(getValue(propertyName))
        } catch (e: Exception) {
            return Optional.empty()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(propertyName: String): T {
        return (get(propertyName) as ObservableValue<*>).value as T
    }

    fun getValueObservable(propertyName: String): Any {
        return get(propertyName)
    }

    /**
     * Set a new [value] to an existing var [propertyName] or creates new var.
     * The value cannot be null.
     */
    fun setValue(propertyName: String, value: Any) {
        if (exists(propertyName)) {
            when (value) {
                is Boolean -> booleanProperty(propertyName).value = value
                is Int -> intProperty(propertyName).value = value
                is Double -> doubleProperty(propertyName).value = value
                is String -> stringProperty(propertyName).value = value
                else -> objectProperty<Any>(propertyName).value = value
            }
        } else {
            val property = when (value) {
                is Boolean -> SimpleBooleanProperty(value)
                is Int -> SimpleIntegerProperty(value)
                is Double-> SimpleDoubleProperty(value)
                is String -> SimpleStringProperty(value)
                else -> SimpleObjectProperty(value)
            }

            properties.put(propertyName, property)
        }
    }

    fun remove(propertyName: String) {
        properties.remove(propertyName)
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
    private val listeners = hashMapOf<ListenerKey, ChangeListener<*>>()

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
        listeners.forEach { (key, listener) ->
            // clean up all non-removed JavaFX listeners
            (get(key.propertyName) as ObservableValue<Any>).removeListener(listener as ChangeListener<Any>)
        }

        listeners.clear()

        properties.clear()
    }

    private fun get(propertyName: String) = properties.get(propertyName)
            ?: throw IllegalArgumentException("Property $propertyName does not exist")

    private class ListenerKey(val propertyName: String, val propertyListener: PropertyChangeListener<*>) {

        override fun hashCode(): Int {
            return Objects.hash(propertyName, propertyListener)
        }

        override fun equals(other: Any?): Boolean {
            // this assumption is valid since this is a private class that is used only
            // inside the listeners map above
            val o = other as ListenerKey

            return propertyName == o.propertyName && propertyListener === o.propertyListener
        }
    }

    override fun toString(): String {
        return properties.toMap().toString()
    }
}