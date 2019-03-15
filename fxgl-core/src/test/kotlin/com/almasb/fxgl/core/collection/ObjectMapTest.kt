/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasEntry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class ObjectMapTest {

    @Test
    fun `To map correctly transfers data`() {
        val objectMap = ObjectMap<String, Int>()

        objectMap.put("key1", 0)
        objectMap.put("key2", -55)
        objectMap.put("key3", 900)

        val map = objectMap.toMap()

        assertThat(map, hasEntry("key1", 0))
        assertThat(map, hasEntry("key2", -55))
        assertThat(map, hasEntry("key3", 900))
    }

    @Test
    fun `Empty map`() {
        val map1 = ObjectMap<String, Int>()

        assertTrue(map1.isEmpty)
    }

    @Test
    fun `Not empty map`() {
        val map1 = ObjectMap<String, Int>()

        assertTrue(!map1.isNotEmpty)
    }

    @Test
    fun `Map size`() {
        val map1 = ObjectMap<String, Int>()

        map1.put("key1", 0)
        map1.put("key2", -55)
        map1.put("key3", 900)

        assertThat(map1.size(), `is`(3))
    }

    @Test
    fun `Map contains all given entries after putAll`() {
        val map1 = ObjectMap<String, Int>()
        val map2 = ObjectMap<String, Int>()

        map1.put("key1", 0)
        map1.put("key2", -55)
        map1.put("key3", 900)

        map2.putAll(map1)

        val map = map1.toMap()

        assertThat(map1.size(), `is`(3))
        assertThat(map, hasEntry("key1", 0))
        assertThat(map, hasEntry("key2", -55))
        assertThat(map, hasEntry("key3", 900))
    }
}
