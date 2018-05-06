/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.component.*
import com.almasb.fxgl.entity.components.*
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import com.almasb.fxgl.texture.Texture
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import javafx.scene.Node
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityTest {

    private lateinit var entity: Entity

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    @BeforeEach
    fun setUp() {
        entity = Entity()
    }

    @Test
    fun `Add component`() {
        val comp = TestComponent()
        entity.addComponent(comp)

        assertThat(entity.components, hasItem(comp))
    }

    @Test
    fun `Add component fails if same type already exists`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            entity.addComponent(TestComponent())
            entity.addComponent(TestComponent())
        }
    }

    @Test
    fun `Add component fails if component is anonymous`() {
        assertThrows(IllegalArgumentException::class.java, {
            entity.addComponent(object : Component() {})
        })
    }

    @Test
    fun `Add component fails if required component is missing`() {
        assertThrows(IllegalStateException::class.java, {
            entity.addComponent(RequireTestComponent())
        })
    }

    @Test
    fun `Get component`() {
        val comp = TestComponent()
        entity.addComponent(comp)

        assertThat(entity.getComponent(TestComponent::class.java), `is`(comp))
    }

    @Test
    fun `Get component fails if not present`() {
        assertThrows(IllegalArgumentException::class.java, {
            entity.getComponent(TestComponent::class.java)
        })
    }

    @Test
    fun `Get component optional`() {
        val comp = TestComponent()
        entity.addComponent(comp)

        assertThat(entity.getComponentOptional(TestComponent::class.java).get(), `is`(comp))
    }

    @Test
    fun `Get core components`() {
        assertThat(entity.positionComponent, `is`(entity.getComponent(PositionComponent::class.java)))
        assertThat(entity.rotationComponent, `is`(entity.getComponent(RotationComponent::class.java)))
        assertThat(entity.boundingBoxComponent, `is`(entity.getComponent(BoundingBoxComponent::class.java)))
        assertThat(entity.viewComponent, `is`(entity.getComponent(ViewComponent::class.java)))
        assertThat(entity.typeComponent, `is`(entity.getComponent(TypeComponent::class.java)))
    }

    @Test
    fun `Has component`() {
        entity.addComponent(TestComponent())

        assertTrue(entity.hasComponent(TestComponent::class.java))
    }

    @Test
    fun `Remove component returns true if removed`() {
        val comp = TestComponent()
        entity.addComponent(comp)

        val isRemoved = entity.removeComponent(TestComponent::class.java)

        assertTrue(isRemoved)
        assertThat(entity.components, not(hasItem(comp)))
    }

    @Test
    fun `Remove component returns false if not found`() {
        val isRemoved = entity.removeComponent(TestComponent::class.java)

        assertFalse(isRemoved)
    }

    @Test
    fun `Remove component fails if component is required by another component`() {
        entity.addComponent(TestComponent())
        entity.addComponent(RequireTestComponent())

        assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(TestComponent::class.java)
        })
    }

    @Test
    fun `Remove component fails if component is required by a control`() {
        entity.addComponent(HPComponent(0.0))
        entity.addComponent(HPControl())

        val e = assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(HPComponent::class.java)
        })

        assertThat(e.message, `is`("Required component: [HPComponent] by: HPControl"))
    }

    @Test
    fun `Throws when removing core component`() {
        assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(TypeComponent::class.java)
        })

        assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(PositionComponent::class.java)
        })

        assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(RotationComponent::class.java)
        })

        assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(BoundingBoxComponent::class.java)
        })

        assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(ViewComponent::class.java)
        })
    }

    @Test
    fun `Add control`() {
        val control = TestControl()
        entity.addComponent(control)

        assertThat(entity.components, hasItem(control))
    }

    @Test
    fun `Add control fails if same type exists`() {
        val control = TestControl()
        entity.addComponent(control)

        assertThrows(IllegalArgumentException::class.java, {
            entity.addComponent(control)
        })
    }

    @Test
    fun `Add control fails if control is anonymous`() {
        assertThrows(IllegalArgumentException::class.java, {
            entity.addComponent(object : Component() {
                override fun onUpdate(tpf: Double) { }
            })
        })
    }

    @Test
    fun `Add control fails if required module is missing`() {
        assertThrows(IllegalStateException::class.java, {
            entity.addComponent(HPControl())
        })
    }

    @Test
    fun `Add control with required module`() {
        entity.addComponent(HPComponent(0.0))
        entity.addComponent(HPControl())

        assertTrue(entity.hasComponent(HPControl::class.java))
    }

    @Test
    fun `Add control fails if within update of another control`() {
        val control = ControlAddingControl()
        entity.addComponent(control)

        assertThrows(IllegalStateException::class.java, {
            entity.update(0.0)
        })
    }

    @Test
    fun `Remove control returns true if removed`() {
        val control = TestControl()
        entity.addComponent(control)
        val isRemoved = entity.removeComponent(TestControl::class.java)

        assertTrue(isRemoved)
        assertThat(entity.components, not(hasItem(control)))
    }

    @Test
    fun `Remove control returns false if control not found`() {
        val isRemoved = entity.removeComponent(TestControl::class.java)

        assertFalse(isRemoved)
    }

    @Test
    fun `Remove control fails if within update of another control`() {
        val control = ControlRemovingControl()
        entity.addComponent(control)

        assertThrows(IllegalStateException::class.java, {
            entity.update(0.0)
        })
    }

    @Test
    fun `Has control`() {
        val control = TestControl()
        entity.addComponent(control)

        assertTrue(entity.hasComponent(TestControl::class.java))
    }

    @Test
    fun `Get control`() {
        val control = TestControl()
        entity.addComponent(control)

        assertThat(entity.getComponent(TestControl::class.java), `is`(control))
    }

    @Test
    fun `Get control fails if not present`() {
        assertThrows(IllegalArgumentException::class.java, {
            entity.getComponent(TestControl::class.java)
        })
    }

    @Test
    fun `Get control optional`() {
        val control = TestControl()
        entity.addComponent(control)

        assertThat(entity.getComponentOptional(TestControl::class.java).get(), `is`(control))
    }

    @Test
    fun `Update controls`() {
        val control = ValueUpdateControl()
        entity.addComponent(control)

        entity.update(0.017)

        assertThat(control.value, `is`(0.017))
    }

    @Test
    fun `Disable controls`() {
        val control = ValueUpdateControl()
        entity.addComponent(control)

        entity.setUpdateEnabled(false)

        entity.update(0.017)

        assertThat(control.value, `is`(0.0))
    }

    @Test
    fun `Pause control`() {
        val control = ValueUpdateControl()
        entity.addComponent(control)

        control.pause()

        entity.update(0.017)

        assertThat(control.value, `is`(0.0))
    }

    @Test
    fun `Resume control`() {
        val control = ValueUpdateControl()
        entity.addComponent(control)

        control.pause()
        control.resume()

        entity.update(0.017)

        assertThat(control.value, `is`(0.017))
    }

    @Test
    fun `Get property returns correct value`() {
        entity.setProperty("hp", 30)

        assertThat(entity.properties.getInt("hp"), `is`(30))
    }

    @Test
    fun `Get property optional returns correct value`() {
        entity.setProperty("hp", 30)

        assertThat(entity.getPropertyOptional<Int>("hp").get(), `is`(30))
    }

    @Test
    fun `Get property optional is empty if no key found`() {
        assertFalse(entity.getPropertyOptional<Any>("no_key").isPresent)
    }

    @Test
    fun `Get String`() {
        entity.setProperty("weaponName", "Mystic Sword")

        assertThat(entity.getString("weaponName"), `is`("Mystic Sword"))
    }

    @Test
    fun `Get Object`() {
        entity.setProperty("vec2", Vec2())

        assertThat(entity.getObject<Vec2>("vec2"), `is`(Vec2()))
    }

    @Test
    fun `Set position`() {
        entity.setPosition(Vec2(10.0, 10.0))

        assertThat(entity.position, `is`(Point2D(10.0, 10.0)))
    }

    @Test
    fun `Get property optional returns empty if key not found`() {
        val value = entity.getPropertyOptional<Any>("no_key")

        assertFalse(value.isPresent)
    }

    @Test
    fun `Module listener is notified on component add`() {
        val hp = HPComponent(20.0)

        val listener = object : ComponentListener {

            override fun onRemoved(component: Component) {
            }

            override fun onAdded(component: Component) {
                (component as HPComponent).value = 10.0
            }
        }

        entity.addComponentListener(listener)

        entity.addComponent(hp)

        assertThat(hp.value, `is`(10.0))
    }

    @Test
    fun `Module listener is notified on component remove`() {
        val hp = HPComponent(20.0)

        val listener = object : ComponentListener {

            override fun onAdded(component: Component) {
            }

            override fun onRemoved(component: Component) {
                (component as HPComponent).value = 0.0
            }
        }

        entity.addComponentListener(listener)

        entity.addComponent(hp)

        entity.removeComponent(HPComponent::class.java)

        assertThat(hp.value, `is`(0.0))
    }

    @Test
    fun `Module listener is notified on control add`() {
        val control = HPControl(0)

        val listener = object : ComponentListener {

            override fun onRemoved(component: Component) {
            }

            override fun onAdded(component: Component) {
                (component as HPControl).value = 10
            }
        }

        entity.addComponent(HPComponent(33.0))
        entity.addComponentListener(listener)


        entity.addComponent(control)

        assertThat(control.value, `is`(10))
    }

    @Test
    fun `Module listener is notified on control remove`() {
        val control = HPControl()

        val listener = object : ComponentListener {

            override fun onAdded(component: Component) {
            }

            override fun onRemoved(comp: Component) {
                (comp as HPControl).value = 20
            }
        }

        entity.addComponentListener(listener)
        entity.addComponent(HPComponent(33.0))

        entity.addComponent(control)

        entity.removeComponent(HPControl::class.java)

        assertThat(control.value, `is`(20))
    }

    @Test
    fun `Module listener is removed correctly`() {
        val hp = HPComponent(20.0)

        val listener = object : ComponentListener {

            override fun onRemoved(component: Component) {
            }

            override fun onAdded(component: Component) {
                (component as HPComponent).value = 0.0
            }
        }

        entity.addComponentListener(listener)
        entity.removeComponentListener(listener)

        entity.addComponent(hp)

        assertThat(hp.value, `is`(20.0))
    }

    @Test
    fun `Save and load`() {
        entity.addComponent(GravityComponent(true))
        entity.addComponent(CustomDataComponent("SerializationData"))
        entity.addComponent(CustomDataControl("SerializableControl"))

        val bundle = Bundle("Entity01234")
        entity.save(bundle)

        val entity2 = Entity()
        entity2.addComponent(GravityComponent(false))
        entity2.addComponent(CustomDataComponent(""))
        entity2.addComponent(CustomDataControl(""))

        entity2.load(bundle)

        assertThat(entity2.getComponent(CustomDataControl::class.java).data, `is`("SerializableControl"))
        assertThat(entity2.getComponent(CustomDataComponent::class.java).data, `is`("SerializationData"))
        assertThat(entity2.getComponent(GravityComponent::class.java).value, `is`(true))
    }

    @Test
    fun `Load from entity with different components`() {
        val bundle = Bundle("Entity01234")
        entity.save(bundle)

        val entity2 = Entity()
        entity2.addComponent(CustomDataComponent(""))

        // if bundle has no such component coz entity1 did not have it, we ignore it
        entity2.load(bundle)

        assertThat(entity2.components.size(), `is`(5 + 1))
    }

    @Test
    fun `Copying an entity also copies components and controls`() {
        entity.addComponent(HPComponent(33.0))
        entity.addComponent(HPControl(10))

        val e2 = entity.copy()

        assertTrue(e2.hasComponent(HPComponent::class.java))
        assertTrue(e2.hasComponent(HPControl::class.java))

        assertThat(e2.getComponent(HPComponent::class.java).value, `is`(33.0))
        assertThat(e2.getComponent(HPControl::class.java).value, `is`(10))
    }

    @Test
    fun testToString() {
        val component = HPComponent(33.0)
        val control = HPControl()

        entity.addComponent(component)
        entity.addComponent(control)

        val toString = entity.toString()

        assertThat(toString, containsString(component.toString()))
        assertThat(toString, containsString(control.toString()))
    }

    /* INTERACTIONS WITH GAME WORLD */

    @Test
    fun `Entity world is null when created`() {
        assertThat(entity.world, nullValue())
    }

    @Test
    fun `Entity world is set when entity added to game world`() {
        val world = GameWorld()
        world.addEntity(entity)

        assertThat(entity.world, `is`(world))
    }

    @Test
    fun `Entity is not active when created`() {
        assertFalse(entity.isActive)
    }

    @Test
    fun `Entity is active in the same frame when added to game world`() {
        val world = GameWorld()
        world.addEntity(entity)

        assertTrue(entity.isActive)
    }

    @Test
    fun `Entity is not active in the same frame when removed from game world`() {
        val world = GameWorld()
        world.addEntity(entity)
        world.removeEntity(entity)

        assertFalse(entity.isActive)
    }

    @Test
    fun `Set on active callback fires in the same frame when added to world`() {
        var value = 0

        entity.setOnActive { value = 5 }

        val world = GameWorld()
        world.addEntity(entity)

        assertThat(value, `is`(5))
    }

    @Test
    fun `Set on not active callback fires in the same frame when removed from world`() {
        var value = 0

        entity.setOnNotActive { value = 5 }

        val world = GameWorld()
        world.addEntity(entity)
        world.removeEntity(entity)

        assertThat(value, `is`(5))
    }

    @Test
    fun `Set on active callback does not fire if entity already in world`() {
        val world = GameWorld()
        world.addEntity(entity)

        var value = 0

        entity.setOnActive { value = 5 }

        assertThat(value, `is`(0))
    }

    @Test
    fun `Set on not active callback does not fire if entity already not in world`() {
        var value = 0

        entity.setOnNotActive { value = 5 }

        assertThat(value, `is`(0))
    }

    @Test
    fun `Active listener fires in the same frame when added to world`() {
        var value = 0

        entity.activeProperty().addListener { _, _, newValue ->
            if (newValue)
                value = 5
        }

        val world = GameWorld()
        world.addEntity(entity)

        assertThat(value, `is`(5))
    }

    @Test
    fun `Active listener fires in the same frame when removed from world`() {
        var value = 0

        entity.activeProperty().addListener { _, _, newValue ->
            if (!newValue)
                value = 5
        }

        val world = GameWorld()
        world.addEntity(entity)
        world.removeEntity(entity)

        assertThat(value, `is`(5))
    }

    @Test
    fun `Remove from world`() {
        val world = GameWorld()

        world.addEntity(entity)
        entity.removeFromWorld()

        assertThat(world.entities, not(hasItem(entity)))
    }

    @Test
    fun `Remove from world does not fail when entity is not attached to any world`() {
        entity.removeFromWorld()
    }

    @Test
    fun `Components are removed when entity is cleaned`() {
        entity.addComponent(TestComponent())

        entity.clean()

        assertThat(entity.components.size(), `is`(0))
    }

    @Test
    fun `Controls are removed when entity is cleaned`() {
        entity.addComponent(TestControl())

        entity.clean()

        assertThat(entity.components.size(), `is`(0))
    }

    /* CONVENIENCE */

    @Test
    fun `X Y angle type properties`() {
        assertTrue(entity.xProperty() === entity.positionComponent.xProperty())
        assertTrue(entity.yProperty() === entity.positionComponent.yProperty())
        assertTrue(entity.angleProperty() === entity.rotationComponent.valueProperty())
        assertTrue(entity.widthProperty() === entity.boundingBoxComponent.widthProperty())
        assertTrue(entity.heightProperty() === entity.boundingBoxComponent.heightProperty())
        assertTrue(entity.typeProperty() === entity.typeComponent.valueProperty())
    }

    @Test
    fun `Translate`() {
        entity.translateTowards(Point2D(100.0, 0.0), 100.0)
        assertThat(entity.x, `is`(100.0))

        entity.translate(50.0, 50.0)
        assertThat(entity.position, `is`(Point2D(150.0, 50.0)))

        entity.translate(Point2D(-50.0, 50.0))
        assertThat(entity.position, `is`(Point2D(100.0, 100.0)))

        entity.translate(Vec2(40.0, -20.0))
        assertThat(entity.position, `is`(Point2D(140.0, 80.0)))

        entity.translateX(60.0)
        assertThat(entity.position, `is`(Point2D(200.0, 80.0)))

        entity.translateY(120.0)
        assertThat(entity.position, `is`(Point2D(200.0, 200.0)))
    }

    @Test
    fun `Rotate`() {
        entity.rotateBy(40.0)
        assertThat(entity.rotation, `is`(40.0))

        entity.rotateToVector(Point2D(-1.0, 0.0))
        assertThat(entity.rotation, `is`(180.0))
    }

    @Test
    fun `BBox`() {
        entity.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(30.0, 40.0)))

        assertThat(entity.width, `is`(30.0))
        assertThat(entity.height, `is`(40.0))
        assertThat(entity.rightX, `is`(30.0))
        assertThat(entity.bottomY, `is`(40.0))
        assertThat(entity.center, `is`(Point2D(15.0, 20.0)))
        assertTrue(entity.isWithin(Rectangle2D(20.0, 20.0, 10.0, 10.0)))
    }

    @Test
    fun `View`() {
        val r = Rectangle(40.0, 15.0)

        entity.setView(r)

        assertThat(entity.view.nodes, containsInAnyOrder<Node>(r))

        entity.setViewFromTexture("brick.png")

        assertThat(entity.view.nodes.map { it.javaClass }, containsInAnyOrder<Class<*>>(Texture::class.java))

        entity.setViewFromTextureWithBBox("brick.png")

        assertThat(entity.width, `is`(64.0))
        assertThat(entity.height, `is`(64.0))

        entity.setViewWithBBox(r)

        assertThat(entity.width, `is`(40.0))
        assertThat(entity.height, `is`(15.0))
    }

    /* SCRIPTS */

    // TODO:
//    @Test
//    fun `Scripts`() {
//        assertFalse(entity.getScriptHandler("onHit").isPresent)
//
//        entity.setProperty("onHit", "entity_script.js")
//
//        val script = entity.getScriptHandler("onHit").get()
//
//        assertThat(script.call<String>("onHit"), `is`("EntityTest"))
//    }

    /* MOCK CLASSES */

    private inner class TestControl : Component() {
        override fun onUpdate(tpf: Double) {}
    }

    private inner class ControlAddingControl : Component() {
        override fun onUpdate(tpf: Double) {
            entity.addComponent(TestControl())
        }
    }

    private inner class ControlRemovingControl : Component() {
        override fun onAdded() {
            entity.addComponent(TestControl())
        }

        override fun onUpdate(tpf: Double) {
            entity.removeComponent(TestControl::class.java)
        }
    }

    private inner class EntityRemovingControl : Component() {
        override fun onUpdate(tpf: Double) {
            entity.removeFromWorld()
        }
    }

    private inner class ValueUpdateControl : Component() {
        var value = 0.0

        override fun onUpdate(tpf: Double) {
            value += tpf
        }
    }

    @Required(HPComponent::class)
    private inner class HPControl(var value: Int = 0) : Component(), CopyableComponent<HPControl> {

        override fun copy(): HPControl {
            return HPControl(value)
        }
    }

    private inner class TestComponent : Component()

    private inner class HPComponent(value: Double) : DoubleComponent(value), CopyableComponent<HPComponent> {

        override fun copy(): HPComponent {
            return HPComponent(value)
        }
    }

    @Required(TestComponent::class)
    //@Required(HPComponent::class)
    private inner class RequireTestComponent : Component()

    private inner class GravityComponent internal constructor(value: Boolean) : BooleanComponent(value)

    class CustomDataComponent constructor(var data: String) : Component(), SerializableComponent {

        override fun write(bundle: Bundle) {
            bundle.put("data", data)
        }

        override fun read(bundle: Bundle) {
            data = bundle.get("data")
        }
    }

    class CustomDataControl constructor(var data: String) : Component(), SerializableComponent {

        override fun write(bundle: Bundle) {
            bundle.put("data", data)
        }

        override fun read(bundle: Bundle) {
            data = bundle.get("data")
        }
    }
}