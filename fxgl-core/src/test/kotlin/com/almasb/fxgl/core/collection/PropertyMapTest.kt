/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

import javafx.beans.property.StringProperty
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.containsInAnyOrder
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
    fun `To String`() {
        map.setValue("testInt", 3)
        map.setValue("testDouble", 5.0)

        val s = map.toString()

        assertTrue(s.contains("testDouble=DoubleProperty [value: 5.0]"))
        assertTrue(s.contains("testInt=IntegerProperty [value: 3]"))
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
//    fun `Throw if property name not found`() {
//        assertThrows(IllegalArgumentException::class.java, {
//            gameState.getBoolean("notFound")
//        })
//    }
//
//    @Test
//    fun `Test increment`() {
//        gameState.setValue("testInt", 1)
//        gameState.setValue("testDouble", 1.0)
//
//        gameState.increment("testInt", +9)
//        assertThat(gameState.getInt("testInt"), `is`(10))
//
//        gameState.increment("testInt", -10)
//        assertThat(gameState.getInt("testInt"), `is`(0))
//
//        gameState.increment("testDouble", +1.0)
//        assertThat(gameState.getDouble("testDouble"), `is`(2.0))
//
//        gameState.increment("testDouble", -3.0)
//        assertThat(gameState.getDouble("testDouble"), `is`(-1.0))
//    }
//
//    @Test
//    fun `Test listeners`() {
//        gameState.setValue("testInt", 10)
//
//        var count = 0
//
//        val listener = object : PropertyChangeListener<Int> {
//            override fun onChange(prev: Int, now: Int) {
//                assertThat(prev, `is`(10))
//                count += now
//            }
//        }
//
//        gameState.addListener("testInt", listener)
//
//        gameState.setValue("testInt", 25)
//        assertThat(count, `is`(25))
//
//        gameState.removeListener("testInt", listener)
//
//        gameState.setValue("testInt", 1000)
//        assertThat(count, `is`(25))
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