/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.ComponentListener
import com.almasb.fxgl.entity.component.Required
import com.almasb.fxgl.entity.component.SerializableComponent
import com.almasb.fxgl.entity.components.*
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.geometry.Point2D
import javafx.geometry.Rectangle2D
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.Serializable

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityTest {

    private lateinit var entity: Entity

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
        assertThat(entity.transformComponent, `is`(entity.getComponent(TransformComponent::class.java)))
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
            entity.removeComponent(TransformComponent::class.java)
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

        assertFalse(control.pausedProperty().value)

        control.pause()

        assertTrue(control.pausedProperty().value)

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
    fun `Get Int`() {
        entity.setProperty("hp", 11)

        assertThat(entity.getInt("hp"), `is`(11))
    }

    @Test
    fun `Get Double`() {
        entity.setProperty("sp", 33.5)

        assertThat(entity.getDouble("sp"), `is`(33.5))
    }

    @Test
    fun `Get Boolean`() {
        entity.setProperty("hasKey", true)

        assertThat(entity.getBoolean("hasKey"), `is`(true))
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
    fun `Set position x y`() {
        entity.setPosition(15.0, 33.0)

        assertThat(entity.position, `is`(Point2D(15.0, 33.0)))
    }

    @Test
    fun `Set position x y using anchor`() {
        entity.setAnchoredPosition(15.0, 10.0, Point2D(0.0, 0.0))

        assertThat(entity.position, `is`(Point2D(15.0, 10.0)))

        entity.setAnchoredPosition(15.0, 10.0, Point2D(15.0, 10.0))

        assertThat(entity.position, `is`(Point2D(0.0, 0.0)))

        // bbox has no effect to setting position using an anchor
        entity.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(20.0, 20.0)))

        entity.setAnchoredPosition(15.0, 10.0, Point2D(15.0, 10.0))

        assertThat(entity.position, `is`(Point2D(0.0, 0.0)))
    }

    @Test
    fun `Set position center`() {
        entity.setLocalAnchorFromCenter()

        entity.setAnchoredPosition(15.0, 34.0)

        assertThat(entity.position, `is`(Point2D(15.0, 34.0)))

        // bbox has an effect on setPositionCenter() computation
        entity.boundingBoxComponent.addHitBox(HitBox(BoundingShape.box(20.0, 20.0)))

        entity.setLocalAnchorFromCenter()

        entity.setAnchoredPosition(15.0, 34.0)

        assertThat(entity.position, `is`(Point2D(5.0, 24.0)))
    }

    @Test
    fun `Get set position using local anchor`() {
        entity.setPosition(-50.0, -30.0)

        assertThat(entity.getAnchoredPosition(Point2D(30.0, 5.0)), `is`(Point2D(-20.0, -25.0)))

        entity.localAnchor = Point2D(30.0, 30.0)

        assertThat(entity.localAnchor, `is`(Point2D(30.0, 30.0)))

        entity.setAnchoredPosition(Point2D(-50.0, 30.0))

        assertThat(entity.anchoredPosition, `is`(Point2D(-50.0, 30.0)))

        assertThat(entity.position, `is`(Point2D(-80.0, 0.0)))
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

        assertThat(entity.components.size, `is`(0))
    }

    @Test
    fun `Controls are removed when entity is cleaned`() {
        entity.addComponent(TestControl())

        entity.clean()

        assertThat(entity.components.size, `is`(0))
    }

    /* CONVENIENCE */

    @Test
    fun `X Y angle type properties`() {
        assertTrue(entity.xProperty() === entity.transformComponent.xProperty())
        assertTrue(entity.yProperty() === entity.transformComponent.yProperty())
        assertTrue(entity.angleProperty() === entity.transformComponent.angleProperty())
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

        entity.rotation = 15.0
        assertThat(entity.rotation, `is`(15.0))
        assertThat(entity.transformComponent.angle, `is`(15.0))
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

    private enum class TT { O }

    @Test
    fun `Type`() {
        entity.type = TT.O

        assertThat(entity.type, `is`<Serializable>(TT.O))
        assertThat(entity.typeComponent.value, `is`<Serializable>(TT.O))
    }

    @Test
    fun `Call a component method directly`() {
        entity.addComponent(ComponentWithMethod())

        assertThat(entity.call("myMethod", "hello world"), `is`(11))
        assertThat(entity.call("myMethod", "hw"), `is`(2))
        assertThat(entity.call("myMethodWithTwoParams", "hw", 3), `is`(5))
    }

    @Test
    fun `Fail when calling a component method and no suitable method found`() {
        var e = assertThrows(IllegalArgumentException::class.java) {
            entity.call<Any>("bla-bla")
        }

        assertThat(e.message, `is`("Cannot find method: bla-bla()"))

        e = assertThrows(IllegalArgumentException::class.java) {
            entity.call<Any>("bla-bla", 3)
        }

        assertThat(e.message, `is`("Cannot find method: bla-bla(int)"))

        e = assertThrows(IllegalArgumentException::class.java) {
            entity.call<Any>("bla-bla", "hi")
        }

        assertThat(e.message, `is`("Cannot find method: bla-bla(class java.lang.String)"))
    }

    @Test
    fun `Fail when calling a component method and call fails`() {
        entity.addComponent(ComponentWithMethod())

        var e = assertThrows(IllegalArgumentException::class.java) {
            entity.call<Any>("methodThatFails")
        }

        assertThat(e.message, `is`("Failed to call: methodThatFails() Cause: java.lang.IllegalStateException: Test Fail"))

        e = assertThrows(IllegalArgumentException::class.java) {
            entity.call<Any>("methodThatFailsInt", 3)
        }

        assertThat(e.message, `is`("Failed to call: methodThatFailsInt(3) Cause: java.lang.IllegalStateException: Test Fail 3"))

        e = assertThrows(IllegalArgumentException::class.java) {
            entity.call<Any>("methodThatFailsStringInt", "hi", 5)
        }

        assertThat(e.message, `is`("Failed to call: methodThatFailsStringInt(hi, 5) Cause: java.lang.IllegalStateException: Test Fail hi, 5"))
    }

//    @Test
//    fun `View`() {
//        val r = Rectangle()
//        val eView = EntityView(r)
//
//        entity.view = eView
//
//        assertThat(entity.view, `is`<com.almasb.fxgl.core.View>(eView))
//        assertThat(entity.viewComponent.view, `is`<com.almasb.fxgl.core.View>(eView))
//    }

    @Test
    fun `Scale`() {
        entity.scaleX = 2.0
        assertThat(entity.scaleX, `is`(2.0))
        assertThat(entity.transformComponent.scaleX, `is`(2.0))

        entity.scaleY = 1.5
        assertThat(entity.scaleY, `is`(1.5))
        assertThat(entity.transformComponent.scaleY, `is`(1.5))
    }

    @Test
    fun `Z index`() {
        entity.z = 100
        assertThat(entity.z, `is`(100))
        assertThat(entity.transformComponent.z, `is`(100))
    }

    /* MOCK CLASSES */

    private class ComponentWithMethod : Component() {

        fun myMethod(s: String) = s.length

        fun myMethodWithTwoParams(s: String, i: Int) = s.length + i

        fun methodThatFails() {
            throw IllegalStateException("Test Fail")
        }

        fun methodThatFailsInt(i: Int) {
            throw IllegalStateException("Test Fail $i")
        }

        fun methodThatFailsStringInt(s: String, i: Int) {
            throw IllegalStateException("Test Fail $s, $i")
        }
    }

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
    private inner class HPControl(var value: Int = 0) : Component() {
    }

    private inner class TestComponent : Component()

    private inner class HPComponent(value: Double) : DoubleComponent(value)

    @Required(TestComponent::class)
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