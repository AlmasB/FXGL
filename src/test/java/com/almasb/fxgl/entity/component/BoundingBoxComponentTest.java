/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.easyio.serialization.Bundle;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.MockApplicationModule;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BoundingBoxComponentTest {

    private PositionComponent position;
    private BoundingBoxComponent bbox;
    private Entity entity;

    @BeforeClass
    public static void before() {
        FXGL.configure(MockApplicationModule.get());
    }

    @Before
    public void setUp() throws Exception {
        position = new PositionComponent();
        bbox = new BoundingBoxComponent();

        entity = new Entity();
        entity.addComponent(position);
        entity.addComponent(bbox);
    }

    @Test
    public void testRemoveHitBox() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(1, bbox.hitBoxesProperty().size());

        bbox.removeHitBox("arm");
        assertEquals(1, bbox.hitBoxesProperty().size());

        bbox.removeHitBox("ARM");
        assertEquals(0, bbox.hitBoxesProperty().size());
    }

    @Test
    public void testGetWidth() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(40, bbox.getWidth(), 0);

        bbox.addHitBox(new HitBox("ARM2", new Point2D(50, 0), BoundingShape.box(40, 40)));
        assertEquals(90, bbox.getWidth(), 0);

        bbox.addHitBox(new HitBox("ARM3", BoundingShape.box(100, 40)));
        assertEquals(100, bbox.getWidth(), 0);

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");
        bbox.removeHitBox("ARM3");

        bbox.addHitBox(new HitBox("ARM", new Point2D(90, 0), BoundingShape.box(100, 40)));
        assertEquals(100, bbox.getWidth(), 0);
    }

    @Test
    public void testGetHeight() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(40, bbox.getHeight(), 0);

        bbox.addHitBox(new HitBox("ARM2", new Point2D(0, 50), BoundingShape.box(40, 40)));
        assertEquals(90, bbox.getHeight(), 0);

        bbox.addHitBox(new HitBox("ARM3", BoundingShape.box(40, 100)));
        assertEquals(100, bbox.getHeight(), 0);

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");
        bbox.removeHitBox("ARM3");

        bbox.addHitBox(new HitBox("ARM", new Point2D(0, 90), BoundingShape.box(40, 100)));
        assertEquals(100, bbox.getHeight(), 0);
    }

    @Test
    public void testGetMinXLocal() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(0, bbox.getMinXLocal(), 0);

        bbox.removeHitBox("ARM");

        bbox.addHitBox(new HitBox("ARM2", new Point2D(20, 0), BoundingShape.box(40, 40)));
        assertEquals(20, bbox.getMinXLocal(), 0);

        position.translateX(100);
        assertEquals(20, bbox.getMinXLocal(), 0);
    }

    @Test
    public void testGetMinYLocal() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(0, bbox.getMinYLocal(), 0);

        bbox.removeHitBox("ARM");

        bbox.addHitBox(new HitBox("ARM2", new Point2D(0, 20), BoundingShape.box(40, 40)));
        assertEquals(20, bbox.getMinYLocal(), 0);

        position.translateY(100);
        assertEquals(20, bbox.getMinYLocal(), 0);
    }

    @Test
    public void testGetMinXWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(20, 0), BoundingShape.box(40, 40)));
        assertEquals(20, bbox.getMinXWorld(), 0);

        position.translateX(100);
        assertEquals(120, bbox.getMinXWorld(), 0);
    }

    @Test
    public void testGetMinYWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(0, 20), BoundingShape.box(40, 40)));
        assertEquals(20, bbox.getMinYWorld(), 0);

        position.translateY(100);
        assertEquals(120, bbox.getMinYWorld(), 0);
    }

    @Test
    public void testGetMaxXWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(40, bbox.getMaxXWorld(), 0);

        bbox.addHitBox(new HitBox("ARM2", new Point2D(50, 0), BoundingShape.box(40, 40)));
        assertEquals(90, bbox.getMaxXWorld(), 0);

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");

        bbox.addHitBox(new HitBox("ARM", new Point2D(20, 0), BoundingShape.box(40, 40)));
        assertEquals(60, bbox.getMaxXWorld(), 0);

        position.translateX(100);
        assertEquals(160, bbox.getMaxXWorld(), 0);
    }

    @Test
    public void testGetMaxYWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(40, bbox.getMaxYWorld(), 0);

        bbox.addHitBox(new HitBox("ARM2", new Point2D(0, 50), BoundingShape.box(40, 40)));
        assertEquals(90, bbox.getMaxYWorld(), 0);

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");

        bbox.addHitBox(new HitBox("ARM", new Point2D(0, 20), BoundingShape.box(40, 40)));
        assertEquals(60, bbox.getMaxYWorld(), 0);

        position.translateY(100);
        assertEquals(160, bbox.getMaxYWorld(), 0);
    }

    @Test
    public void testGetCenterLocal() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 60)));
        assertEquals(new Point2D(20, 30), bbox.getCenterLocal());

        bbox.addHitBox(new HitBox("ARM2", new Point2D(20, 50), BoundingShape.box(40, 40)));
        assertEquals(new Point2D(30, 45), bbox.getCenterLocal());

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");

        bbox.addHitBox(new HitBox("ARM", new Point2D(50, 100), BoundingShape.box(40, 60)));
        assertEquals(new Point2D(20, 30), bbox.getCenterLocal());

        position.translate(100, 100);
        assertEquals(new Point2D(20, 30), bbox.getCenterLocal());
    }

    @Test
    public void testGetCenterWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 60)));
        assertEquals(new Point2D(20, 30), bbox.getCenterWorld());

        bbox.addHitBox(new HitBox("ARM2", new Point2D(20, 50), BoundingShape.box(40, 40)));
        assertEquals(new Point2D(30, 45), bbox.getCenterWorld());

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");

        bbox.addHitBox(new HitBox("ARM", new Point2D(50, 100), BoundingShape.box(40, 60)));
        assertEquals(new Point2D(70, 130), bbox.getCenterWorld());

        position.translate(100, 100);
        assertEquals(new Point2D(170, 230), bbox.getCenterWorld());
    }

    @Test
    public void testCheckCollision() throws Exception {

    }

    @Test
    public void testIsCollidingWith() throws Exception {

    }

    @Test
    public void testIsWithin() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(50, 50), BoundingShape.box(40, 60)));

        assertTrue(bbox.isWithin(50, 50, 60, 60));
        assertTrue(bbox.isWithin(55, 55, 60, 60));
        assertTrue(bbox.isWithin(0, 0, 50, 60));
        assertTrue(!bbox.isWithin(100, 50, 140, 60));
        assertTrue(!bbox.isWithin(50, 120, 90, 60));

        assertTrue(bbox.isWithin(0, 0, 51, 51));
        assertTrue(bbox.isWithin(0, 0, 50, 50));
        assertTrue(!bbox.isWithin(0, 0, 49, 49));
        assertTrue(!bbox.isWithin(91, 0, 49, 49));
    }

    @Test
    public void testIsOutside() throws Exception {

    }

    @Test
    public void testRange() throws Exception {

    }

    @Test
    public void testSerialization() {
        bbox.addHitBox(new HitBox("BOX", BoundingShape.box(30, 40)));

        // write
        Bundle bundle = new Bundle("BBOXTest");
        bbox.write(bundle);

        // read
        BoundingBoxComponent bbox2 = new BoundingBoxComponent();
        bbox2.read(bundle);

        assertThat(bbox2.getWidth(), is(30.0));
        assertThat(bbox2.getHeight(), is(40.0));

        List<HitBox> boxes = bbox2.hitBoxesProperty();

        assertThat(boxes.size(), is(1));
        assertThat(boxes.get(0).getName(), is("BOX"));
        assertThat(boxes.get(0).getWidth(), is(30.0));
        assertThat(boxes.get(0).getHeight(), is(40.0));
    }
}
