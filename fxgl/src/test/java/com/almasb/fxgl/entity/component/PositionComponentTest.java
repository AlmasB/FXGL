/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
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