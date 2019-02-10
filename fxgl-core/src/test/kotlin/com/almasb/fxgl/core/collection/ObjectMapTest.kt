/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core.collection

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasEntry
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
}