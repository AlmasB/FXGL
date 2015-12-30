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

package com.almasb.fxgl.gameplay;

public class GameWorldTest {

//    private enum Type implements EntityType {
//        TEST_ENTITY
//    }
//
//    private GameWorld gameWorld;
//
//    @Before
//    public void setUp() {
//        //gameWorld = new GameWorld();
//    }
//
//    @Test
//    public void addRemoveEntities() {
//        Entity entity = new Entity(Type.TEST_ENTITY);
//
//        gameWorld.addEntities(entity);
//        assertEquals(0, gameWorld.getEntities().size());
//
//        gameWorld.update();
//        assertEquals(1, gameWorld.getEntities().size());
//
//        List<Entity> list = gameWorld.getEntities(Type.TEST_ENTITY);
//        assertEquals(1, list.size());
//        assertEquals(entity, list.get(0));
//
//
//
////        list = gameWorld.getEntitiesInRange(new Rectangle2D(50, 50, 100, 100));
////        assertEquals(1, list.size());
////        assertEquals(entity, list.get(0));
//
//        gameWorld.removeEntity(entity);
//        assertEquals(1, gameWorld.getEntities().size());
//
//        gameWorld.update();
//        assertEquals(0, gameWorld.getEntities().size());
//    }
//
//    @Test
//    public void getEntityAt() {
//        Entity entity = new Entity(Type.TEST_ENTITY);
//        entity.setPosition(100, 100);
//
//        gameWorld.addEntities(entity);
//        assertEquals(0, gameWorld.getEntities().size());
//
//        gameWorld.update();
//        assertEquals(1, gameWorld.getEntities().size());
//
//        Optional<Entity> maybe = gameWorld.getEntityAt(new Point2D(100, 100));
//        assertTrue(maybe.isPresent());
//        assertEquals(entity, maybe.get());
//    }
//
//    @Test
//    public void getEntitiesFiltered() {
//        Entity entity = new Entity(Type.TEST_ENTITY);
//        entity.setPosition(100, 100);
//        entity.setSceneView(new Rectangle(40, 40));
//
//        gameWorld.addEntities(entity);
//        assertEquals(0, gameWorld.getEntities().size());
//
//        gameWorld.update();
//        assertEquals(1, gameWorld.getEntities().size());
//
//        List<Entity> list = gameWorld.getEntitiesFiltered(e -> e.getPosition().equals(new Point2D(100, 100)));
//        assertEquals(1, list.size());
//        assertEquals(entity, list.get(0));
//    }
//
//    @Test
//    public void getEntitiesInRange() {
//        Entity entity = new Entity(Type.TEST_ENTITY);
//        entity.setPosition(100, 100);
//        entity.setSceneView(new Rectangle(40, 40));
//
//        Entity entity2 = new Entity(Type.TEST_ENTITY);
//        entity2.setPosition(200, 100);
//        entity2.setSceneView(new Rectangle(40, 40));
//
//        Entity entity3 = new Entity(Type.TEST_ENTITY);
//        entity3.setPosition(300, 100);
//        entity3.setSceneView(new Rectangle(40, 40));
//
//        gameWorld.addEntities(entity, entity2, entity3);
//        gameWorld.update();
//
//        List<Entity> list = gameWorld.getEntitiesInRange(new Rectangle2D(150, 50, 100, 150));
//        assertEquals(1, list.size());
//        assertEquals(entity2, list.get(0));
//
//        list = gameWorld.getEntitiesInRange(new Rectangle2D(150, 50, 180, 150));
//        assertEquals(2, list.size());
//        assertListContains(list, entity2, entity3);
//
//        list = gameWorld.getEntitiesInRange(new Rectangle2D(100, 50, 300, 150));
//        assertEquals(3, list.size());
//        assertListContains(list, entity, entity2, entity3);
//
//        list = gameWorld.getEntitiesInRange(new Rectangle2D(0, 0, 50, 50));
//        assertEquals(0, list.size());
//    }
//
//    @Test
//    public void reset() {
//        Entity entity = new Entity(Type.TEST_ENTITY);
//
//        gameWorld.addEntities(entity);
//        gameWorld.update();
//
//        gameWorld.reset();
//        assertEquals(0, gameWorld.getEntities().size());
//    }
//
//    @Test
//    public void notifications() {
//        Entity entity = new Entity(Type.TEST_ENTITY);
//        assertFalse(entity.isActive());
//
//        gameWorld.addEntity(entity);
//        assertFalse(entity.isActive());
//
//        gameWorld.update();
//        assertTrue(entity.isActive());
//        assertEquals(gameWorld, entity.getWorld());
//    }
//
//    private static boolean assertListContains(List<?> list, Object... objects) {
//        return list.containsAll(Arrays.asList(objects));
//    }
}
