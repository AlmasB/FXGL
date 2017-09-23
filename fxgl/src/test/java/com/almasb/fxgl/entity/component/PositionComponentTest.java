/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PositionComponentTest {

    private PositionComponent position;

    @BeforeEach
    public void setUp() throws Exception {
        position = new PositionComponent();
    }

    @Test
    public void testTranslateX() throws Exception {
        assertThat(position.getX(), is(0.0));

        position.translateX(100);
        assertThat(position.getX(), is(100.0));

        position.translateX(100);
        assertThat(position.getX(), is(200.0));

        position.translateX(-250);
        assertThat(position.getX(), is(-50.0));
    }

    @Test
    public void testTranslateY() throws Exception {
        assertThat(position.getY(), is(0.0));

        position.translateY(100);
        assertThat(position.getY(), is(100.0));

        position.translateY(100);
        assertThat(position.getY(), is(200.0));

        position.translateY(-250);
        assertThat(position.getY(), is(-50.0));
    }

    @Test
    public void testDistance() throws Exception {
        PositionComponent position2 = new PositionComponent();
        assertThat(position.distance(position2), is(0.0));

        position2.setValue(100, 0);
        assertThat(position.distance(position2), is(100.0));

        position2.setValue(0, 100);
        assertThat(position.distance(position2), is(100.0));

        position.setValue(25, 25);
        position2.setValue(50, 50);
        assertEquals(35.0, position.distance(position2), 0.5);
    }
}