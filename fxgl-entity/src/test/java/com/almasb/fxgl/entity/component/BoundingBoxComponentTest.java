/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.component;

import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionResult;
import com.almasb.fxgl.physics.HitBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BoundingBoxComponentTest {

    private TransformComponent position;
    private BoundingBoxComponent bbox;

    @BeforeEach
    public void setUp() throws Exception {
        Entity entity = new Entity();

        position = entity.getTransformComponent();
        bbox = entity.getBoundingBoxComponent();
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
    public void testClearHitBoxes() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertEquals(1, bbox.hitBoxesProperty().size());

        bbox.clearHitBoxes();
        assertEquals(0, bbox.hitBoxesProperty().size());
    }

    @Test
    public void testGetWidth() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertThat(bbox.getWidth(), is(40.0));

        bbox.addHitBox(new HitBox("ARM2", new Point2D(50, 0), BoundingShape.box(40, 40)));
        assertThat(bbox.getWidth(), is(90.0));

        bbox.addHitBox(new HitBox("ARM3", BoundingShape.box(100, 40)));
        assertThat(bbox.getWidth(), is(100.0));

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");
        bbox.removeHitBox("ARM3");

        bbox.addHitBox(new HitBox("ARM", new Point2D(90, 0), BoundingShape.box(100, 40)));
        assertThat(bbox.getWidth(), is(100.0));
    }

    @Test
    public void testGetHeight() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertThat(bbox.getHeight(), is(40.0));

        bbox.addHitBox(new HitBox("ARM2", new Point2D(0, 50), BoundingShape.box(40, 40)));
        assertThat(bbox.getHeight(), is(90.0));

        bbox.addHitBox(new HitBox("ARM3", BoundingShape.box(40, 100)));
        assertThat(bbox.getHeight(), is(100.0));

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");
        bbox.removeHitBox("ARM3");

        bbox.addHitBox(new HitBox("ARM", new Point2D(0, 90), BoundingShape.box(40, 100)));
        assertThat(bbox.getHeight(), is(100.0));
    }

    @Test
    public void testMaxLocals() {
        bbox.addHitBox(new HitBox(BoundingShape.box(40, 20)));

        assertThat(bbox.getMaxXLocal(), is(40.0));
        assertThat(bbox.getMaxYLocal(), is(20.0));
    }

    @Test
    public void testGetMinXLocal() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertThat(bbox.getMinXLocal(), is(0.0));

        bbox.removeHitBox("ARM");

        bbox.addHitBox(new HitBox("ARM2", new Point2D(20, 0), BoundingShape.box(40, 40)));
        assertThat(bbox.getMinXLocal(), is(20.0));

        position.translateX(100);
        assertThat(bbox.getMinXLocal(), is(20.0));
        assertThat(bbox.minXLocalProperty().get(), is(20.0));
    }

    @Test
    public void testGetMinYLocal() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertThat(bbox.getMinYLocal(), is(0.0));

        bbox.removeHitBox("ARM");

        bbox.addHitBox(new HitBox("ARM2", new Point2D(0, 20), BoundingShape.box(40, 40)));
        assertThat(bbox.getMinYLocal(), is(20.0));

        position.translateY(100);
        assertThat(bbox.getMinYLocal(), is(20.0));
        assertThat(bbox.minYLocalProperty().get(), is(20.0));
    }

    @Test
    public void testGetMinXWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(20, 0), BoundingShape.box(40, 40)));
        assertThat(bbox.getMinXWorld(), is(20.0));

        position.translateX(100);
        assertThat(bbox.getMinXWorld(), is(120.0));
        assertThat(bbox.minXWorldProperty().get(), is(120.0));
    }

    @Test
    public void testGetMinYWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(0, 20), BoundingShape.box(40, 40)));
        assertThat(bbox.getMinYWorld(), is(20.0));

        position.translateY(100);
        assertThat(bbox.getMinYWorld(), is(120.0));
        assertThat(bbox.minYWorldProperty().get(), is(120.0));
    }

    @Test
    public void testGetMaxXWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertThat(bbox.getMaxXWorld(), is(40.0));

        bbox.addHitBox(new HitBox("ARM2", new Point2D(50, 0), BoundingShape.box(40, 40)));
        assertThat(bbox.getMaxXWorld(), is(90.0));

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");

        bbox.addHitBox(new HitBox("ARM", new Point2D(20, 0), BoundingShape.box(40, 40)));
        assertThat(bbox.getMaxXWorld(), is(60.0));

        position.translateX(100);
        assertThat(bbox.getMaxXWorld(), is(160.0));
        assertThat(bbox.maxXWorldProperty().get(), is(160.0));
    }

    @Test
    public void testGetMaxYWorld() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 40)));
        assertThat(bbox.getMaxYWorld(), is(40.0));

        bbox.addHitBox(new HitBox("ARM2", new Point2D(0, 50), BoundingShape.box(40, 40)));
        assertThat(bbox.getMaxYWorld(), is(90.0));

        bbox.removeHitBox("ARM");
        bbox.removeHitBox("ARM2");

        bbox.addHitBox(new HitBox("ARM", new Point2D(0, 20), BoundingShape.box(40, 40)));
        assertThat(bbox.getMaxYWorld(), is(60.0));

        position.translateY(100);
        assertThat(bbox.getMaxYWorld(), is(160.0));
        assertThat(bbox.maxYWorldProperty().get(), is(160.0));
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
        bbox.addHitBox(new HitBox("TEST", BoundingShape.box(40, 40)));

        Entity entity2 = new Entity();
        entity2.getBoundingBoxComponent().addHitBox(new HitBox("TEST2", BoundingShape.box(40, 40)));

        CollisionResult result = new CollisionResult();
        var collision = bbox.checkCollision(entity2.getBoundingBoxComponent(), result);

        assertThat(collision, is(true));
        assertThat(result.getBoxA(), is(bbox.hitBoxesProperty().get(0)));
        assertThat(result.getBoxB(), is(entity2.getBoundingBoxComponent().hitBoxesProperty().get(0)));

        Entity entity3 = new Entity();
        entity3.getBoundingBoxComponent().addHitBox(new HitBox("TEST3", new Point2D(45, 0), BoundingShape.box(40, 40)));

        collision = bbox.checkCollision(entity3.getBoundingBoxComponent(), result);
        assertThat(collision, is(false));
    }

    @Test
    public void testIsCollidingWith() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(50, 50), BoundingShape.box(40, 60)));

        Entity entity2 = new Entity();
        entity2.getBoundingBoxComponent().addHitBox(new HitBox("test", BoundingShape.box(50, 50)));

        Entity entity3 = new Entity();
        entity3.getBoundingBoxComponent().addHitBox(new HitBox("test", BoundingShape.box(51, 51)));

        Entity entity4 = new Entity();
        entity4.getBoundingBoxComponent().addHitBox(new HitBox("test", BoundingShape.box(49, 49)));

        assertTrue(bbox.isCollidingWith(entity2.getBoundingBoxComponent()));
        assertTrue(bbox.isCollidingWith(entity3.getBoundingBoxComponent()));
        assertFalse(bbox.isCollidingWith(entity4.getBoundingBoxComponent()));
    }

    @Test
    public void testIsCollidingWithAngles() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 60)));

        Entity entity2 = new Entity();
        entity2.getBoundingBoxComponent().addHitBox(new HitBox("test", BoundingShape.box(50, 50)));
        entity2.setPosition(50, 0);
        entity2.getTransformComponent().setRotationOrigin(new Point2D(0, 0));
        entity2.setRotation(91);

        Entity entity3 = new Entity();
        entity3.getBoundingBoxComponent().addHitBox(new HitBox("test", BoundingShape.box(50, 50)));
        entity3.setPosition(50, 0);

        // both identical but e2 is rotated and e3 is not
        assertTrue(bbox.isCollidingWith(entity2.getBoundingBoxComponent()));
        assertFalse(bbox.isCollidingWith(entity3.getBoundingBoxComponent()));
    }

    @Test
    public void testIsWithin() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(50, 50), BoundingShape.box(40, 60)));

        assertTrue(bbox.isWithin(50, 50, 60, 60));
        assertTrue(bbox.isWithin(55, 55, 60, 60));
        assertFalse(bbox.isWithin(0, 0, 50, 60));
        assertFalse(bbox.isWithin(100, 50, 140, 60));
        assertFalse(bbox.isWithin(50, 120, 90, 60));

        assertTrue(bbox.isWithin(0, 0, 51, 51));
        assertFalse(bbox.isWithin(0, 0, 50, 50));
        assertFalse(bbox.isWithin(0, 0, 49, 49));
        assertFalse(bbox.isWithin(91, 0, 49, 49));
    }

    @Test
    public void testIsOutside() throws Exception {
        bbox.addHitBox(new HitBox("ARM", BoundingShape.box(40, 60)));

        assertTrue(bbox.isOutside(-10, -10, 0, 0));
        assertTrue(bbox.isOutside(40, 60, 100, 100));
        assertTrue(bbox.isOutside(-10, 0, 0, 60));
        assertFalse(bbox.isOutside(10, 10, 20, 20));

        assertTrue(bbox.isOutside(new Rectangle2D(-10, 0, 0, 60)));
        assertFalse(bbox.isOutside(new Rectangle2D(10, 10, 20, 20)));
    }

    @Test
    public void testRange() throws Exception {
        bbox.addHitBox(new HitBox("ARM", new Point2D(15, 15), BoundingShape.box(40, 60)));

        Rectangle2D range = bbox.range(10, 10);

        assertThat(range.getMinX(), is(15.0 - 10));
        assertThat(range.getMinY(), is(15.0 - 10));
        assertThat(range.getMaxX(), is(15.0 + 40 + 10));
        assertThat(range.getMaxY(), is(15.0 + 60 + 10));
    }

    @Test
    public void testSerialization() {
        bbox.addHitBox(new HitBox("BOX", BoundingShape.box(30, 40)));

        // write
        Bundle bundle = new Bundle("BBOXTest");
        bbox.write(bundle);

        Entity e2 = new Entity();
        // read
        BoundingBoxComponent bbox2 = e2.getBoundingBoxComponent();
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
