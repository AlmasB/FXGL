/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.CollidableComponent
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
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
        e.addComponent(control)

        assertThat(control.entity, `is`(e))
    }

    class InjectEntityControl : Component() {

        // this component is not present on the entity
        // so we test if impl ignores it
        lateinit var collidable: CollidableComponent

        override fun onUpdate(tpf: Double) {
        }
    }
}