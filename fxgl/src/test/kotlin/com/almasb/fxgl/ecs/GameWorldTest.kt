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
import com.almasb.fxgl.ecs.component.IrremovableComponent
import com.almasb.fxgl.entity.*
import com.almasb.fxgl.entity.component.*
import com.almasb.fxgl.gameplay.Level
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.event.Event
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*

import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.function.Executable

class GameWorldTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    private lateinit var gameWorld: GameWorld

    @BeforeEach
    fun `init`() {
        gameWorld = GameWorld()
    }

    @Test
    fun `Add entity`() {
        val e = Entity()

        gameWorld.addEntity(e)

        assertThat(gameWorld.entities, contains(e))
    }

    @Test
    fun `Throw when removing entity not attached to this world`() {
        val e = Entity()

        val newWorld = GameWorld()
        newWorld.addEntity(e)

        assertThrows(IllegalArgumentException::class.java, {
            gameWorld.removeEntity(e)
        })
    }

    @Test
    fun `Add multiple entities`() {
        val e = Entity()
        val e2 = Entity()

        gameWorld.addEntities(e, e2)

        assertThat(gameWorld.entities, containsInAnyOrder(e, e2))
    }

    @Test
    fun `Throw if entity already attached`() {
        val e = Entity()

        gameWorld.addEntity(e)

        assertThrows(IllegalArgumentException::class.java, {
            gameWorld.addEntity(e)
        })
    }

    @Test
    fun `Remove entity`() {
        val e = Entity()

        gameWorld.addEntity(e)
        gameWorld.removeEntity(e)

        assertThat(gameWorld.entities, not(contains(e)))
    }

    @Test
    fun `Remove multiple entities`() {
        val e = Entity()
        val e2 = Entity()

        gameWorld.addEntities(e, e2)
        gameWorld.removeEntities(e, e2)

        assertThat(gameWorld.entities, not(containsInAnyOrder(e, e2)))
    }

    @Test
    fun `Removing entity multiple times does not fail`() {
        val e = Entity()

        gameWorld.addEntity(e)
        gameWorld.removeEntity(e)

        gameWorld.removeEntity(e)
    }

    @Test
    fun `Listeners notified in the same frame`() {
        val e = Entity()
        e.addComponent(PositionComponent(100.0, 100.0))

        var count = 0

        gameWorld.addWorldListener(object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {
                count++
                assertThat(entity, `is`(e))
                assertThat(entity.getComponent(PositionComponent::class.java).value, `is`(Point2D(100.0, 100.0)))
            }

            override fun onEntityRemoved(entity: Entity) {
                count++
                assertThat(entity, `is`(e))
                assertThat(entity.getComponent(PositionComponent::class.java).value, `is`(Point2D(100.0, 100.0)))
            }
        })

        gameWorld.addEntity(e)
        assertThat(count, `is`(1))

        gameWorld.removeEntity(e)
        assertThat(count, `is`(2))
    }

    @Test
    fun `getEntitiesCopy has all entities`() {
        val e = Entity()
        val e2 = Entity()

        gameWorld.addEntities(e, e2)

        assertThat(gameWorld.entitiesCopy, containsInAnyOrder(e, e2))
    }

    @Test
    fun `getEntitiesCopy returns a copy`() {
        assertFalse(gameWorld.entities === gameWorld.entitiesCopy)
    }

    /* SPAWNS */

    @Test
    fun `Throw if spawn called with no factory`() {
        assertThrows(IllegalStateException::class.java, {
            gameWorld.spawn("bla-bla")
        })
    }

    @Test
    fun `Set entity factory`() {
        val factory = TestEntityFactory()

        gameWorld.setEntityFactory(factory)

        assertThat(gameWorld.getEntityFactory(), `is`(factory))
    }

    @Test
    fun `Spawn without initial position`() {
        val factory = TestEntityFactory()
        gameWorld.setEntityFactory(factory)

        val e = gameWorld.spawn("enemy")

        assertThat(e.getComponent(PositionComponent::class.java).value, `is`(Point2D(0.0, 0.0)))
    }

    @Test
    fun `Spawn with initial position`() {
        val factory = TestEntityFactory()
        gameWorld.setEntityFactory(factory)

        val e = gameWorld.spawn("enemy", 33.0, 40.0)

        assertThat(e.getComponent(PositionComponent::class.java).value, `is`(Point2D(33.0, 40.0)))
    }

    @Test
    fun `Throw if factory has no such spawn method`() {
        gameWorld.setEntityFactory(TestEntityFactory())

        assertThrows(IllegalArgumentException::class.java, {
            gameWorld.spawn("bla-bla")
        })
    }

    /* QUERIES */

    @Test
    fun `By Type List`() {
        val e1 = Entity()
        e1.addComponent(TypeComponent(TestType.T1))

        val e2 = Entity()
        e2.addComponent(TypeComponent(TestType.T2))

        val e3 = Entity()
        e3.addComponent(TypeComponent(TestType.T3))

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesByType(TestType.T1), contains(e1)) },
                Executable { assertThat(gameWorld.getEntitiesByType(TestType.T2), contains(e2)) },
                Executable { assertThat(gameWorld.getEntitiesByType(TestType.T2, TestType.T3), containsInAnyOrder(e2, e3)) }
        )
    }

    @Test
    fun `By Type Array`() {
        val e1 = Entity()
        e1.addComponent(TypeComponent(TestType.T1))

        val e2 = Entity()
        e2.addComponent(TypeComponent(TestType.T2))

        val e3 = Entity()
        e3.addComponent(TypeComponent(TestType.T3))

        gameWorld.addEntities(e1, e2, e3)

        val result1 = Array<Entity>()
        val result2 = Array<Entity>()
        val result3 = Array<Entity>()

        gameWorld.getEntitiesByType(result1, TestType.T1)
        gameWorld.getEntitiesByType(result2, TestType.T2)
        gameWorld.getEntitiesByType(result3, TestType.T2, TestType.T3)

        assertAll(
                Executable { assertThat(result1, contains(e1)) },
                Executable { assertThat(result2, contains(e2)) },
                Executable { assertThat(result3, containsInAnyOrder(e2, e3)) }
        )
    }

    @Test
    fun `Given no args, byType returns all entities`() {
        val e = Entity()
        val e2 = Entity()

        gameWorld.addEntities(e, e2)

        val result = Array<Entity>()
        gameWorld.getEntitiesByType(result)

        assertThat(result, containsInAnyOrder(e, e2))
    }

    @Test
    fun `Array based queries append to array`() {
        val e = Entity()
        val e2 = Entity()
        val e3 = Entity()

        gameWorld.addEntities(e, e2, e3)

        val result = Array<Entity>()
        result.add(e3)

        gameWorld.getEntitiesByType(result)

        assertThat(result, containsInAnyOrder(e, e2, e3, e3))
    }

    @Test
    fun `Closest entity returns Optional empty if no valid entity found`() {
        val e = Entity()
        e.addComponent(PositionComponent(10.0, 10.0))

        gameWorld.addEntity(e)

        assertThat(gameWorld.getClosestEntity(e, { true }), `is`(Optional.empty()))
    }

    @Test
    fun `Closest entity`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(10.0, 10.0))

        val e2 = Entity()
        e2.addComponent(PositionComponent(20.0, 10.0))

        val e3 = Entity()
        e3.addComponent(PositionComponent(100.0, 10.0))

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getClosestEntity(e1, { true }).get(), `is`(e2)) },
                Executable { assertThat(gameWorld.getClosestEntity(e2, { true }).get(), `is`(e1)) },
                Executable { assertThat(gameWorld.getClosestEntity(e3, { true }).get(), `is`(e2)) }
        )
    }

    @Test
    fun `Filtered entities List`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(10.0, 10.0))

        val e2 = Entity()
        e2.addComponent(PositionComponent(20.0, 10.0))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesFiltered { Entities.getPosition(it) != null && Entities.getPosition(it).x > 15 }, contains(e2)) },
                Executable { assertThat(gameWorld.getEntitiesFiltered { Entities.getPosition(it) != null && Entities.getPosition(it).y < 30 }, containsInAnyOrder(e1, e2)) },
                Executable { assertThat(gameWorld.getEntitiesFiltered { true }, containsInAnyOrder(e1, e2, e3)) }
        )
    }

    @Test
    fun `Filtered entities Array`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(10.0, 10.0))

        val e2 = Entity()
        e2.addComponent(PositionComponent(20.0, 10.0))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        val result1 = Array<Entity>()
        val result2 = Array<Entity>()
        val result3 = Array<Entity>()

        gameWorld.getEntitiesFiltered(result1) { Entities.getPosition(it) != null && Entities.getPosition(it).x > 15 }
        gameWorld.getEntitiesFiltered(result2) { Entities.getPosition(it) != null && Entities.getPosition(it).y < 30 }
        gameWorld.getEntitiesFiltered(result3) { true }

        assertAll(
                Executable { assertThat(result1, contains(e2)) },
                Executable { assertThat(result2, containsInAnyOrder(e1, e2)) },
                Executable { assertThat(result3, containsInAnyOrder(e1, e2, e3)) }
        )
    }

    @Test
    fun `Get entities at List`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(10.0, 10.0))

        val e2 = Entity()
        e2.addComponent(PositionComponent(10.0, 10.0))

        val e3 = Entity()
        e3.addComponent(PositionComponent(100.0, 10.0))

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesAt(Point2D(10.0, 10.0)), containsInAnyOrder(e1, e2)) },
                Executable { assertThat(gameWorld.getEntitiesAt(Point2D(100.0, 10.0)), contains(e3)) }
        )
    }

    @Test
    fun `Get entities at Array`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(10.0, 10.0))

        val e2 = Entity()
        e2.addComponent(PositionComponent(10.0, 10.0))

        val e3 = Entity()
        e3.addComponent(PositionComponent(100.0, 10.0))

        gameWorld.addEntities(e1, e2, e3)

        val result1 = Array<Entity>()
        val result2 = Array<Entity>()

        gameWorld.getEntitiesAt(result1, Point2D(10.0, 10.0))
        gameWorld.getEntitiesAt(result2, Point2D(100.0, 10.0))

        assertAll(
                Executable { assertThat(result1, containsInAnyOrder(e1, e2)) },
                Executable { assertThat(result2, contains(e3)) }
        )
    }

    @Test
    fun `By ID`() {
        val e1 = Entity()
        e1.addComponent(IDComponent("e", 1))

        val e2 = Entity()
        e2.addComponent(IDComponent("e", 2))

        val e3 = Entity()
        e3.addComponent(IDComponent("e", 3))

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntityByID("e", 1).get(), `is`(e1)) },
                Executable { assertThat(gameWorld.getEntityByID("e", 2).get(), `is`(e2)) },
                Executable { assertThat(gameWorld.getEntityByID("e", 3).get(), `is`(e3)) },
                Executable { assertThat(gameWorld.getEntityByID("e", 4), `is`<Any>(Optional.empty<Entity>())) }
        )
    }

    @Test
    fun `By range List`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(0.0, 0.0))
        e1.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e2 = Entity()
        e2.addComponent(PositionComponent(100.0, 0.0))
        e2.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesInRange(Rectangle2D(0.0, 0.0, 100.0, 100.0)), containsInAnyOrder(e1, e2)) },
                Executable { assertThat(gameWorld.getEntitiesInRange(Rectangle2D(90.0, 0.0, 20.0, 20.0)), contains(e2)) }
        )
    }

    @Test
    fun `By range Array`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(0.0, 0.0))
        e1.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e2 = Entity()
        e2.addComponent(PositionComponent(100.0, 0.0))
        e2.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        val result1 = Array<Entity>()
        val result2 = Array<Entity>()

        gameWorld.getEntitiesInRange(result1, 0.0, 0.0, 100.0, 100.0)
        gameWorld.getEntitiesInRange(result2, 90.0, 0.0, 90 + 20.0, 20.0)

        assertAll(
                Executable { assertThat(result1, containsInAnyOrder(e1, e2)) },
                Executable { assertThat(result2, contains(e2)) }
        )
    }

    @Test
    fun `Get colliding entities`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(0.0, 0.0))
        e1.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e2 = Entity()
        e2.addComponent(PositionComponent(10.0, 0.0))
        e2.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getCollidingEntities(e1), contains(e2)) },
                Executable { assertThat(gameWorld.getCollidingEntities(e2), contains(e1)) },
                Executable { assertTrue(gameWorld.getCollidingEntities(e3).isEmpty()) }
        )
    }

    @Test
    fun `Get colliding entities Array`() {
        val e1 = Entity()
        e1.addComponent(PositionComponent(0.0, 0.0))
        e1.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e2 = Entity()
        e2.addComponent(PositionComponent(10.0, 0.0))
        e2.addComponent(BoundingBoxComponent(HitBox("main", BoundingShape.box(20.0, 20.0))))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        val result1 = Array<Entity>()
        val result2 = Array<Entity>()
        val result3 = Array<Entity>()

        gameWorld.getCollidingEntities(result1, e1)
        gameWorld.getCollidingEntities(result2, e2)
        gameWorld.getCollidingEntities(result3, e3)

        assertAll(
                Executable { assertThat(result1, contains(e2)) },
                Executable { assertThat(result2, contains(e1)) },
                Executable { assertTrue(result3.isEmpty) }
        )
    }

    @Test
    fun `By layer List`() {
        val layer = object : RenderLayer {
            override fun name(): String {
                return "TEST"
            }

            override fun index(): Int {
                return 0
            }
        }

        val e1 = Entity()
        e1.addComponent(PositionComponent(0.0, 0.0))
        e1.addComponent(RotationComponent())
        e1.addComponent(ViewComponent(layer))

        val e2 = Entity()
        e2.addComponent(PositionComponent(10.0, 0.0))
        e2.addComponent(RotationComponent())
        e2.addComponent(ViewComponent())

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesByLayer(layer), contains(e1)) },
                Executable { assertThat(gameWorld.getEntitiesByLayer(RenderLayer.TOP), contains(e2)) }
        )
    }

    @Test
    fun `By layer Array`() {
        val layer = object : RenderLayer {
            override fun name(): String {
                return "TEST"
            }

            override fun index(): Int {
                return 0
            }
        }

        val e1 = Entity()
        e1.addComponent(PositionComponent(0.0, 0.0))
        e1.addComponent(RotationComponent())
        e1.addComponent(ViewComponent(layer))

        val e2 = Entity()
        e2.addComponent(PositionComponent(10.0, 0.0))
        e2.addComponent(RotationComponent())
        e2.addComponent(ViewComponent())

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        val result1 = Array<Entity>()
        val result2 = Array<Entity>()

        gameWorld.getEntitiesByLayer(result1, layer)
        gameWorld.getEntitiesByLayer(result2, RenderLayer.TOP)

        assertAll(
                Executable { assertThat(result1, contains(e1)) },
                Executable { assertThat(result2, contains(e2)) }
        )
    }



//
//    @Test
//    fun `Do not remove if entity has IrremovableComponent`() {
//        val e = Entity()
//        e.addComponent(IrremovableComponent())
//
//        gameWorld.addEntity(e)
//        gameWorld.removeEntity(e)
//
//        assertThat(gameWorld.entities, hasItems(e))
//    }
//

//
//    @Test
//    fun `Reset`() {
//        assertThat(gameWorld.entities.size, `is`(not(0)))
//
//        gameWorld.clear()
//
//        assertThat(gameWorld.entities.size, `is`(0))
//    }
//
//    @Test
//    fun `Reset does not remove if entity has IrremovableComponent`() {
//        val e = Entity()
//        e.addComponent(IrremovableComponent())
//
//        gameWorld.addEntity(e)
//        gameWorld.clear()
//
//        assertThat(gameWorld.entities, hasItems(e))
//    }
//
//    @Test
//    fun `Set level`() {
//        val e = Entity()
//        val ee = Entity()
//
//        val level = Level(100, 50, arrayListOf(e, ee))
//
//        assertThat(gameWorld.entities, hasItems(e1, e2))
//        assertThat(gameWorld.entities, not(hasItems(e, ee)))
//
//        gameWorld.setLevel(level)
//
//        assertThat(gameWorld.entities, not(hasItems(e1, e2)))
//        assertThat(gameWorld.entities, hasItems(e, ee))
//    }
//
    private enum class TestType {
        T1, T2, T3
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