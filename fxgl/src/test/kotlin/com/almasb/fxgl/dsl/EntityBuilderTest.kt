/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.entity.SpawnData
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityBuilderTest {

    enum class EBTType {
        ONE, TWO
    }

    private lateinit var builder: EntityBuilder

    @BeforeEach
    fun `setUp`() {
        builder = EntityBuilder()
    }

    @Test
    fun `from parses position and type`() {
        val data = SpawnData(15.0, 22.0).put("type", EBTType.TWO)

        val e = builder
                .from(data)
                .build()

        assertThat(e.position, `is`(Point2D(15.0, 22.0)))
        assertThat(e.type, `is`<Any>(EBTType.TWO))
    }

    @Test
    fun `from does not fail if type is not enum`() {
        val data = SpawnData(15.0, 22.0).put("type", "someValue")

        val e = builder
                .from(data)
                .build()

        assertTrue(e.type !is String)
    }
}