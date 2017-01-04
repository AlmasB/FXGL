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

package com.almasb.fxgl.gameplay;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.MockApplicationModule;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.IDComponent;
import com.almasb.fxgl.event.EventTrigger;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.time.UpdateEvent;
import com.almasb.gameutils.collection.Array;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Rectangle;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GameWorldTest {

    private enum TestType {
        T1, T2, T3, T4
    }

    private GameWorld gameWorld;

    private Entity e1, e10, e11, e2, e3, e4;
    private Entity genericEntity;

    @BeforeClass
    public static void before() {
        FXGL.configure(MockApplicationModule.get());
    }

    @Before
    public void setUp() {
        gameWorld = new GameWorld();

        EntityView view = new EntityView(new Rectangle(10, 10));
        view.setRenderLayer(new RenderLayer() {
            @Override
            public String name() {
                return "TEST";
            }

            @Override
            public int index() {
                return 0;
            }
        });

        e1 = Entities.builder()
                .type(TestType.T1)
                .at(100, 100)
                .bbox(new HitBox("TEST", BoundingShape.box(10, 10)))
                .viewFromNode(view)
                .with(new IDComponent("e1", 0))
                .buildAndAttach(gameWorld);

        e10 = Entities.builder()
                .type(TestType.T1)
                .at(100, 105)
                .bbox(new HitBox("TEST", BoundingShape.box(10, 10)))
                .with(new IDComponent("e1", 1))
                .buildAndAttach(gameWorld);

        e11 = Entities.builder()
                .type(TestType.T1)
                .at(100, 110)
                .bbox(new HitBox("TEST", BoundingShape.box(10, 10)))
                .with(new IDComponent("e1", 2))
                .buildAndAttach(gameWorld);

        e2 = Entities.builder()
                .type(TestType.T2)
                .at(150, 100)
                .bbox(new HitBox("TEST", BoundingShape.box(10, 10)))
                .with(new IDComponent("e2", 0))
                .buildAndAttach(gameWorld);

        e3 = Entities.builder()
                .type(TestType.T3)
                .at(200, 100)
                .bbox(new HitBox("TEST", BoundingShape.box(10, 10)))
                .buildAndAttach(gameWorld);

        e4 = Entities.builder()
                .type(TestType.T4)
                .at(250, 100)
                .bbox(new HitBox("TEST", BoundingShape.box(10, 10)))
                .buildAndAttach(gameWorld);

        genericEntity = new Entity();
        gameWorld.addEntity(genericEntity);

        gameWorld.onUpdateEvent(new UpdateEvent(1, 0.016));
    }

    @Test
    public void testGetEntitiesByType() throws Exception {
        List<Entity> list = gameWorld.getEntitiesByType();
        assertThat(list, hasItems(e1, e10, e11, e2, e3, e4, genericEntity));

        list = gameWorld.getEntitiesByType(TestType.T1);
        assertThat(list, is(Arrays.asList(e1, e10, e11)));

        list = gameWorld.getEntitiesByType(TestType.T2);
        assertThat(list, is(Collections.singletonList(e2)));

        list = gameWorld.getEntitiesByType(TestType.T3);
        assertThat(list, is(Collections.singletonList(e3)));

        list = gameWorld.getEntitiesByType(TestType.T4);
        assertThat(list, is(Collections.singletonList(e4)));

        Array<Entity> result = new Array<>(8);
        gameWorld.getEntitiesByType(result, TestType.T1);

        assertThat(result.size(), is(3));
        assertThat(result, hasItems(e1, e10, e11));

        result.clear();
        gameWorld.getEntitiesByType(result, TestType.T2);

        assertThat(result.size(), is(1));
        assertThat(result, hasItems(e2));

        result.clear();
        gameWorld.getEntitiesByType(result, TestType.T3);

        assertThat(result.size(), is(1));
        assertThat(result, hasItems(e3));
    }

    @Test
    public void testGetClosestEntity() throws Exception {
        assertThat(gameWorld.getClosestEntity(e1, e -> Entities.getType(e)
                .isType(TestType.T2))
                .get(),
                is(e2));

        assertThat(gameWorld.getClosestEntity(e1, e -> Entities.getType(e)
                .isType(TestType.T1))
                .get(),
                is(e10));

        assertThat(gameWorld.getClosestEntity(e2, e -> Entities.getType(e)
                .isType(TestType.T2)), is(Optional.empty()));
    }

    @Test
    public void testGetEntitiesFiltered() throws Exception {
        assertThat(gameWorld.getEntitiesFiltered(e -> Entities.getPosition(e) != null && Entities.getPosition(e).getX() > 150),
                is(Arrays.asList(e3, e4)));

        assertThat(gameWorld.getEntitiesFiltered(e -> Entities.getPosition(e) != null && Entities.getPosition(e).getY() < 105),
                is(Arrays.asList(e1, e2, e3, e4)));

        Array<Entity> result = new Array<>(8);
        gameWorld.getEntitiesFiltered(result, e -> Entities.getPosition(e) != null && Entities.getPosition(e).getX() > 150);

        assertThat(result.size(), is(2));
        assertThat(result, hasItems(e3, e4));

        result.clear();
        gameWorld.getEntitiesFiltered(result, e -> Entities.getPosition(e) != null && Entities.getPosition(e).getY() < 105);

        assertThat(result.size(), is(4));
        assertThat(result, hasItems(e1, e2, e3, e4));
    }

    @Test
    public void testGetEntitiesInRange() throws Exception {
        assertThat(gameWorld.getEntitiesInRange(new Rectangle2D(130, 50, 100, 100)),
                is(Arrays.asList(e2, e3)));

        Array<Entity> result = new Array<>(8);

        gameWorld.getEntitiesInRange(result, 130, 50, 130+100, 50+100);
        assertThat(result.size(), is(2));
        assertThat(result, hasItems(e2, e3));
    }

    @Test
    public void testGetCollidingEntities() {
        assertThat(gameWorld.getCollidingEntities(e1), is(Arrays.asList(e10, e11)));

        Array<Entity> result = new Array<>(8);
        gameWorld.getCollidingEntities(result, e1);

        assertThat(result.size(), is(2));
        assertThat(result, hasItems(e10, e11));
    }

    @Test
    public void testGetEntitiesByLayer() throws Exception {
        assertThat(gameWorld.getEntitiesByLayer(new RenderLayer() {
            @Override
            public String name() {
                return "TEST";
            }

            @Override
            public int index() {
                return 0;
            }
        }), is(Collections.singletonList(e1)));

        assertThat(gameWorld.getEntitiesByLayer(RenderLayer.TOP),
                is(Arrays.asList(e10, e11, e2, e3, e4)));

        Array<Entity> result = new Array<>(8);

        gameWorld.getEntitiesByLayer(result, RenderLayer.TOP);
        assertThat(result.size(), is(5));
        assertThat(result, hasItems(e10, e11, e2, e3, e4));
    }

    @Test
    public void testGetEntityAt() throws Exception {
        assertThat(gameWorld.getEntityAt(new Point2D(100, 100)).get(), is(e1));
        assertThat(gameWorld.getEntityAt(new Point2D(150, 100)).get(), is(e2));
        assertThat(gameWorld.getEntityAt(new Point2D(200, 100)).get(), is(e3));
        assertThat(gameWorld.getEntityAt(new Point2D(250, 100)).get(), is(e4));

        assertThat(gameWorld.getEntityAt(new Point2D(100.5, 100)), is(Optional.empty()));
    }

    @Test
    public void testGetEntityByID() {
        assertThat(gameWorld.getEntityByID("e1", 0).get(), is(e1));
        assertThat(gameWorld.getEntityByID("e1", 1).get(), is(e10));
        assertThat(gameWorld.getEntityByID("e1", 2).get(), is(e11));
        assertThat(gameWorld.getEntityByID("e2", 0).get(), is(e2));

        assertThat(gameWorld.getEntityByID("e3", 0), is(Optional.empty()));
    }

    @Test
    public void setLevel() {
        Level level = new Level(10, 10, Arrays.asList(e1, e2, e3, e4));

        GameWorld world = new GameWorld();
        world.setLevel(level);
        world.onUpdateEvent(new UpdateEvent(1, 0.016));

        assertThat(world.getEntities(), hasItems(e1, e2, e3, e4));
    }

//    @Test
//    public void testTriggers() {
//        IntegerProperty count = new SimpleIntegerProperty(0);
//
//        FXGL.getEventBus().addEventHandler(MyEvent.ANY, e -> {
//            count.set(count.get() + 1);
//        });
//
//        gameWorld.addEventTrigger(new EventTrigger<>(
//                () -> gameWorld.getEntities().size() < 6,
//                MyEvent::new,
//                2, Duration.millis(0)
//        ));
//
//        assertThat(count.get(), is(0));
//        gameWorld.onUpdateEvent(new UpdateEvent(2, 0.016));
//        assertThat(count.get(), is(1));
//
//        gameWorld.onUpdateEvent(new UpdateEvent(3, 0.016));
//        assertThat(count.get(), is(2));
//
//        // 2 times only
//        gameWorld.onUpdateEvent(new UpdateEvent(3, 0.016));
//        assertThat(count.get(), is(2));
//    }

    private static class MyEvent extends Event {

        public static final EventType<MyEvent> ANY = new EventType<>(Event.ANY, "MY_EVENT");

        public MyEvent() {
            super(ANY);
        }
    }
}
