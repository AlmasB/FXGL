/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

/**
 * Any rechargeable component, such as HP, SP, ammo, etc.
 *
 * The internal value is a double in the range [0..maxValue].
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class RechargeableComponent extends DoubleComponent {
    private double maxValue;

    public RechargeableComponent(double maxValue) {
        super(maxValue);
        this.maxValue = maxValue;
    }

    /**
     * @return max value
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * Set max value.
     *
     * @param maxValue max value
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Damage component by given value.
     *
     * @param value the damage amount
     */
    public void damage(double value) {
        setValue(Math.max(0, getValue() - value));
    }

    /**
     * Damage component by given percentage. The percentage is calculated from
     * current value.
     *
     * @param value percentage of current value
     */
    public void damagePercentageCurrent(double value) {
        damage(value / 100 * getValue());
    }

    /**
     * Damage component by given percentage. The percentage is calculated from
     * max value.
     *
     * @param value percentage of max value
     */
    public void damagePercentageMax(double value) {
        damage(value / 100 * maxValue);
    }

    /**
     * Restore component by given value.
     *
     * @param value the amount to restore
     */
    public void restore(double value) {
        setValue(Math.min(maxValue, getValue() + value));
    }

    /**
     * Restore component by given percentage. The percentage is calculated from
     * current value.
     *
     * @param value percentage of current value
     */
    public void restorePercentageCurrent(double value) {
        restore(value / 100 * getValue());
    }

    /**
     * Restore by given percentage. The percentage is calculated from
     * max value.
     *
     * @param value percentage of max value
     */
    public void restorePercentageMax(double value) {
        restore(value / 100 * maxValue);
    }

    /**
     * Check if value is 0. Note that because internal value is a double,
     * value of 0.xx will not return true.
     *
     * @return true iff value is 0
     */
    public boolean isZero() {
        return getValue() == 0;
    }
}
