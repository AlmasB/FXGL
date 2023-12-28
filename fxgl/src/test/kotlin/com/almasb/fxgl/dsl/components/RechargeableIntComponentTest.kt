/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class RechargeableIntComponentTest {

    private lateinit var hp: HealthIntComponent

    @BeforeEach
    fun setUp() {
        hp = HealthIntComponent(100)
    }

    @Test
    fun `Creation`() {
        assertThat(hp.maxValue, `is`(100))
        assertThat(hp.value, `is`(100))
        assertThat(hp.isZero, `is`(false))
    }

    @Test
    fun `Modification`() {
        hp.value = 100
        assertThat(hp.value, `is`(100))

        hp.damage(30)
        assertThat(hp.value, `is`(70))

        hp.damagePercentageCurrent(10.0)
        assertThat(hp.value, `is`(63))

        hp.damagePercentageMax(50.0)
        assertThat(hp.value, `is`(13))

        hp.restore(37)
        assertThat(hp.value, `is`(50))

        hp.restorePercentageCurrent(50.0)
        assertThat(hp.value, `is`(75))

        hp.restorePercentageMax(15.0)
        assertThat(hp.value, `is`(90))
        assertThat(hp.isZero, `is`(false))

        hp.damage(100)
        assertThat(hp.value, `is`(0))
        assertThat(hp.isZero, `is`(true))

        hp.damageFully()
        assertThat(hp.value, `is`(0))
        assertThat(hp.isZero, `is`(true))

        hp.restoreFully()
        assertThat(hp.value, `is`(100))
        assertThat(hp.isZero, `is`(false))

        hp.value = 50
        assertThat(hp.value, `is`(50))

        // From now on, maxValue is 200
        hp.maxValue = 200
        assertThat(hp.maxValue, `is`(200))
        // Value is still 50
        assertThat(hp.value, `is`(50))

        hp.restoreFully()
        assertThat(hp.value, `is`(200))

        hp.damagePercentageMax(75.0)
        assertThat(hp.value, `is`(50))

        hp.restorePercentageMax(50.0)
        assertThat(hp.value, `is`(150))

        hp.restorePercentageMax(50.0)
        // value cannot be > maxValue
        assertThat(hp.value, `is`(200))
        hp.damagePercentageMax(150.0)
        // value cannot be < 0
        assertThat(hp.value, `is`(0))
        assertThat(hp.isZero, `is`(true))
    }

    @Test
    fun `Set max below current value`() {
        hp.restoreFully()
        hp.maxValue = 50
        // value cannot be > maxValue
        assertTrue(hp.value <= hp.maxValue)
    }

    @Test
    fun `Set current value above max`() {
        hp.value = 200
        // value cannot be > maxValue
        assertTrue(hp.value <= hp.maxValue)
    }

    @Test
    fun `Properties`() {
        var value = 0
        var maxValue = 0
        var zero = false

        // Add listeners so we can monitor the properties
        hp.valueProperty().addListener { _, _, newValue -> value = newValue as Int }
        hp.maxValueProperty().addListener { _, _, newValue -> maxValue = newValue as Int }
        hp.zeroProperty().addListener { _, _, newValue -> zero = newValue as Boolean }

        hp.damage(75)
        assertThat(value, `is`(25))
        assertThat(zero, `is`(false))

        hp.damagePercentageMax(100.0)
        assertThat(value, `is`(0))
        assertThat(zero, `is`(true))

        hp.restorePercentageCurrent(10.0)
        // Still zero
        assertThat(value, `is`(0))
        assertThat(zero, `is`(true))

        hp.restorePercentageMax(10.0)
        assertThat(value, `is`(10))
        assertThat(zero, `is`(false))

        // From now on, maxValue is 200
        hp.maxValue = 200
        assertThat(maxValue, `is`(200))
        assertThat(value, `is`(10))
        assertThat(zero, `is`(false))

        hp.restorePercentageMax(10.0)
        assertThat(value, `is`(30))
        assertThat(zero, `is`(false))
    }

    @Test
    fun `value in percent`() {
        hp.value = 10
        hp.maxValue = 100

        assertThat(hp.valuePercent, `is`(10.0))

        hp.maxValue = 50

        assertThat(hp.valuePercent, `is`(20.0))
        assertThat(hp.valuePercentProperty().value, `is`(20.0))
    }
}
