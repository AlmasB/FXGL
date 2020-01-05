/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl.components

import com.almasb.fxgl.entity.components.DoubleComponent
import com.almasb.fxgl.entity.components.IntegerComponent
import javafx.beans.binding.BooleanBinding
import kotlin.math.max
import kotlin.math.min

/**
 * Health component backed up by a double value.
 */
class HealthDoubleComponent(maxValue: Double) : RechargeableDoubleComponent(maxValue)

/**
 * Health component backed up by an int value.
 */
class HealthIntComponent(maxValue: Int) : RechargeableIntComponent(maxValue)

/**
 * Mana component backed up by a double value.
 */
class ManaDoubleComponent(maxValue: Double) : RechargeableDoubleComponent(maxValue)

/**
 * Mana component backed up by an int value.
 */
class ManaIntComponent(maxValue: Int) : RechargeableIntComponent(maxValue)

/**
 * Any rechargeable component, such as HP, SP, ammo, etc.
 * The internal value is a double in the range [0..maxValue].
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class RechargeableDoubleComponent
@JvmOverloads constructor(
        var maxValue: Double,
        initialValue: Double = maxValue
) : DoubleComponent(initialValue) {

    /**
     * Set component value to 0.
     */
    fun damageFully() {
        value = 0.0
    }

    /**
     * Damage component by given [amount].
     */
    fun damage(amount: Double) {
        value = max(0.0, value - amount)
    }

    /**
     * Damage component by given percentage. The percentage is calculated from
     * current value.
     *
     * @param percentage percentage of current value in range [0..100]
     */
    fun damagePercentageCurrent(percentage: Double) {
        damage(percentage / 100 * value)
    }

    /**
     * Damage component by given percentage. The percentage is calculated from
     * max value.
     *
     * @param percentage percentage of max value in range [0..100]
     */
    fun damagePercentageMax(percentage: Double) {
        damage(percentage / 100 * maxValue)
    }

    /**
     * Restore component value to max value.
     */
    fun restoreFully() {
        value = maxValue
    }

    /**
     * Restore component value by given amount.
     *
     * @param amount the amount to restore
     */
    fun restore(amount: Double) {
        value = min(maxValue, value + amount)
    }

    /**
     * Restore component by given percentage. The percentage is calculated from
     * current value.
     *
     * @param percentage percentage of current value in range [0..100]
     */
    fun restorePercentageCurrent(percentage: Double) {
        restore(percentage / 100 * value)
    }

    /**
     * Restore by given percentage. The percentage is calculated from
     * max value.
     *
     * @param percentage percentage of max value in range [0..100]
     */
    fun restorePercentageMax(percentage: Double) {
        restore(percentage / 100 * maxValue)
    }

    private val zeroProp = valueProperty().lessThanOrEqualTo(0.0)

    /**
     * Check if value is 0. Note that because internal value is a double,
     * value of 0.xx will not return true.
     *
     * @return true iff value is 0
     */
    val isZero: Boolean
        get() = zeroProp.value

    /**
     * @return a binding that returns true when the value is 0
     */
    fun zeroProperty(): BooleanBinding = zeroProp
}

/**
 * Any rechargeable component, such as HP, SP, ammo, etc.
 * The internal value is an int in the range [0..maxValue].
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
abstract class RechargeableIntComponent
@JvmOverloads constructor(
        var maxValue: Int,
        initialValue: Int = maxValue
) : IntegerComponent(initialValue) {

    /**
     * Set component value to 0.
     */
    fun damageFully() {
        value = 0
    }

    /**
     * Damage component by given [amount].
     */
    fun damage(amount: Int) {
        value = max(0, value - amount)
    }

    /**
     * Damage component by given percentage. The percentage is calculated from
     * current value.
     *
     * @param percentage percentage of current value in range [0..100]
     */
    fun damagePercentageCurrent(percentage: Double) {
        damage((percentage / 100 * value).toInt())
    }

    /**
     * Damage component by given percentage. The percentage is calculated from
     * max value.
     *
     * @param percentage percentage of max value in range [0..100]
     */
    fun damagePercentageMax(percentage: Double) {
        damage((percentage / 100 * maxValue).toInt())
    }

    /**
     * Restore component value to max value.
     */
    fun restoreFully() {
        value = maxValue
    }

    /**
     * Restore component value by given amount.
     *
     * @param amount the amount to restore
     */
    fun restore(amount: Int) {
        value = min(maxValue, value + amount)
    }

    /**
     * Restore component by given percentage. The percentage is calculated from
     * current value.
     *
     * @param percentage percentage of current value in range [0..100]
     */
    fun restorePercentageCurrent(percentage: Double) {
        restore((percentage / 100 * value).toInt())
    }

    /**
     * Restore by given percentage. The percentage is calculated from
     * max value.
     *
     * @param percentage percentage of max value in range [0..100]
     */
    fun restorePercentageMax(percentage: Double) {
        restore((percentage / 100 * maxValue).toInt())
    }

    private val zeroProp = valueProperty().lessThanOrEqualTo(0.0)

    /**
     * Check if value is 0. Note that because internal value is a double,
     * value of 0.xx will not return true.
     *
     * @return true iff value is 0
     */
    val isZero: Boolean
        get() = zeroProp.value

    /**
     * @return a binding that returns true when the value is 0
     */
    fun zeroProperty(): BooleanBinding = zeroProp
}