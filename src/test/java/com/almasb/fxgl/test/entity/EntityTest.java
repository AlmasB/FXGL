/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.test.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;
import com.almasb.fxgl.test.TestGameApplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;

public class EntityTest {

    private enum Type implements EntityType {
        TEST_ENTITY
    }

    @Before
    public void setUp() {
        Thread t = new Thread(() ->
            Application.launch(TestGameApplication.class, new String[] {}));
        t.setDaemon(true);
        t.start();
    }

    @After
    public void tearDown() {
        Platform.exit();
    }

    @Test
    public void essentials() {
        Entity entity = new Entity(Type.TEST_ENTITY);

        assertTrue(entity.isAlive());
        assertFalse(entity.isActive());

        assertEquals(Point2D.ZERO, entity.getPosition());

        assertEquals(Type.TEST_ENTITY, entity.getEntityType());
        assertEquals(Type.TEST_ENTITY.getUniqueType(), entity.getTypeAsString());
    }
}
