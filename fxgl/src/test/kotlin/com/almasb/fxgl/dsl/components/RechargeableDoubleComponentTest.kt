/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RechargeableDoubleComponentTest {

    private class HPComponent : RechargeableDoubleComponent(100.0) {

    }

    private lateinit var hp: HPComponent

    @BeforeEach
    fun setUp() {
        hp = HPComponent()
    }

    @Test
    fun `Creation`() {
        assertThat(hp.maxValue, `is`(100.0))
        assertThat(hp.value, `is`(100.0))
        assertThat(hp.isZero, `is`(false))
    }

    @Test
    fun `Modification`() {
        hp.value = 100.0
        assertThat(hp.value, `is`(100.0))

        hp.damage(30.0)
        assertThat(hp.value, `is`(70.0))

        hp.damagePercentageCurrent(10.0)
        assertThat(hp.value, `is`(63.0))

        hp.damagePercentageMax(50.0)
        assertThat(hp.value, `is`(13.0))

        hp.restore(37.0)
        assertThat(hp.value, `is`(50.0))

        hp.restorePercentageCurrent(50.0)
        assertThat(hp.value, `is`(75.0))

        hp.restorePercentageMax(15.0)
        assertThat(hp.value, `is`(90.0))
        assertThat(hp.isZero, `is`(false))

        hp.damage(100.0)
        assertThat(hp.value, `is`(0.0))
        assertThat(hp.isZero, `is`(true))
    }
}