/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SpawnDataTest {

    @Test
    fun `Creation`() {
        val data = SpawnData(15.0, 30.0)

        assertThat(data.x, `is`(15.0))
        assertThat(data.y, `is`(30.0))
    }

    @Test
    fun `Put and get`() {
        val data = SpawnData(0.0, 0.0)

        data.put("testInt", 100)
        data.put("testDouble", 3.0)

        assertThat(data.get("testInt"), `is`(100))
        assertThat(data.get("testDouble"), `is`(3.0))
    }

    @Test
    fun `Throw if key not found`() {
        assertThrows(IllegalArgumentException::class.java, {
            SpawnData(Point2D.ZERO).get<Int>("SomeKey")
        })
    }
}