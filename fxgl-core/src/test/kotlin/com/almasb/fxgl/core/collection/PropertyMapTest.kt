/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.core.collection

import com.almasb.fxgl.core.math.Vec2
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.StringProperty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class PropertyMapTest {

    private lateinit var map: PropertyMap

    @BeforeEach
    fun setUp() {
        map = PropertyMap()
    }

    @Test
    fun `Exists`() {
        assertFalse(map.exists("key1"))

        map.setValue("key1", "aaa")

        assertTrue(map.exists("key1"))

        map.remove("key1")

        assertFalse(map.exists("key1"))
    }

    @Test
    fun `Get optional value`() {
        map.setValue("key1", "aaa")

        assertThat(map.getValueOptional<String>("key1").get(), `is`("aaa"))

        map.remove("key1")

        assertFalse(map.getValueOptional<Any>("key1").isPresent)
    }

    @Test
    fun `Get observable value`() {
        map.setValue("key1", "aaa")

        val obsValue = map.getValueObservable("key1") as StringProperty

        assertThat(obsValue.value, `is`("aaa"))
    }

    @Test
    fun `Put and get`() {
        map.setValue("key1", "aaa")
        map.setValue("key2", -55)
        map.setValue("key3", 900.0)
        map.setValue("key4", MyClass(2))
        map.setValue("key5", true)

        assertThat(map.getString("key1"), `is`("aaa"))
        assertThat(map.getInt("key2"), `is`(-55))
        assertThat(map.getDouble("key3"), `is`(900.0))
        assertThat(map.getObject<MyClass>("key4").i, `is`(2))
        assertThat(map.getBoolean("key5"), `is`(true))

        assertThat(map.getValue("key1"), `is`("aaa"))
        assertThat(map.getValue("key2"), `is`(-55))

        assertThat(map.keys(), containsInAnyOrder("key1", "key2", "key3", "key4", "key5"))

        map.setValue("key1", "a2")
        map.setValue("key2", 33)
        map.setValue("key3", 40.0)
        map.setValue("key4", MyClass(4))
        map.setValue("key5", false)

        assertThat(map.getString("key1"), `is`("a2"))
        assertThat(map.getInt("key2"), `is`(33))
        assertThat(map.getDouble("key3"), `is`(40.0))
        assertThat(map.getObject<MyClass>("key4").i, `is`(4))
        assertThat(map.getBoolean("key5"), `is`(false))

        map.clear()
        assertTrue(map.keys().isEmpty())
    }

    @Test
    fun `Throws if no such key`() {
        assertThrows<IllegalArgumentException> {
            map.getValue("hello")
        }
    }

    @Test
    fun `Increment Double and Int`() {
        map.setValue("key2", -55)
        map.setValue("key3", 900.0)

        map.increment("key2", 2)
        assertThat(map.getInt("key2"), `is`(-53))

        map.increment("key3", -100.0)
        assertThat(map.getDouble("key3"), `is`(800.0))
    }

    @Test
    fun `Listeners`() {
        var count = 0

        map.setValue("key", 1)
        map.setValue("key2", 2)

        val l = object : PropertyChangeListener<Int> {

            override fun onChange(prev: Int, now: Int) {
                assertThat(now, `is`(3))
                assertThat(prev, `is`(1))

                count++
            }
        }

        map.addListener("key", l)
        map.addListener("key2", object : PropertyChangeListener<Int> {

            override fun onChange(prev: Int, now: Int) {
                // this should not fire
                count++
            }
        })

        map.setValue("key", 3)
        assertThat(count, `is`(1))

        map.removeListener("key", l)

        map.setValue("key", 5)
        assertThat(count, `is`(1))

        map.addListener("key", l)

        map.clear()

        map.setValue("key", 7)
        assertThat(count, `is`(1))
    }

    @Test
    fun `Map change listener`() {
        var count = 0

        map.addListener(object : PropertyMapChangeListener {
            override fun onUpdated(propertyName: String, propertyValue: Any) {
                if (count == 0) {

                    assertThat(propertyName, `is`("key"))
                    assertThat(propertyValue, `is`(2))

                    count++
                } else {
                    assertThat(propertyName, `is`("key"))
                    assertThat(propertyValue, `is`(5))

                    count++
                }
            }

            override fun onRemoved(propertyName: String, propertyValue: Any) {

                assertThat(count, `is`(2))
                assertThat(propertyName, `is`("key"))
                assertThat(propertyValue, `is`(5))

                count++
            }
        })

        map.setValue("key", 2)

        map.setValue("key", 5)

        map.remove("key")

        assertThat(count, `is`(3))
    }

    @Test
    fun `ObjectProperty changes are notified via listeners`() {
        var count = 0

        val v = Vec2(5.0, 2.0)

        map.setValue("v", v)

        val l = PropertyChangeListener<Vec2> { prev, now ->

            if (count == 0) {
                assertThat(now, `is`(v))
                assertThat(v.x, `is`(2f))
                assertThat(v.y, `is`(3f))
            } else {
                assertThat(now, `is`(not(v)))
                assertThat(now.x, `is`(0f))
                assertThat(now.y, `is`(0f))
            }

            count++
        }

        map.addListener("v", l)

        v.set(2f, 3f)

        map.setValue("v", v)

        map.setValue("v", Vec2())

        assertThat(count, `is`(2))

        // remove listener, so setValue shouldn't fire
        map.removeListener("v", l)
        map.setValue("v", Vec2())

        assertThat(count, `is`(2))
    }

    @Test
    fun `Copy returns a shallow copy`() {
        map.setValue("testInt", 3)
        map.setValue("testDouble", 5.0)

        val copy = map.copy()

        assertFalse(copy === map)
        assertThat(copy.keys().size, `is`(2))
        assertThat(copy.getInt("testInt"), `is`(3))
        assertThat(copy.getDouble("testDouble"), `is`(5.0))
    }

    @Test
    fun `From map`() {
        val javaMap = mapOf(
                "testInt" to 3,
                "testDouble" to 5.0,
                "testBoolean" to false,
                "testString" to "Hello world",
                "testList" to listOf(3, 5)
        )

        map = PropertyMap.from(javaMap)

        assertThat(map.keys().size, `is`(5))
        assertThat(map.getInt("testInt"), `is`(3))
        assertThat(map.getDouble("testDouble"), `is`(5.0))
        assertThat(map.getBoolean("testBoolean"), `is`(false))
        assertThat(map.getString("testString"), `is`("Hello world"))

        val list = map.getObject<List<Int>>("testList")

        assertThat(list, contains(3, 5))
    }

    @Test
    fun `From String map`() {
        val javaMap = mapOf(
                "testInt" to "3",
                "testDouble" to "5.0",
                "testBoolean" to "false",
                "testBoolean2" to "true",
                "testString" to "Hello world"
        )

        map = PropertyMap.fromStringMap(javaMap)

        assertThat(map.keys().size, `is`(5))
        assertThat(map.getInt("testInt"), `is`(3))
        assertThat(map.getDouble("testDouble"), `is`(5.0))
        assertThat(map.getBoolean("testBoolean"), `is`(false))
        assertThat(map.getBoolean("testBoolean2"), `is`(true))
        assertThat(map.getString("testString"), `is`("Hello world"))
    }

    @Test
    fun `To map`() {
        map.setValue("testInt", 3)
        map.setValue("testDouble", 5.0)

        val javaMap = map.toMap()

        assertThat(javaMap.size, `is`(2))

        assertTrue(javaMap["testInt"] == 3)
        assertTrue(javaMap["testDouble"] == 5.0)
    }

    @Test
    fun `To String map`() {
        map.setValue("testInt", 3)
        map.setValue("testDouble", 5.0)

        val javaMap = map.toStringMap()

        assertThat(javaMap.size, `is`(2))

        assertTrue(javaMap["testInt"] == "3")
        assertTrue(javaMap["testDouble"] == "5.0")
    }

    @Test
    fun `To String`() {
        map.setValue("testInt", 3)
        map.setValue("testDouble", 5.0)

        val s = map.toString()

        assertTrue(s.contains("testDouble=DoubleProperty [value: 5.0]"))
        assertTrue(s.contains("testInt=IntegerProperty [value: 3]"))
    }

    @Test
    fun `forEach test`() {
        var timesCounter = 0
        var sumCounter = 0

        map.setValue("testElement1", 2)
        map.setValue("testElement2", 2)

        map.forEach { _, value ->
            run {
                timesCounter++
                sumCounter += (value as SimpleIntegerProperty).value
            }
        }

        assertEquals(2, timesCounter)
        assertEquals(4, sumCounter)
    }

    private class MyClass(val i: Int)


///**
// *
// *
// * @author Almas Baimagambetov (almaslvl@gmail.com)
// */
//class GameStateTest {
//
//    private lateinit var gameState: GameState
//
//    @BeforeEach
//    fun setUp() {
//        gameState = GameState()
//    }
//
//    @Test
//    fun `Test put get`() {
//        gameState.setValue("testBoolean", true)
//        gameState.setValue("testInt", 5)
//        gameState.setValue("testDouble", 10.5)
//        gameState.setValue("testString", "StringData")
//        gameState.setValue("testObject", Dummy("ObjectData"))
//
//        assertTrue(gameState.getBoolean("testBoolean"))
//        assertThat(gameState.getInt("testInt"), `is`(5))
//        assertThat(gameState.getDouble("testDouble"), `is`(10.5))
//        assertThat(gameState.getString("testString"), `is`("StringData"))
//        assertThat(gameState.getObject<Dummy>("testObject").data, `is`("ObjectData"))
//
//        assertThat(gameState.getBoolean("testBoolean"), `is`(gameState.booleanProperty("testBoolean").value))
//        assertThat(gameState.getInt("testInt"), `is`(gameState.intProperty("testInt").value))
//        assertThat(gameState.getDouble("testDouble"), `is`(gameState.doubleProperty("testDouble").value))
//        assertThat(gameState.getString("testString"), `is`(gameState.stringProperty("testString").value))
//        assertThat(gameState.getObject<Dummy>("testObject"), `is`(gameState.objectProperty<Dummy>("testObject").value))
//    }
//
//    @Test
//    fun `Test set`() {
//        gameState.setValue("testBoolean", true)
//        gameState.setValue("testInt", 5)
//        gameState.setValue("testDouble", 10.5)
//        gameState.setValue("testString", "StringData")
//        gameState.setValue("testObject", Dummy("ObjectData"))
//
//        gameState.setValue("testBoolean", false)
//        gameState.setValue("testInt", 50)
//        gameState.setValue("testDouble", 100.5)
//        gameState.setValue("testString", "StringDataNew")
//        gameState.setValue("testObject", Dummy("ObjectDataNew"))
//
//        assertFalse(gameState.getBoolean("testBoolean"))
//        assertThat(gameState.getInt("testInt"), `is`(50))
//        assertThat(gameState.getDouble("testDouble"), `is`(100.5))
//        assertThat(gameState.getString("testString"), `is`("StringDataNew"))
//        assertThat(gameState.getObject<Dummy>("testObject").data, `is`("ObjectDataNew"))
//    }

//
//    @Test
//    fun `Game difficulty`() {
//        gameState.gameDifficultyProperty().value = GameDifficulty.NIGHTMARE
//
//        assertThat(gameState.getGameDifficulty(), `is`(GameDifficulty.NIGHTMARE))
//    }
//
//    @Test
//    fun `Clear`() {
//        gameState.setValue("testInt", 10)
//
//        val listener = object : PropertyChangeListener<Int> {
//            override fun onChange(prev: Int, now: Int) {
//            }
//        }
//
//        gameState.addListener("testInt", listener)
//
//        gameState.clear()
//
//        assertThrows(IllegalArgumentException::class.java, {
//            gameState.getInt("testInt")
//        })
//    }
//
//    private class Dummy(var data: String) {}
//}
}