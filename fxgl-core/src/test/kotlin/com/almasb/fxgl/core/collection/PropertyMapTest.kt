/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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

        assertThat(map.keys(), containsInAnyOrder("key1", "key2", "key3", "key4", "key5"))

        map.clear()
        assertTrue(map.keys().isEmpty())
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

    private class MyClass(val i: Int)
}