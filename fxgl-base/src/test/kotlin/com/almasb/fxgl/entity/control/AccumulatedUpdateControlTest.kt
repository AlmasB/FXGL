/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.control

import com.almasb.fxgl.app.FXGLMock
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

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @Test
    fun `Control updates every 5 frames`() {
        val e = Entity()

        val control = ClearControl()
        e.addComponent(control)

        for (i in 1..4) {
            control.onUpdate(0.016)
            assertThat(control.count, `is`(0))
        }

        control.onUpdate(0.016)
        assertThat(control.count, `is`(999))
    }

    class ClearControl : AccumulatedUpdateControl(4) {
        var count = 0

        override fun onAccumulatedUpdate(entity: Entity, tpfSum: Double) {
            count = 999
            assertThat(tpfSum, `is`(0.016 * (4 + 1)))
        }
    }
}