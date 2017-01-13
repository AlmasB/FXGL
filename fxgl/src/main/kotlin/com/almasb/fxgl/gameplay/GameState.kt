/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.core.collection.ObjectMap
import javafx.beans.property.*

/**
 * Holds game CVars as JavaFX properties and allows
 * easy manipulation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameState {

    private val properties = ObjectMap<String, Any>(32)

    fun setValue(propertyName: String, value: Any) {
        when (value) {
            is Int -> intProperty(propertyName).value = value
            is Double -> doubleProperty(propertyName).value = value
        }
    }

    fun increment(propertyName: String, value: Int) {
        intProperty(propertyName).value += value
    }

    fun increment(propertyName: String, value: Double) {
        doubleProperty(propertyName).value += value
    }

    fun getInt(propertyName: String) = intProperty(propertyName).value

    fun getDouble(propertyName: String) = doubleProperty(propertyName).value

    fun intProperty(propertyName: String): IntegerProperty {
        return getOrCreate(propertyName, Int::class.java) as IntegerProperty
    }

    fun doubleProperty(propertyName: String): DoubleProperty {
        return getOrCreate(propertyName, Double::class.java) as DoubleProperty
    }

    private fun getOrCreate(propertyName: String, type: Class<*>): Any {
        var property = properties.get(propertyName)

        if (property == null) {
            property = when (type) {
                Boolean::class.java -> SimpleBooleanProperty()
                Int::class.java -> SimpleIntegerProperty()
                Double::class.java -> SimpleDoubleProperty()
                String::class.java -> SimpleStringProperty()
                else -> throw IllegalArgumentException("Unknown property type: $type")
            }

            properties.put(propertyName, property)
        }

        return property
    }
}