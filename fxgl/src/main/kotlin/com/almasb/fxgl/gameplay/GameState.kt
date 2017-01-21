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

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.collection.ObjectMap
import javafx.beans.property.*

/**
 * Holds game CVars as JavaFX properties and allows
 * easy manipulation.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameState {

    companion object {
        private val log = FXGL.getLogger(GameState::class.java)
    }

    private val properties = ObjectMap<String, Any>(32)

    fun exists(propertyName: String) = properties.containsKey(propertyName)

    fun getType(propertyName: String): Class<*> {
        val value = properties.get(propertyName) ?: throw IllegalArgumentException("Property $propertyName does not exist")

        return value.javaClass
    }

    fun setValue(propertyName: String, value: Any) {
        when (value) {
            is Boolean -> booleanProperty(propertyName).value = value
            is Int -> intProperty(propertyName).value = value
            is Double -> doubleProperty(propertyName).value = value
            is String -> stringProperty(propertyName).value = value
            else -> log.warning("Value for property $propertyName is of unknown type: ${value.javaClass}")
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

    fun booleanProperty(propertyName: String) =
            getOrCreate(propertyName, Boolean::class.java) as BooleanProperty

    fun intProperty(propertyName: String) =
            getOrCreate(propertyName, Int::class.java) as IntegerProperty

    fun doubleProperty(propertyName: String) =
            getOrCreate(propertyName, Double::class.java) as DoubleProperty

    fun stringProperty(propertyName: String) =
            getOrCreate(propertyName, String::class.java) as StringProperty

    fun clear() {
        properties.clear()
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