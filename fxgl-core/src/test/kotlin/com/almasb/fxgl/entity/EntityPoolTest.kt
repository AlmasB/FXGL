/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.entity

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityPoolTest {

    private lateinit var pool: EntityPool

    @BeforeEach
    fun setUp() {
        pool = EntityPool()
    }

    @Test
    fun `Test put take`() {
        assertThat(pool.take("bla-bla"), nullValue())

        val e1 = Entity()
        val e2 = Entity()

        pool.put("bullet", e1)
        pool.put("bullet", e2)

        // first in first out
        assertThat(pool.take("bullet"), `is`(e1))
        assertThat(pool.take("bullet"), `is`(e2))
        assertThat(pool.take("bullet"), nullValue())
    }
}