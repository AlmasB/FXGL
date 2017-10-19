/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InjectEntityControlTest {

    @Test
    fun `Test entity injection`() {
        val control = InjectEntityControl()

        val e = Entity()
        e.addControl(control)

        assertThat(control.injectEntity, `is`(e))
    }

    class InjectEntityControl : Control() {

        lateinit var injectEntity: Entity

        override fun onUpdate(entity: Entity, tpf: Double) {
        }
    }
}