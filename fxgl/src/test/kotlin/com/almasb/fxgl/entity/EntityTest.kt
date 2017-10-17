/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.entity.component.BooleanComponent
import com.almasb.fxgl.entity.component.DoubleComponent
import com.almasb.fxgl.entity.component.Required
import com.almasb.fxgl.entity.serialization.SerializableComponent
import com.almasb.fxgl.entity.serialization.SerializableControl
import com.almasb.fxgl.io.serialization.Bundle
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
    fun `Get component optional`() {
        val comp = TestComponent()
        entity.addComponent(comp)

        assertThat(entity.getComponentOptional(TestComponent::class.java).get(), `is`(comp))
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
        entity.addControl(HPControl())

        assertThrows(IllegalArgumentException::class.java, {
            entity.removeComponent(HPComponent::class.java)
        })
    }

//    @Test
//    fun `Remove all components`() {
//        val comp1 = TestComponent()
//        entity.addComponent(comp1)
//
//        val comp2 = RequireTestComponent()
//        entity.addComponent(comp2)
//
//        entity.removeAllComponents()
//
//        assertThat(entity.components, not(hasItems(comp1, comp2)))
//    }

    @Test
    fun `Add control`() {
        val control = TestControl()
        entity.addControl(control)

        assertThat(entity.controls, hasItem(control))
    }

    @Test
    fun `Add control fails if same type exists`() {
        val control = TestControl()
        entity.addControl(control)

        assertThrows(IllegalArgumentException::class.java, {
            entity.addControl(control)
        })
    }

    @Test
    fun `Add control fails if control is anonymous`() {
        assertThrows(IllegalArgumentException::class.java, {
            entity.addControl(object : Control() {
                override fun onUpdate(entity: Entity, tpf: Double) { }
            })
        })
    }

    @Test
    fun `Add control fails if required module is missing`() {
        assertThrows(IllegalStateException::class.java, {
            entity.addControl(HPControl())
        })
    }

    @Test
    fun `Add control with required module`() {
        entity.addComponent(HPComponent(0.0))
        entity.addControl(HPControl())

        assertTrue(entity.hasControl(HPControl::class.java))
    }

    @Test
    fun `Add control fails if within update of another control`() {
        val control = ControlAddingControl()
        entity.addControl(control)

        assertThrows(IllegalStateException::class.java, {
            entity.update(0.0)
        })
    }

    @Test
    fun `Remove control returns true if removed`() {
        val control = TestControl()
        entity.addControl(control)
        val isRemoved = entity.removeControl(TestControl::class.java)

        assertTrue(isRemoved)
        assertThat(entity.controls, not(hasItem(control)))
    }

    @Test
    fun `Remove control returns false if control not found`() {
        val isRemoved = entity.removeControl(TestControl::class.java)

        assertFalse(isRemoved)
    }

    @Test
    fun `Remove control fails if within update of another control`() {
        val control = ControlRemovingControl()
        entity.addControl(control)

        assertThrows(IllegalStateException::class.java, {
            entity.update(0.0)
        })
    }

    @Test
    fun `Has control`() {
        val control = TestControl()
        entity.addControl(control)

        assertTrue(entity.hasControl(TestControl::class.java))
    }

    @Test
    fun `Get control`() {
        val control = TestControl()
        entity.addControl(control)

        assertThat(entity.getControl(TestControl::class.java), `is`(control))
    }

    @Test
    fun `Get control optional`() {
        val control = TestControl()
        entity.addControl(control)

        assertThat(entity.getControlOptional(TestControl::class.java).get(), `is`(control))
    }

//    @Test
//    fun `Remove all controls`() {
//        val control = TestControl()
//        entity.addControl(control)
//
//        val control2 = ControlAddingControl()
//        entity.addControl(control2)
//
//        entity.removeAllControls()
//
//        assertThat(entity.controls, not(hasItems(control, control2)))
//    }

//    @Test
//    fun `Remove all controls fails if within update of another control`() {
//        entity.addControl(AllControlRemovingControl())
//        entity.update(0.0)
//    }

    @Test
    fun `Update controls`() {
        val control = ValueUpdateControl()
        entity.addControl(control)

        entity.update(0.017)

        assertThat(control.value, `is`(0.017))
    }

    @Test
    fun `Disable controls`() {
        val control = ValueUpdateControl()
        entity.addControl(control)

        entity.setControlsEnabled(false)

        entity.update(0.017)

        assertThat(control.value, `is`(0.0))
    }

    @Test
    fun `Pause control`() {
        val control = ValueUpdateControl()
        entity.addControl(control)

        control.pause()

        entity.update(0.017)

        assertThat(control.value, `is`(0.0))
    }

    @Test
    fun `Resume control`() {
        val control = ValueUpdateControl()
        entity.addControl(control)

        control.pause()
        control.resume()

        entity.update(0.017)

        assertThat(control.value, `is`(0.017))
    }

    @Test
    fun `Get property returns correct value`() {
        entity.setProperty("hp", 30)

        assertThat(entity.getProperty("hp"), `is`(30))
    }

    @Test
    fun `Get property optional returns correct value`() {
        entity.setProperty("hp", 30)

        assertThat(entity.getPropertyOptional<Int>("hp").get(), `is`(30))
    }

    @Test
    fun `Get property returns null if key not found`() {
        assertThat(entity.getProperty<Any>("no_key"), nullValue())
    }

    @Test
    fun `Get property returns null if value is null`() {
        entity.setProperty("lastTime", null)

        assertThat(entity.getProperty<Any>("lastTime"), nullValue())
    }

    @Test
    fun `Get property optional returns empty if key not found`() {
        val value = entity.getPropertyOptional<Any>("no_key")

        assertFalse(value.isPresent)
    }

    @Test
    fun `Module listener is notified on component add`() {
        val hp = HPComponent(20.0)

        val listener = object : ModuleListener {
            override fun onAdded(component: Component) {
                (component as HPComponent).value = 10.0
            }
        }

        entity.addModuleListener(listener)

        entity.addComponent(hp)

        assertThat(hp.value, `is`(10.0))
    }

    @Test
    fun `Module listener is notified on component remove`() {
        val hp = HPComponent(20.0)

        val listener = object : ModuleListener {
            override fun onRemoved(component: Component) {
                (component as HPComponent).value = 0.0
            }
        }

        entity.addModuleListener(listener)

        entity.addComponent(hp)

        entity.removeComponent(HPComponent::class.java)

        assertThat(hp.value, `is`(0.0))
    }

    @Test
    fun `Module listener is notified on control add`() {
        val control = HPControl(0)

        val listener = object : ModuleListener {
            override fun onAdded(control: Control) {
                (control as HPControl).value = 10
            }
        }

        entity.addModuleListener(listener)
        entity.addComponent(HPComponent(33.0))

        entity.addControl(control)

        assertThat(control.value, `is`(10))
    }

    @Test
    fun `Module listener is notified on control remove`() {
        val control = HPControl()

        val listener = object : ModuleListener {
            override fun onRemoved(control: Control) {
                (control as HPControl).value = 20
            }
        }

        entity.addModuleListener(listener)
        entity.addComponent(HPComponent(33.0))

        entity.addControl(control)

        entity.removeControl(HPControl::class.java)

        assertThat(control.value, `is`(20))
    }

    @Test
    fun `Module listener is removed correctly`() {
        val hp = HPComponent(20.0)

        val listener = object : ModuleListener {
            override fun onAdded(component: Component) {
                (component as HPComponent).value = 0.0
            }
        }

        entity.addModuleListener(listener)
        entity.removeModuleListener(listener)

        entity.addComponent(hp)

        assertThat(hp.value, `is`(20.0))
    }

    @Test
    fun `Save and load`() {
        entity.addComponent(GravityComponent(true))
        entity.addComponent(CustomDataComponent("SerializationData"))
        entity.addControl(CustomDataControl("SerializableControl"))

        val bundle = Bundle("Entity01234")
        entity.save(bundle)

        val entity2 = Entity()
        entity2.addComponent(GravityComponent(false))
        entity2.addComponent(CustomDataComponent(""))
        entity2.addControl(CustomDataControl(""))

        entity2.load(bundle)

        assertThat(entity2.getControl(CustomDataControl::class.java).data, `is`("SerializableControl"))
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
        entity.addControl(HPControl(10))

        val e2 = entity.copy()

        assertTrue(e2.hasComponent(HPComponent::class.java))
        assertTrue(e2.hasControl(HPControl::class.java))

        assertThat(e2.getComponent(HPComponent::class.java).value, `is`(33.0))
        assertThat(e2.getControl(HPControl::class.java).value, `is`(10))
    }

    @Test
    fun testToString() {
        val component = HPComponent(33.0)
        val control = HPControl()

        entity.addComponent(component)
        entity.addControl(control)

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
        entity.addControl(TestControl())

        entity.clean()

        assertThat(entity.controls.size(), `is`(0))
    }

    /* MOCK CLASSES */

    private inner class TestControl : Control() {
        override fun onUpdate(entity: Entity, tpf: Double) {}
    }

    private inner class ControlAddingControl : Control() {
        override fun onUpdate(entity: Entity, tpf: Double) {
            entity.addControl(TestControl())
        }
    }

    private inner class ControlRemovingControl : Control() {
        override fun onUpdate(entity: Entity, tpf: Double) {
            entity.removeControl(TestControl::class.java)
        }
    }

//    private inner class AllControlRemovingControl : Control() {
//        override fun onUpdate(entity: Entity, tpf: Double) {
//            entity.removeAllControls()
//        }
//    }

    private inner class EntityRemovingControl : Control() {
        override fun onUpdate(entity: Entity, tpf: Double) {
            entity.removeFromWorld()
        }
    }

    private inner class ValueUpdateControl : Control() {
        var value = 0.0

        override fun onUpdate(entity: Entity, tpf: Double) {
            value += tpf
        }
    }

    @Required(HPComponent::class)
    private inner class HPControl(var value: Int = 0) : Control(), CopyableControl<HPControl> {

        override fun onUpdate(entity: Entity, tpf: Double) {}

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

    class CustomDataControl constructor(var data: String) : Control(), SerializableControl {

        override fun onUpdate(entity: Entity, tpf: Double) { }

        override fun write(bundle: Bundle) {
            bundle.put("data", data)
        }

        override fun read(bundle: Bundle) {
            data = bundle.get("data")
        }
    }
}