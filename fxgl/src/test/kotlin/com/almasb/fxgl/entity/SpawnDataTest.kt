/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SpawnDataTest {

    @Test
    fun `Put and get`() {
        val data = SpawnData(0.0, 0.0)

        data.put("testInt", 100)
        data.put("testDouble", 3.0)

        assertThat(data.get("testInt"), `is`(100))
        assertThat(data.get("testDouble"), `is`(3.0))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Throw if key not found`() {
        SpawnData(0.0, 0.0).get<Int>("SomeKey")
    }
}