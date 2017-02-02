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
import java.util.*

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

    private val gameDifficulty = SimpleObjectProperty(GameDifficulty.MEDIUM)

    /**
     * @return game difficulty
     */
    fun getGameDifficulty(): GameDifficulty {
        return gameDifficultyProperty().get()
    }

    /**
     * @return game difficulty property
     */
    fun gameDifficultyProperty(): ObjectProperty<GameDifficulty> {
        return gameDifficulty
    }

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
            is Boolean -> SimpleBooleanProperty()
            is Int -> SimpleIntegerProperty()
            is Double-> SimpleDoubleProperty()
            is String -> SimpleStringProperty()
            else -> throw IllegalArgumentException("Unknown value type: $value")
        }

        properties.put(propertyName, property)
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

    fun booleanProperty(propertyName: String) = get(propertyName) as BooleanProperty

    fun intProperty(propertyName: String) = get(propertyName) as IntegerProperty

    fun doubleProperty(propertyName: String) = get(propertyName) as DoubleProperty

    fun stringProperty(propertyName: String) = get(propertyName) as StringProperty

    fun clear() {
        properties.clear()
    }

    private fun get(propertyName: String) = properties.get(propertyName)
            ?: throw IllegalArgumentException("Property $propertyName does not exist")
}