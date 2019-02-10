/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity;

import com.almasb.fxgl.entity.component.CopyableComponent;
import com.almasb.fxgl.entity.components.IntegerComponent;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CopyableComponentTest {

    @Test
    public void testCopy() {
        HPComponent hp1 = new HPComponent(300, 50.0);
        HPComponent hp2 = hp1.copy();

        assertThat(hp1, is(not(hp2)));
        assertTrue(hp1.equivalent(hp2));
    }

    private class HPComponent extends IntegerComponent implements CopyableComponent<HPComponent> {

        private double extraData;

        public HPComponent(int value, double extraData) {
            super(value);
            this.extraData = extraData;
        }

        @Override
        public HPComponent copy() {
            return new HPComponent(getValue(), extraData);
        }

        public boolean equivalent(HPComponent other) {
            return getValue() == other.getValue() && extraData == other.extraData;
        }
    }
}
