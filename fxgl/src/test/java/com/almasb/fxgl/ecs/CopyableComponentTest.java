/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.ecs;

import com.almasb.fxgl.ecs.component.IntegerComponent;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
