/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.core.collection

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class UnorderedPairMapTest {

    private lateinit var map: UnorderedPairMap<String, Int>

    @BeforeEach
    fun setUp() {
        map = UnorderedPairMap()
    }

    @Test
    fun `Map operations`() {
        map.put("Hello", "World", 5)

        assertThat(map.get("Hello", "World"), `is`(5))
        assertThat(map.get("World", "Hello"), `is`(5))
        assertThat(map.get("Hello", "Hello"), nullValue())
        assertThat(map.get("World", "World"), nullValue())

        map.put("World", "Hello", 33)

        assertThat(map.get("Hello", "World"), `is`(33))
        assertThat(map.get("World", "Hello"), `is`(33))

        map.put("Hi", "Hello", 155)

        assertThat(map.values, containsInAnyOrder(33, 155))

        map.remove("Hi", "Hello")

        assertThat(map.values, containsInAnyOrder(33))

        map.clear()

        assertThat(map.values.isEmpty(), `is`(true))
    }
    
    @Test
    fun `Same key objects work`() {
        val map2 = UnorderedPairMap<CustomObject, CustomObject>(32)

        val key1 = CustomObject()
        val value = CustomObject()

        map2.put(key1, key1, value)

        assertThat(map2.values, containsInAnyOrder(value))

        assertThat(map2.get(key1, key1), `is`(value))

        assertThat(map2.get(key1, CustomObject()), nullValue())
    }

    @Test
    fun `Different pairs with the same hash stay distinct`() {
        class Key(val id: Int) {
            override fun hashCode() = 7
            override fun equals(other: Any?) = other is Key && id == other.id
        }

        val map2 = UnorderedPairMap<Key, String>()
        val a = Key(1)
        val b = Key(2)
        val c = Key(3)
        val d = Key(4)

        map2.put(a, b, "ab")
        map2.put(c, d, "cd")

        assertThat(map2.get(a, b), `is`("ab"))
        assertThat(map2.get(b, a), `is`("ab"))
        assertThat(map2.get(c, d), `is`("cd"))
        assertThat(map2.get(d, c), `is`("cd"))
        assertThat(map2.values, containsInAnyOrder("ab", "cd"))

        map2.remove(a, b)

        assertThat(map2.get(a, b), nullValue())
        assertThat(map2.get(c, d), `is`("cd"))
    }

    private class CustomObject
}