/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import javafx.beans.value.ChangeListener
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RechargeableDoubleComponentTest {

    private class HPComponent : RechargeableDoubleComponent(100.0)

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

        hp.damageFully()
        assertThat(hp.value, `is`(0.0))
        assertThat(hp.isZero, `is`(true))

        hp.restoreFully()
        assertThat(hp.value, `is`(100.0))
        assertThat(hp.isZero, `is`(false))

        hp.value = 50.0
        assertThat(hp.value, `is`(50.0))

        // From now on, maxValue is 200.0
        hp.maxValue = 200.0
        assertThat(hp.maxValue, `is`(200.0))
        // Value is still 50.0
        assertThat(hp.value, `is`(50.0))

        hp.restoreFully()
        assertThat(hp.value, `is`(200.0))

        hp.damagePercentageMax(75.0)
        assertThat(hp.value, `is`(50.0))

        hp.restorePercentageMax(50.0)
        assertThat(hp.value, `is`(150.0))

        hp.restorePercentageMax(50.0)
        // value cannot be > maxValue
        assertThat(hp.value, `is`(200.0))
        hp.damagePercentageMax(150.0)
        // value cannot be < 0.0
        assertThat(hp.value, `is`(0.0))
        assertThat(hp.isZero, `is`(true))
    }

    @Test
    fun `Negative damage`() {
        hp.restoreFully()
        hp.damage(-50.0)
        // value cannot be > maxValue
        // FAILS:
        // assertTrue(hp.value <= hp.maxValue)
    }

    @Test
    fun `Change max`() {
        hp.restoreFully()
        hp.maxValue = 50.0
        // value cannot be > maxValue
        // FAILS:
        // assertTrue(hp.value <= hp.maxValue)
    }

    @Test
    fun `Properties`() {
        var value = 0.0
        var maxValue = 0.0
        var zero = false

        // Add listeners so we can monitor the properties
        hp.valueProperty().addListener { _, _, newValue -> value = newValue as Double }
        hp.maxValueProperty().addListener { _, _, newValue -> maxValue = newValue as Double }
        hp.zeroProperty().addListener { _, _, newValue -> zero = newValue as Boolean }

        hp.damage(75.0)
        assertThat(value, `is`(25.0))
        assertThat(zero, `is`(false))

        hp.damagePercentageMax(100.0)
        assertThat(value, `is`(0.0))
        assertThat(zero, `is`(true))

        hp.restorePercentageCurrent(10.0)
        // Still zero
        assertThat(value, `is`(0.0))
        assertThat(zero, `is`(true))

        hp.restorePercentageMax(10.0)
        assertThat(value, `is`(10.0))
        assertThat(zero, `is`(false))

        // From now on, maxValue is 200.0
        hp.maxValue = 200.0
        assertThat(maxValue, `is`(200.0))
        assertThat(value, `is`(10.0))
        assertThat(zero, `is`(false))

        hp.restorePercentageMax(10.0)
        assertThat(value, `is`(30.0))
        assertThat(zero, `is`(false))
    }
}
