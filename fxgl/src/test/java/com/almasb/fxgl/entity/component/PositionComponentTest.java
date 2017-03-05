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

package com.almasb.fxgl.entity.component;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PositionComponentTest {

    private PositionComponent position;

    @Before
    public void setUp() throws Exception {
        position = new PositionComponent();
    }

    @Test
    public void testTranslateX() throws Exception {
        assertEquals(0, position.getX(), 0);

        position.translateX(100);
        assertEquals(100, position.getX(), 0);

        position.translateX(100);
        assertEquals(200, position.getX(), 0);

        position.translateX(-250);
        assertEquals(-50, position.getX(), 0);
    }

    @Test
    public void testTranslateY() throws Exception {
        assertEquals(0, position.getY(), 0);

        position.translateY(100);
        assertEquals(100, position.getY(), 0);

        position.translateY(100);
        assertEquals(200, position.getY(), 0);

        position.translateY(-250);
        assertEquals(-50, position.getY(), 0);
    }

    @Test
    public void testDistance() throws Exception {
        PositionComponent position2 = new PositionComponent();
        assertEquals(0, position.distance(position2), 0);

        position2.setValue(100, 0);
        assertEquals(100, position.distance(position2), 0);

        position2.setValue(0, 100);
        assertEquals(100, position.distance(position2), 0);

        position.setValue(25, 25);
        position2.setValue(50, 50);
        assertEquals(35, position.distance(position2), 0.5);
    }
}