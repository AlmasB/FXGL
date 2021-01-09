/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.Entity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class TopDownMoveComponentTest {

    private lateinit var e: Entity
    private lateinit var component: TopDownMoveComponent

    @BeforeEach
    fun setUp() {
        e = Entity()
        component = TopDownMoveComponent(100.0)

        e.addComponent(component)
        component.onUpdate(0.016)
    }

    @Test
    fun `Component movements`() {
        component.moveDown()

        assertThat(e.y, `is`(2.0))

        component.moveUp()

        assertThat(e.y, `is`(0.0))

        component.moveLeft()

        assertThat(e.x, `is`(-2.0))

        component.moveRight()

        assertThat(e.x, `is`(0.0))
    }
}