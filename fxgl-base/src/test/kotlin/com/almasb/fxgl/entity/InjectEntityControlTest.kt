/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.entity.component.CollidableComponent
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

        // this component is not present on the entity
        // so we test if impl ignores it
        lateinit var collidable: CollidableComponent
        lateinit var injectEntity: Entity

        override fun onUpdate(entity: Entity, tpf: Double) {
        }
    }
}