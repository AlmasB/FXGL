/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

import com.almasb.fxgl.annotation.Spawns
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import com.almasb.fxgl.core.collection.Array
import com.almasb.fxgl.entity.*
import com.almasb.fxgl.entity.component.IDComponent
import com.almasb.fxgl.entity.component.PositionComponent
import com.almasb.fxgl.gameplay.Level
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.event.Event
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.*
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.util.*

class GameWorldTest {

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    private enum class TestType {
        T1, T2, T3, T4
    }

    private lateinit var gameWorld: GameWorld

    private lateinit var e1: Entity
    private lateinit var e10: Entity
    private lateinit var e11: Entity
    private lateinit var e2: Entity
    private lateinit var e3: Entity
    private lateinit var e4: Entity
    private lateinit var genericEntity: Entity

    @Before
    fun setUp() {
        gameWorld = GameWorld()

        val view = EntityView(Rectangle(10.0, 10.0))
        view.renderLayer = object : RenderLayer {
            override fun name(): String {
                return "TEST"
            }

            override fun index(): Int {
                return 0
            }
        }

        e1 = Entities.builder()
                .type(TestType.T1)
                .at(100.0, 100.0)
                .bbox(HitBox("TEST", BoundingShape.box(10.0, 10.0)))
                .viewFromNode(view)
                .with(IDComponent("e1", 0))
                .buildAndAttach(gameWorld)

        e10 = Entities.builder()
                .type(TestType.T1)
                .at(100.0, 105.0)
                .bbox(HitBox("TEST", BoundingShape.box(10.0, 10.0)))
                .with(IDComponent("e1", 1))
                .buildAndAttach(gameWorld)

        e11 = Entities.builder()
                .type(TestType.T1)
                .at(100.0, 110.0)
                .bbox(HitBox("TEST", BoundingShape.box(10.0, 10.0)))
                .with(IDComponent("e1", 2))
                .buildAndAttach(gameWorld)

        e2 = Entities.builder()
                .type(TestType.T2)
                .at(150.0, 100.0)
                .bbox(HitBox("TEST", BoundingShape.box(10.0, 10.0)))
                .with(IDComponent("e2", 0))
                .buildAndAttach(gameWorld)

        e3 = Entities.builder()
                .type(TestType.T3)
                .at(200.0, 100.0)
                .bbox(HitBox("TEST", BoundingShape.box(10.0, 10.0)))
                .buildAndAttach(gameWorld)

        e4 = Entities.builder()
                .type(TestType.T4)
                .at(250.0, 100.0)
                .bbox(HitBox("TEST", BoundingShape.box(10.0, 10.0)))
                .buildAndAttach(gameWorld)

        genericEntity = Entity()
        gameWorld.addEntity(genericEntity)

        gameWorld.onUpdate(0.016)
    }

    @Test
    fun testGetEntitiesByType() {
        var list = gameWorld.getEntitiesByType()
        assertThat(list, hasItems<Entity>(e1, e10, e11, e2, e3, e4, genericEntity))

        list = gameWorld.getEntitiesByType(TestType.T1)
        assertThat(list, `is`(Arrays.asList<Entity>(e1, e10, e11)))

        list = gameWorld.getEntitiesByType(TestType.T2)
        assertThat(list, `is`(listOf<Entity>(e2)))

        list = gameWorld.getEntitiesByType(TestType.T3)
        assertThat(list, `is`(listOf<Entity>(e3)))

        list = gameWorld.getEntitiesByType(TestType.T4)
        assertThat(list, `is`(listOf<Entity>(e4)))

        val result = Array<Entity>(8)
        gameWorld.getEntitiesByType(result, TestType.T1)

        assertThat(result.size(), `is`(3))
        assertThat(result, hasItems(e1, e10, e11))

        result.clear()
        gameWorld.getEntitiesByType(result, TestType.T2)

        assertThat(result.size(), `is`(1))
        assertThat(result, hasItems(e2))

        result.clear()
        gameWorld.getEntitiesByType(result, TestType.T3)

        assertThat(result.size(), `is`(1))
        assertThat(result, hasItems(e3))
    }

    @Test
    fun `Given no args, by type query returns all entities`() {
        val result = Array<Entity>()
        gameWorld.getEntitiesByType(result)

        assertThat(result, hasItems(e1, e10, e11, e2, e3, e4, genericEntity))
    }

    @Test
    fun testGetClosestEntity() {
        assertThat(gameWorld.getClosestEntity(e1) { e -> Entities.getType(e).isType(TestType.T2) }.get(), `is`<Entity>(e2))

        assertThat(gameWorld.getClosestEntity(e1) { e -> Entities.getType(e).isType(TestType.T1) }.get(), `is`<Entity>(e10))

        assertThat(gameWorld.getClosestEntity(e2) { e -> Entities.getType(e).isType(TestType.T2) }, `is`(Optional.empty()))
    }

    @Test
    fun testGetEntitiesFiltered() {
        assertThat(gameWorld.getEntitiesFiltered { e -> Entities.getPosition(e) != null && Entities.getPosition(e).x > 150 },
                `is`(Arrays.asList<Entity>(e3, e4)))

        assertThat(gameWorld.getEntitiesFiltered { e -> Entities.getPosition(e) != null && Entities.getPosition(e).y < 105 },
                `is`(Arrays.asList<Entity>(e1, e2, e3, e4)))

        val result = Array<Entity>(8)
        gameWorld.getEntitiesFiltered(result) { e -> Entities.getPosition(e) != null && Entities.getPosition(e).x > 150 }

        assertThat(result.size(), `is`(2))
        assertThat(result, hasItems<Entity>(e3, e4))

        result.clear()
        gameWorld.getEntitiesFiltered(result) { e -> Entities.getPosition(e) != null && Entities.getPosition(e).y < 105 }

        assertThat(result.size(), `is`(4))
        assertThat(result, hasItems<Entity>(e1, e2, e3, e4))
    }

    @Test
    fun testGetEntitiesInRange() {
        assertThat(gameWorld.getEntitiesInRange(Rectangle2D(130.0, 50.0, 100.0, 100.0)),
                `is`(Arrays.asList<Entity>(e2, e3)))

        val result = Array<Entity>(8)

        gameWorld.getEntitiesInRange(result, 130.0, 50.0, (130 + 100).toDouble(), (50 + 100).toDouble())
        assertThat(result.size(), `is`(2))
        assertThat(result, hasItems<Entity>(e2, e3))
    }

    @Test
    fun testGetCollidingEntities() {
        assertThat(gameWorld.getCollidingEntities(e1), `is`(Arrays.asList<Entity>(e10, e11)))

        val result = Array<Entity>(8)
        gameWorld.getCollidingEntities(result, e1)

        assertThat(result.size(), `is`(2))
        assertThat(result, hasItems<Entity>(e10, e11))
    }

    @Test
    fun testGetEntitiesByLayer() {
        assertThat(gameWorld.getEntitiesByLayer(object : RenderLayer {
            override fun name(): String {
                return "TEST"
            }

            override fun index(): Int {
                return 0
            }
        }), `is`(listOf<Entity>(e1)))

        assertThat(gameWorld.getEntitiesByLayer(RenderLayer.TOP),
                `is`(Arrays.asList<Entity>(e10, e11, e2, e3, e4)))

        val result = Array<Entity>(8)

        gameWorld.getEntitiesByLayer(result, RenderLayer.TOP)
        assertThat(result.size(), `is`(5))
        assertThat(result, hasItems<Entity>(e10, e11, e2, e3, e4))
    }

    @Test
    fun `Get entities at`() {
        assertThat(gameWorld.getEntitiesAt(Point2D(100.0, 100.0)), hasItems(e1))
        assertThat(gameWorld.getEntitiesAt(Point2D(150.0, 100.0)), hasItems(e2))
        assertThat(gameWorld.getEntitiesAt(Point2D(200.0, 100.0)), hasItems(e3))
        assertThat(gameWorld.getEntitiesAt(Point2D(250.0, 100.0)), hasItems(e4))

        assertThat(gameWorld.getEntitiesAt(Point2D(100.5, 100.0)).size, `is`(0))

        val result = Array<Entity>(8)

        val e = Entity()
        e.addComponent(PositionComponent(250.0, 100.0))

        gameWorld.addEntity(e)

        gameWorld.getEntitiesAt(result, Point2D(250.0, 100.0))
        assertThat(result.size(), `is`(2))
        assertThat(result, hasItems(e4, e))
    }

    @Test
    fun testGetEntityByID() {
        assertThat(gameWorld.getEntityByID("e1", 0).get(), `is`<Entity>(e1))
        assertThat(gameWorld.getEntityByID("e1", 1).get(), `is`<Entity>(e10))
        assertThat(gameWorld.getEntityByID("e1", 2).get(), `is`<Entity>(e11))
        assertThat(gameWorld.getEntityByID("e2", 0).get(), `is`<Entity>(e2))

        assertThat(gameWorld.getEntityByID("e3", 0), `is`<Optional<out Any>>(Optional.empty<Any>()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Throw if entity already attached`() {
        val e = Entity()

        gameWorld.addEntity(e)
        gameWorld.addEntity(e)
    }

    @Test
    fun `Add multiple entities`() {
        val e = Entity()
        val ee = Entity()

        gameWorld.addEntities(e, ee)
        assertThat(gameWorld.entities, hasItems(e, ee))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Throw when removing entity not attached to this world`() {
        val e = Entity()

        gameWorld.removeEntity(e)
    }

    @Test
    fun `Remove multiple entities`() {
        assertThat(gameWorld.entities, hasItems(e1, e2))

        gameWorld.removeEntities(e1, e2)
        assertThat(gameWorld.entities, not(hasItems(e1, e2)))
    }

    @Test
    fun `Reset`() {
        assertThat(gameWorld.entities.size, `is`(not(0)))

        gameWorld.reset()

        assertThat(gameWorld.entities.size, `is`(0))
    }

    @Test
    fun `Set level`() {
        val e = Entity()
        val ee = Entity()

        val level = Level(100, 50, arrayListOf(e, ee))

        assertThat(gameWorld.entities, hasItems(e1, e2))
        assertThat(gameWorld.entities, not(hasItems(e, ee)))

        gameWorld.setLevel(level)

        assertThat(gameWorld.entities, not(hasItems(e1, e2)))
        assertThat(gameWorld.entities, hasItems(e, ee))
    }

    @Test(expected = IllegalStateException::class)
    fun `Throw if spawn with no factory`() {
        gameWorld.spawn("bla-bla")
    }

    @Test
    fun `Entity factory spawning`() {
        val factory = TestEntityFactory()

        gameWorld.setEntityFactory(factory)
        assertThat(gameWorld.getEntityFactory(), `is`(factory))

        var e = gameWorld.spawn("enemy", 33.0, 40.0)

        assertTrue(e.hasComponent(PositionComponent::class.java))
        assertThat(e.getComponent(PositionComponent::class.java).value, `is`(Point2D(33.0, 40.0)))

        e = gameWorld.spawn("enemy")

        assertThat(e.getComponent(PositionComponent::class.java).value, `is`(Point2D(0.0, 0.0)))
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

    private class MyEvent internal constructor() : Event(GameWorldTest.MyEvent.ANY) {
        companion object {

            val ANY = EventType<MyEvent>(Event.ANY, "MY_EVENT")
        }
    }

    class TestEntityFactory : EntityFactory {

        @Spawns("enemy")
        fun makeEnemy(data: SpawnData): Entity {
            return Entities.builder()
                    .from(data)
                    .build()
        }
    }
}