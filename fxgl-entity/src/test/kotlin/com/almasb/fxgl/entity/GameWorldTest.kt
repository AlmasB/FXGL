/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import java.util.Optional
import com.almasb.fxgl.core.util.Predicate
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.components.IDComponent
import com.almasb.fxgl.entity.components.IrremovableComponent
import com.almasb.fxgl.entity.components.TimeComponent
import com.almasb.fxgl.entity.components.TypeComponent
import com.almasb.fxgl.entity.level.Level
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.function.Executable

class GameWorldTest {

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
    fun `Removed entity keeps components and components in that frame`() {
        val e = Entity()
        e.addComponent(EntityTest.CustomDataComponent("hello"))

        gameWorld.addEntity(e)
        gameWorld.removeEntity(e)

        assertTrue(e.components.isNotEmpty())
    }

    @Test
    fun `Removed entity is cleaned in next frame`() {
        val e = Entity()
        e.addComponent(EntityTest.CustomDataComponent("hello"))

        gameWorld.addEntity(e)
        gameWorld.removeEntity(e)

        gameWorld.onUpdate(0.0)

        assertTrue(e.components.isEmpty())
    }

    @Test
    fun `Remove multiple entities by passing entities as varag)`() {
        val e = Entity()
        val e2 = Entity()

        gameWorld.addEntities(e, e2)
        gameWorld.removeEntities(e, e2)

        assertThat(gameWorld.entities, not(containsInAnyOrder(e, e2)))
    }

    @Test
    fun `Remove multiple entities by passing entities as collection`() {
        val e = Entity()
        val e2 = Entity()

        gameWorld.addEntities(e, e2)
        gameWorld.removeEntities(listOf(e, e2))

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
        e.x = 100.0
        e.y = 100.0

        var count = 0

        gameWorld.addWorldListener(object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {
                count++
                assertThat(entity, `is`(e))
                assertThat(entity.position, `is`(Point2D(100.0, 100.0)))
            }

            override fun onEntityRemoved(entity: Entity) {
                count++
                assertThat(entity, `is`(e))
                assertThat(entity.position, `is`(Point2D(100.0, 100.0)))
            }
        })

        gameWorld.addEntity(e)
        assertThat(count, `is`(1))

        gameWorld.removeEntity(e)
        assertThat(count, `is`(2))
    }

    @Test
    fun `Remove world listener`() {
        val e = Entity()
        e.position = Point2D(100.0, 100.0)

        var count = 0

        val listener = object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {
                count++
            }

            override fun onEntityRemoved(entity: Entity) {
                count++
            }
        }

        gameWorld.addWorldListener(listener)
        gameWorld.removeWorldListener(listener)

        gameWorld.addEntity(e)
        assertThat(count, `is`(0))

        gameWorld.removeEntity(e)
        assertThat(count, `is`(0))
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
//
//    @Test
//    fun `Selected entity`() {
//        val e1 = Entity()
//        val e2 = Entity()
//
//        val c = SelectableComponent(true)
//
//        e1.addComponent(c)
//        e2.addComponent(c.copy())
//
//        gameWorld.addEntities(e1, e2)
//
//        val event = MouseEvent(MouseEvent.MOUSE_PRESSED, 0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 0,
//                false,
//                false,
//                false,
//                false, false, false, false, false, false, false, null)
//
//        e1.view.fireEvent(event)
//
//        assertThat(gameWorld.getSelectedEntity().get(), `is`(e1))
//
//        e2.view.fireEvent(event)
//
//        assertThat(gameWorld.getSelectedEntity().get(), `is`(e2))
//
//        // now disable selectable
//
//        e1.getComponent(SelectableComponent::class.java).value = false
//
//        e1.view.fireEvent(event)
//
//        assertThat(gameWorld.getSelectedEntity().get(), `is`(e2))
//
//        // now enable
//
//        e1.getComponent(SelectableComponent::class.java).value = true
//
//        e1.view.fireEvent(event)
//
//        assertThat(gameWorld.getSelectedEntity().get(), `is`(e1))
//
//        e1.removeComponent(SelectableComponent::class.java)
//
//        assertFalse(gameWorld.getSelectedEntity().isPresent)
//
//        val c3 = SelectableComponent(false)
//
//        e1.addComponent(c3)
//
//        e1.view.fireEvent(event)
//
//        assertFalse(gameWorld.getSelectedEntity().isPresent)
//    }

//    @Test
//    fun `Selected entity property`() {
//        val e1 = Entity()
//        e1.addComponent(SelectableComponent(true))
//
//        val e2 = Entity()
//        e2.addComponent(SelectableComponent(true))
//
//        gameWorld.addEntities(e1, e2)
//
//        val event = MouseEvent(MouseEvent.MOUSE_PRESSED, 0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 0,
//                false,
//                false,
//                false,
//                false, false, false, false, false, false, false, null)
//
//        e1.view.fireEvent(event)
//
//        var count = 0
//
//        gameWorld.selectedEntityProperty().addListener { _, oldValue, newValue ->
//            assertAll(
//                    Executable { assertThat(oldValue, `is`<Entity>(e1)) },
//                    Executable { assertThat(newValue, `is`<Entity>(e2)) }
//            )
//
//            count++
//        }
//
//        e2.view.fireEvent(event)
//
//        assertThat(count, `is`(1))
//    }

    /* LEVELS */

    @Test
    fun `Set level removes previous entities`() {
        val e1 = Entity()

        gameWorld.addEntity(e1)

        val e2 = Entity()
        val e3 = Entity()

        val level = Level(100, 50, listOf(e2, e3))

        gameWorld.setLevel(level)

        assertThat(gameWorld.entities, containsInAnyOrder(e2, e3))
    }

    @Test
    fun `Set level does not remove Irremovable entities`() {
        val e1 = Entity()
        e1.addComponent(IrremovableComponent())

        gameWorld.addEntity(e1)

        // move e1 to update list
        gameWorld.onUpdate(0.0)

        val e4 = Entity()
        e4.addComponent(IrremovableComponent())

        gameWorld.addEntity(e4)

        val e2 = Entity()
        val e3 = Entity()

        val level = Level(100, 50, listOf(e2, e3))

        gameWorld.setLevel(level)

        assertThat(gameWorld.entities, containsInAnyOrder(e1, e2, e3, e4))
    }
//
//    @Test
//    fun `Set level from Tiled map json`() {
//        gameWorld.addEntityFactory(TiledMapEntityFactory())
//        gameWorld.setLevelFromMap("test_level1.json")
//
//        assertThat(gameWorld.entities, containsInAnyOrder(
//                // this is "layer" entity
//                EntityMatcher(0, 0),
//
//                // these are "object" entities
//                EntityMatcher(0, 736),
//                EntityMatcher(160, 608),
//                EntityMatcher(1472, 608),
//                EntityMatcher(352, 640)
//        ))
//    }
//
//    @Test
//    fun `Set level from Tiled map tmx`() {
//        gameWorld.addEntityFactory(TiledMapEntityFactory())
//        gameWorld.setLevelFromMap("test_level1.tmx")
//
//        assertThat(gameWorld.entities, containsInAnyOrder(
//                // this is "layer" entity
//                EntityMatcher(0, 0),
//
//                // these are "object" entities
//                EntityMatcher(0, 736),
//                EntityMatcher(160, 608),
//                EntityMatcher(1472, 608),
//                EntityMatcher(352, 640)
//        ))
//    }
//
//    @Test
//    fun `Fail if unknown Tiled map format`() {
//        assertThrows(IllegalArgumentException::class.java, {
//            gameWorld.setLevelFromMap("level1.map")
//        })
//    }

    @Test
    fun `Clear removes all entities`() {
        val e = Entity()
        e.addComponent(IrremovableComponent())

        val e2 = Entity()

        gameWorld.addEntities(e, e2)

        gameWorld.clear()

        assertTrue(gameWorld.entities.isEmpty())
    }

    @Test
    fun `Clear correctly cleans all entities`() {
        val e = Entity()
        e.addComponent(EntityTest.CustomDataComponent("hello"))
        gameWorld.addEntity(e)

        gameWorld.onUpdate(0.0)

        val e2 = Entity()
        e2.addComponent(EntityTest.CustomDataComponent("hello"))
        gameWorld.addEntity(e2)

        var count = 0

        gameWorld.addWorldListener(object : EntityWorldListener {
            override fun onEntityAdded(entity: Entity) {}

            override fun onEntityRemoved(entity: Entity) {
                count++
            }
        })

        gameWorld.clear()

        assertAll(
                Executable { assertThat(count, `is`(2)) },
                Executable { assertTrue(e.components.isEmpty()) },
                Executable { assertTrue(e2.components.isEmpty()) }
        )
    }

    @Test
    fun `Clear correctly cleans entity removed in previous frame`() {
        val e = Entity()
        e.addComponent(EntityTest.CustomDataComponent("hello"))
        gameWorld.addEntity(e)

        gameWorld.onUpdate(0.0)

        gameWorld.removeEntity(e)

        gameWorld.clear()

        assertTrue(e.components.isEmpty())
    }

    @Test
    fun `Do not remove if entity has IrremovableComponent`() {
        val e = Entity()
        e.addComponent(IrremovableComponent())

        gameWorld.addEntity(e)
        gameWorld.removeEntity(e)

        assertThat(gameWorld.entities, contains(e))
    }

    /* SPAWNS */

    @Test
    fun `Throw if spawn called with no factory`() {
        assertThrows(IllegalStateException::class.java, {
            gameWorld.spawn("bla-bla")
        })
    }

    @Test
    fun `Add Remove factory`() {
        val factory = TestEntityFactory()

        gameWorld.addEntityFactory(factory)
        gameWorld.spawn("enemy")

        gameWorld.removeEntityFactory(factory)

        assertThrows(IllegalStateException::class.java, {
            gameWorld.spawn("enemy")
        })
    }

    @Test
    fun `Cannot add factories with duplicate spawns`() {
        val factory = TestEntityFactory()
        val factory2 = TestEntityFactory2()

        gameWorld.addEntityFactory(factory)

        assertThrows(IllegalArgumentException::class.java, {
            gameWorld.addEntityFactory(factory2)
        })
    }

    @Test
    fun `Spawn without initial position`() {
        val factory = TestEntityFactory()
        gameWorld.addEntityFactory(factory)

        val e = gameWorld.spawn("enemy")

        assertThat(e.position, `is`(Point2D(0.0, 0.0)))
    }

    @Test
    fun `Spawn with initial position`() {
        val factory = TestEntityFactory()
        gameWorld.addEntityFactory(factory)

        assertAll(
                Executable {
                    val e1 = gameWorld.spawn("enemy", 33.0, 40.0)
                    assertThat(e1.position, `is`(Point2D(33.0, 40.0)))
                },

                Executable {
                    val e2 = gameWorld.spawn("enemy", Point2D(100.0, 100.0))
                    assertThat(e2.position, `is`(Point2D(100.0, 100.0)))
                }
        )
    }

//    @Test
//    fun `Spawn with initial properties`() {
//        val factory = TestEntityFactory()
//        gameWorld.addEntityFactory(factory)
//
//        assertAll(
//                Executable {
//                    val e1 = gameWorld.spawn("enemy", SpawnData(0.0, 0.0).put("fly", true))
//                    assertTrue(e1.getBoolean("fly"))
//                },
//
//                Executable {
//                    val e1 = gameWorld.spawn("enemy", SpawnData(0.0, 0.0).put("gravity", 0.5))
//                    assertThat(e1.getDouble("gravity"), `is`(0.5))
//                }
//        )
//    }

    @Test
    fun `Throw if factory has no such spawn method`() {
        gameWorld.addEntityFactory(TestEntityFactory())

        assertThrows(IllegalArgumentException::class.java, {
            gameWorld.spawn("bla-bla")
        })
    }

    /* QUERIES */

    @Test
    fun `By Type List`() {
        val e1 = Entity()
        e1.type = TestType.T1

        val e2 = Entity()
        e2.type = TestType.T2

        val e3 = Entity()
        e3.type = TestType.T3

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesByType(TestType.T1), contains(e1)) },
                Executable { assertThat(gameWorld.getEntitiesByType(TestType.T2), contains(e2)) },
                Executable { assertThat(gameWorld.getEntitiesByType(TestType.T2, TestType.T3), containsInAnyOrder(e2, e3)) },
                Executable { assertThat(gameWorld.getEntitiesByType(), contains(e1, e2, e3)) }
        )
    }

    @Test
    fun `Singleton`() {
        val e1 = Entity()
        e1.type = TestType.T1

        val e2 = Entity()
        e2.type = TestType.T2

        val e3 = Entity()
        e3.type = TestType.T3

        val e4 = Entity()

        gameWorld.addEntities(e1, e2, e3, e4)

        assertAll(
                Executable { assertThat(gameWorld.getSingleton(TestType.T1), `is`(e1)) },
                Executable { assertThat(gameWorld.getSingleton(Predicate { it.isType(TestType.T1) }), `is`(e1)) },
                Executable { assertThat(gameWorld.getSingletonOptional(TestType.T1).get(), `is`(e1)) },
                Executable { assertThat(gameWorld.getSingletonOptional(TestType.T2).get(), `is`(e2)) },
                Executable { assertThat(gameWorld.getSingletonOptional (Predicate { it.isType(TestType.T3) }).get(), `is`(e3)) },
                Executable { assertFalse(gameWorld.getSingletonOptional(TestType.T4).isPresent) },
                Executable { assertFalse(gameWorld.getSingletonOptional (Predicate { it.isType(TestType.T4) }).isPresent) },
                Executable { assertThrows<NoSuchElementException> { gameWorld.getSingleton(Predicate { it.x < 0 }) } }
        )
    }

    @Test
    fun `Random returns the single item present`() {
        val e1 = Entity()
        e1.type = TestType.T1

        val e2 = Entity()
        e2.type = TestType.T2

        gameWorld.addEntities(e1, e2)

        assertAll(
                Executable { assertThat(gameWorld.getRandom(TestType.T1).get(), `is`(e1)) },
                Executable { assertThat(gameWorld.getRandom(Predicate { it.getComponent(TypeComponent::class.java).isType(TestType.T2) }).get(), `is`(e2)) }
        )
    }

    @Test
    fun `Closest entity returns Optional empty if no valid entity found`() {
        val e = Entity()
        e.x = 10.0
        e.y = 10.0

        gameWorld.addEntity(e)

        assertThat(gameWorld.getClosestEntity(e, Predicate { true }), `is`(Optional.empty()))
    }

    @Test
    fun `Closest entity`() {
        val e1 = Entity()
        e1.x = 10.0
        e1.y = 10.0

        val e2 = Entity()
        e2.x = 20.0
        e2.y = 10.0

        val e3 = Entity()
        e3.x = 100.0
        e3.y = 10.0

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getClosestEntity(e1, Predicate { true }).get(), `is`(e2)) },
                Executable { assertThat(gameWorld.getClosestEntity(e2, Predicate { true }).get(), `is`(e1)) },
                Executable { assertThat(gameWorld.getClosestEntity(e3, Predicate { true }).get(), `is`(e2)) }
        )
    }

    @Test
    fun `Filtered entities List`() {
        val e1 = Entity()
        e1.x = 10.0
        e1.y = 10.0

        val e2 = Entity()
        e2.x = 20.0
        e2.y = 10.0

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesFiltered (Predicate { it.x > 15 }), contains(e2)) },
                Executable { assertThat(gameWorld.getEntitiesFiltered (Predicate { it.y < 30 }), containsInAnyOrder(e1, e2, e3)) },
                Executable { assertThat(gameWorld.getEntitiesFiltered (Predicate { true }), containsInAnyOrder(e1, e2, e3)) }
        )
    }

    @Test
    fun `Get entities at List`() {
        val e1 = Entity()
        e1.x = 10.0
        e1.y = 10.0

        val e2 = Entity()
        e2.x = 10.0
        e2.y = 10.0

        val e3 = Entity()
        e3.x = 100.0
        e3.y = 10.0

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesAt(Point2D(10.0, 10.0)), containsInAnyOrder(e1, e2)) },
                Executable { assertThat(gameWorld.getEntitiesAt(Point2D(100.0, 10.0)), contains(e3)) }
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
        e1.boundingBoxComponent.addHitBox(HitBox("main", BoundingShape.box(20.0, 20.0)))

        val e2 = Entity()
        e2.x = 100.0
        e2.boundingBoxComponent.addHitBox(HitBox("main", BoundingShape.box(20.0, 20.0)))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getEntitiesInRange(Rectangle2D(0.0, 0.0, 100.0, 100.0)), contains(e1)) },
                Executable { assertThat(gameWorld.getEntitiesInRange(Rectangle2D(90.0, 0.0, 20.0, 20.0)), contains(e2)) }
        )
    }

    @Test
    fun `Get colliding entities`() {
        val e1 = Entity()
        e1.boundingBoxComponent.addHitBox(HitBox("main", BoundingShape.box(20.0, 20.0)))

        val e2 = Entity()
        e2.x = 10.0
        e2.boundingBoxComponent.addHitBox(HitBox("main", BoundingShape.box(20.0, 20.0)))

        val e3 = Entity()

        gameWorld.addEntities(e1, e2, e3)

        assertAll(
                Executable { assertThat(gameWorld.getCollidingEntities(e1), contains(e2)) },
                Executable { assertThat(gameWorld.getCollidingEntities(e2), contains(e1)) },
                Executable { assertTrue(gameWorld.getCollidingEntities(e3).isEmpty()) }
        )
    }

    /* SPECIAL CASES */

    @Test
    fun `Time component is honored`() {
        val e = Entity()
        e.addComponent(TimeComponent(0.5))

        val control = TimeBasedControl()
        e.addComponent(control)

        gameWorld.addEntity(e)
        gameWorld.onUpdate(0.016)

        assertTrue(control.assertPassed)
    }

    private class TimeBasedControl : Component() {
        var assertPassed = false

        override fun onUpdate(tpf: Double) {
            assertThat(tpf, `is`(0.008))
            assertPassed = true
        }
    }

    private class EntityMatcher(val x: Int, val y: Int) : BaseMatcher<Entity>() {

        override fun matches(item: Any): Boolean {
            val position = (item as Entity).transformComponent

            return position.x.toInt() == x && position.y.toInt() == y
        }

        override fun describeTo(description: Description) {
            description.appendText("Entity at $x,$y")
        }
    }
    
    private enum class TestType {
        T1, T2, T3, T4
    }

    class TestEntityFactory : EntityFactory {

        @Spawns("enemy")
        fun makeEnemy(data: SpawnData): Entity {
            val e = Entity()
            e.setPosition(data.x, data.y)
            return e
        }
    }

    class TestEntityFactory2 : EntityFactory {

        @Spawns("enemy")
        fun makeEnemy(data: SpawnData): Entity {
            val e = Entity()
            e.setPosition(data.x, data.y)
            return e
        }
    }

    class TiledMapEntityFactory : EntityFactory {

        @Spawns("player")
        fun makePlayer(data: SpawnData): Entity {
            val e = Entity()
            e.setPosition(data.x, data.y)
            return e
        }

        @Spawns("platform")
        fun makePlatform(data: SpawnData): Entity {
            val e = Entity()
            e.setPosition(data.x, data.y)
            return e
        }
    }
}