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
package com.almasb.fxgl.test.entity;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.entity.AbstractControl;
import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.DoubleComponent;
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

    private Entity testEntity;

    @Before
    public void initEntity() {
        testEntity = new Entity(Type.TEST_ENTITY);
    }

    @Test
    public void essentials() throws Exception {
        assertTrue(testEntity.isAlive());
        assertFalse(testEntity.isActive());

        assertEquals(Point2D.ZERO, testEntity.getPosition());

        assertEquals(Type.TEST_ENTITY, testEntity.getEntityType());
        assertEquals(Type.TEST_ENTITY.getUniqueType(), testEntity.getTypeAsString());
        assertTrue(testEntity.isType(Type.TEST_ENTITY));
    }

    @Test
    public void controls() {
        TestControl control = new TestControl();
        testEntity.addControl(control);

        Optional<TestControl> maybe = testEntity.getControl(TestControl.class);
        assertTrue(maybe.isPresent());
        assertEquals(control, maybe.get());

        testEntity.removeControl(TestControl.class);
        assertFalse(testEntity.getControl(TestControl.class).isPresent());

        testEntity.addControl(control);
        maybe = testEntity.getControl(TestControl.class);
        assertTrue(maybe.isPresent());
        assertEquals(control, maybe.get());

        testEntity.removeControls();
        assertFalse(testEntity.getControl(TestControl.class).isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void controlsDuplicate() {
        testEntity.addControl(new TestControl());
        testEntity.addControl(new TestControl());
    }

    @Test
    public void components() {
        HPComponent hp = new HPComponent(100);
        testEntity.addComponent(hp);

        Optional<HPComponent> maybe = testEntity.getComponent(HPComponent.class);

        assertTrue(maybe.isPresent());
        assertEquals(hp, maybe.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void componentsAnonymous() {
        testEntity.addComponent(new Component() {});
        testEntity.addComponent(new DoubleComponent() {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void componentsDuplicate() {
        HPComponent hp1 = new HPComponent(100);
        HPComponent hp2 = new HPComponent(100);

        testEntity.addComponent(hp1);
        testEntity.addComponent(hp2);
    }

    private class TestControl extends AbstractControl {

        @Override
        protected void initEntity(Entity entity) {
            assertEquals(testEntity, entity);
        }

        @Override
        public void onUpdate(Entity entity, long now) {

        }
    }

    private class HPComponent extends DoubleComponent {
        public HPComponent(double value) {
            super(value);
        }
    }

    // private enum Type implements EntityType {
    // TEST_ENTITY
    // }
    //
    // private static GameApplication app;
    // private Entity testEntity;
    //
    // @BeforeClass
    // public static void setupFramework() {
    // Thread t = new Thread(() ->
    // Application.launch(TestGameApplication.class, new String[] {}));
    // t.setDaemon(true);
    // t.start();
    //
    // while ((app = TestGameApplication.getInstance()) == null) {
    // try {
    // Thread.sleep(10);
    // }
    // catch (InterruptedException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // @AfterClass
    // public static void exitFramework() {
    // Platform.exit();
    // }

}
