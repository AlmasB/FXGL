/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.extra.entity.components;

import com.almasb.fxgl.entity.components.IntegerComponent;

/**
 * Represents some form of entity health based on integer values.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HealthComponent extends IntegerComponent {

    public HealthComponent(int value) {
        super(value);
    }

    @Override
    public String toString() {
        return "Health(" + getValue() + ")";
    }
}
