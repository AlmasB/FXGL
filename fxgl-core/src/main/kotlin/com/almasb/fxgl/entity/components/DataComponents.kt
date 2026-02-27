/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */


package com.almasb.fxgl.entity.components

import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.SerializableComponent
import javafx.beans.property.*

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */

/**
 * Represents a boolean value based component.
 */
abstract class BooleanComponent
@JvmOverloads constructor(initialValue: Boolean = false) : Component(), SerializableComponent {

    private val property: BooleanProperty = SimpleBooleanProperty(initialValue)

    var value: Boolean
        get() = property.get()
        set(value) = property.set(value)

    fun valueProperty() = property

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    override fun toString() = "${javaClass.simpleName.substringBefore("Component")}($value)"
}

/**
 * Represents an int value based component.
 */
abstract class IntegerComponent
@JvmOverloads constructor(initialValue: Int = 0) : Component(), SerializableComponent {

    private val property: IntegerProperty = SimpleIntegerProperty(initialValue)

    var value: Int
        get() = property.get()
        set(value) = property.set(value)

    fun valueProperty() = property

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    override fun toString() = "${javaClass.simpleName.substringBefore("Component")}($value)"
}

/**
 * Represents a double value based component.
 */
abstract class DoubleComponent
@JvmOverloads constructor(initialValue: Double = 0.0) : Component(), SerializableComponent {

    private val property: DoubleProperty = SimpleDoubleProperty(initialValue)

    var value: Double
        get() = property.get()
        set(value) = property.set(value)

    fun valueProperty() = property

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    override fun toString() = "${javaClass.simpleName.substringBefore("Component")}($value)"
}

/**
 * Represents a String value based component.
 */
abstract class StringComponent
@JvmOverloads constructor(initialValue: String = "") : Component(), SerializableComponent {

    private val property: StringProperty = SimpleStringProperty(initialValue)

    var value: String
        get() = property.get()
        set(value) = property.set(value)

    fun valueProperty() = property

    override fun write(bundle: Bundle) {
        bundle.put("value", value)
    }

    override fun read(bundle: Bundle) {
        value = bundle.get("value")
    }

    override fun toString() = "${javaClass.simpleName.substringBefore("Component")}($value)"
}

/**
 * Represents an Object value based component.
 */
abstract class ObjectComponent<T>(initialValue: T) : Component() {

    private val property: ObjectProperty<T> = SimpleObjectProperty(initialValue)

    var value: T
        get() = property.get()
        set(value) = property.set(value)

    fun valueProperty() = property

    override fun toString() = "${javaClass.simpleName.substringBefore("Component")}($value)"
}
