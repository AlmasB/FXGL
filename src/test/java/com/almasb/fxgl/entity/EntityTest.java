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
package com.almasb.fxgl.entity;

import static org.junit.Assert.*;

import java.util.Optional;

import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.BoundingBox;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.almasb.fxgl.GameApplication;
import com.almasb.fxgl.TestGameApplication;
import com.almasb.fxgl.entity.AbstractControl;
import com.almasb.fxgl.entity.Component;
import com.almasb.fxgl.entity.DoubleComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityType;

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
        assertFalse(testEntity.isCollidable());

        assertEquals(Point2D.ZERO, testEntity.getPosition());

        assertEquals(Type.TEST_ENTITY, testEntity.getEntityType());
        assertEquals(Type.TEST_ENTITY.getUniqueType(), testEntity.getTypeAsString());
        assertTrue(testEntity.isType(Type.TEST_ENTITY));

        testEntity.translate(15, 20);
        assertEquals(15, testEntity.getX(), 0.1);
        assertEquals(20, testEntity.getY(), 0.1);
        assertEquals(new Point2D(15, 20), testEntity.getPosition());

        testEntity.setX(50);
        assertEquals(50, testEntity.getX(), 0.1);
        testEntity.setY(300);
        assertEquals(300, testEntity.getY(), 0.1);

        testEntity.rotateBy(50);
        assertEquals(50, testEntity.getRotation(), 0.1);
        testEntity.rotateBy(45);
        assertEquals(95, testEntity.getRotation(), 0.1);
        testEntity.setRotation(-30);
        assertEquals(-30, testEntity.getRotation(), 0.1);

        assertFalse(testEntity.isXFlipped());
        testEntity.setXFlipped(true);
        assertTrue(testEntity.isXFlipped());
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

        testEntity.removeAllControls();
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

    @Test
    public void getWidth() {
        assertEquals(0, testEntity.getWidth(), 0.1);

        testEntity.addHitBox(new HitBox("TEST", new BoundingBox(0, 0, 40, 40)));
        assertEquals(40, testEntity.getWidth(), 0.1);

        testEntity.translate(50, 0);
        assertEquals(40, testEntity.getWidth(), 0.1);

        testEntity.addHitBox(new HitBox("TEST2", new BoundingBox(20, 0, 40, 20)));
        assertEquals(60, testEntity.getWidth(), 0.1);
    }

    @Test
    public void getHeight() {
        assertEquals(0, testEntity.getHeight(), 0.1);

        testEntity.addHitBox(new HitBox("TEST", new BoundingBox(0, 0, 40, 40)));
        assertEquals(40, testEntity.getHeight(), 0.1);

        testEntity.translate(0, 50);
        assertEquals(40, testEntity.getHeight(), 0.1);

        testEntity.addHitBox(new HitBox("TEST2", new BoundingBox(0, 30, 40, 20)));
        assertEquals(50, testEntity.getHeight(), 0.1);
    }

    @Test
    public void rotateToVector() {
        assertEquals(0, testEntity.getRotation(), 0.1);

        testEntity.rotateToVector(new Point2D(1, 1));
        assertEquals(45, testEntity.getRotation(), 0.1);

        testEntity.rotateToVector(new Point2D(0, 1));
        assertEquals(90, testEntity.getRotation(), 0.1);

        testEntity.rotateToVector(new Point2D(-1, 0));
        assertEquals(180, testEntity.getRotation(), 0.1);

        testEntity.rotateToVector(new Point2D(-1, -1));
        assertEquals(-135, testEntity.getRotation(), 0.1);
    }

    private class TestControl extends AbstractControl {

        @Override
        protected void initEntity(Entity entity) {
            assertEquals(testEntity, entity);
        }

        @Override
        public void onUpdate(Entity entity) {

        }
    }

    private class HPComponent extends DoubleComponent {
        public HPComponent(double value) {
            super(value);
        }
    }
}
