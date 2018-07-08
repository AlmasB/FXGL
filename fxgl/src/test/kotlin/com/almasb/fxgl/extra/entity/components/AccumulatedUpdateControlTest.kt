/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.entity.Entity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AccumulatedUpdateControlTest {

    @Test
    fun `Component updates every 5 frames`() {
        val e = Entity()

        val control = ClearComponent()
        e.addComponent(control)

        for (i in 1..4) {
            control.onUpdate(0.016)
            assertThat(control.count, `is`(0))
        }

        control.onUpdate(0.016)
        assertThat(control.count, `is`(999))
    }

    class ClearComponent : AccumulatedUpdateComponent(4) {
        var count = 0

        override fun onAccumulatedUpdate(tpfSum: Double) {
            count = 999
            assertThat(tpfSum, `is`(0.016 * (4 + 1)))
        }
    }
}