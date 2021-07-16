/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")

package com.almasb.fxgl.dsl

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.components.CollidableComponent
import com.almasb.fxgl.physics.BoundingShape
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntityBuilderTest {

    enum class EBTType {
        ONE, TWO
    }

    private lateinit var builder: EntityBuilder

    @BeforeEach
    fun `setUp`() {
        builder = EntityBuilder()
    }

    @Test
    fun `from parses position and type`() {
        val data = SpawnData(15.0, 22.0).put("type", EBTType.TWO)

        val e = entityBuilder(data)
                .build()

        assertThat(e.position, `is`(Point2D(15.0, 22.0)))
        assertThat(e.type, `is`<Any>(EBTType.TWO))
    }

    @Test
    fun `from does not fail if type is not enum`() {
        val data = SpawnData(15.0, 22.0).put("type", "someValue")

        val e = entityBuilder(data)
                .build()

        assertTrue(e.type !is String)
    }

    @Test
    fun `Bbox generation from view`() {
        val e = builder
                .viewWithBBox(Rectangle(40.0, 30.0))
                .build()

        assertThat(e.boundingBoxComponent.hitBoxesProperty().size, `is`(1))

        val box = e.boundingBoxComponent.hitBoxesProperty()[0]

        assertThat(box.width, `is`(40.0))
        assertThat(box.height, `is`(30.0))
    }

    @Test
    fun `on active and not active`() {
        var count = 0

        val e = builder
                .onActive { count++ }
                .onNotActive { count-- }
                .build()

        assertThat(count, `is`(0))

        val world = GameWorld()
        world.addEntity(e)

        assertThat(count, `is`(1))

        world.removeEntity(e)

        assertThat(count, `is`(0))
    }

    @Test
    fun `never updated`() {
        val e = builder
                .neverUpdated()
                .build()

        assertFalse(e.isEverUpdated)
    }

    @Test
    fun `at Vec2 positions the entity correctly`() {
        val e = builder
                .at(Vec2(100.0, 150.0))
                .build()

        assertThat(e.x, `is`(100.0))
        assertThat(e.y, `is`(150.0))
    }

    @Test
    fun `at Point2D positions the entity correctly`() {
        val e = builder
                .at(Point2D(100.0, 150.0))
                .build()

        assertThat(e.x, `is`(100.0))
        assertThat(e.y, `is`(150.0))
    }

    @Test
    fun `at xy positions the entity correctly`() {
        val e = builder
                .at(100.0, 150.0)
                .build()

        assertThat(e.x, `is`(100.0))
        assertThat(e.y, `is`(150.0))
    }

    @Test
    fun `at Point3D positions the entity correctly`() {
        val e = builder
                .at(Point3D(10.0, 15.0, 11.0))
                .build()

        assertThat(e.x, `is`(10.0))
        assertThat(e.y, `is`(15.0))
        assertThat(e.z, `is`(11.0))
    }

    @Test
    fun `at anchored positions the entity correctly`() {
        val e = builder
                .atAnchored(Point2D(50.0, 50.0), Point2D(100.0, 150.0))
                .build()

        assertThat(e.x, `is`(50.0))
        assertThat(e.y, `is`(100.0))
    }

    @Test
    fun `rotation origin xy is set`() {
        val e = builder
                .rotationOrigin(1.0, 2.0)
                .build()

        assertThat(e.transformComponent.rotationOrigin, `is`(Point2D(1.0, 2.0)))
    }

    @Test
    fun `rotation origin Point2D is set`() {
        val e = builder
                .rotationOrigin(Point2D(1.0, 2.0))
                .build()

        assertThat(e.transformComponent.rotationOrigin, `is`(Point2D(1.0, 2.0)))
    }

    @Test
    fun `Entity is correctly rotated`() {
        val e = builder
                .rotate(23.0)
                .build()

        assertThat(e.rotation, `is`(23.0))
    }

    @Test
    fun `Scale origin xy is set`() {
        val e = builder
                .scaleOrigin(1.0, 2.0)
                .build()

        assertThat(e.transformComponent.scaleOrigin, `is`(Point2D(1.0, 2.0)))
    }

    @Test
    fun `Scale origin Point2D is set`() {
        val e = builder
                .scaleOrigin(Point2D(1.0, 2.0))
                .build()

        assertThat(e.transformComponent.scaleOrigin, `is`(Point2D(1.0, 2.0)))
    }

    @Test
    fun `Scale xy is set`() {
        val e = builder
                .scale(1.0, 2.0)
                .build()

        assertThat(e.transformComponent.scaleX, `is`(1.0))
        assertThat(e.transformComponent.scaleY, `is`(2.0))
    }

    @Test
    fun `Scale Point2D is set`() {
        val e = builder
                .scale(Point2D(1.0, 2.0))
                .build()

        assertThat(e.transformComponent.scaleX, `is`(1.0))
        assertThat(e.transformComponent.scaleY, `is`(2.0))
    }

    @Test
    fun `Scale xyz is set`() {
        val e = builder
                .scale(1.0, 2.0, 3.0)
                .build()

        assertThat(e.transformComponent.scaleX, `is`(1.0))
        assertThat(e.transformComponent.scaleY, `is`(2.0))
        assertThat(e.transformComponent.scaleZ, `is`(3.0))
    }

    @Test
    fun `Scale Point3D is set`() {
        val e = builder
                .scale(Point3D(1.0, 2.0, 3.0))
                .build()

        assertThat(e.transformComponent.scaleX, `is`(1.0))
        assertThat(e.transformComponent.scaleY, `is`(2.0))
        assertThat(e.transformComponent.scaleZ, `is`(3.0))
    }

    @Test
    fun `Opacity is set`() {
        val e = builder
                .opacity(0.5)
                .build()

        assertThat(e.opacity, `is`(0.5))
    }

    @Test
    fun `Z index is set`() {
        val e = builder
                .zIndex(333)
                .build()

        assertThat(e.zIndex, `is`(333))
    }

    @Test
    fun `Collidable is set`() {
        val e = builder
                .collidable()
                .build()

        assertTrue(e.getComponent(CollidableComponent::class.java).value)
    }

    @Test
    fun `with adds the component`() {
        val e = builder
                .with(CollidableComponent(true))
                .build()

        assertTrue(e.getComponent(CollidableComponent::class.java).value)
    }

    @Test
    fun `with adds the property`() {
        val e = builder
                .with("testInt", 33)
                .build()

        assertThat(e.getInt("testInt"), `is`(33))
    }

    @Test
    fun `OnClick fires when clicked`() {
        var e2: Entity? = null

        val e = builder
                .onClick { e2 = it }
                .build()

        val event = MouseEvent(MouseEvent.MOUSE_CLICKED, 0.0, 0.0, 0.0, 0.0, MouseButton.PRIMARY, 1,
                false, false, false,
                false, false, false, false, false, false, false, null)

        e.viewComponent.parent.fireEvent(event)

        assertThat(e2, `is`(e))
    }

    @Test
    fun `AnchorFromCenter correctly set`() {
        val e = builder
                .viewWithBBox(Rectangle(100.0, 100.0))
                .anchorFromCenter()
                .build()

        assertThat(e.localAnchor, `is`(Point2D(50.0, 50.0)))
    }

    @Test
    fun `bbox with BoundingShape correctly created`() {
        val shape = BoundingShape.box(40.0, 40.0)

        val e = builder
            .bbox(shape)
            .build()

        val boxes = e.boundingBoxComponent.hitBoxesProperty()
        assertEquals(1, boxes.size)

        val box = boxes[0]
        assertEquals(40.0, box.width)
        assertEquals(40.0, box.height)
    }

    @Test
    fun `Build origin 3D`() {
        val e = entityBuilder()
                .buildOrigin3D()

        assertThat(e.viewComponent.children.size, `is`(3 + 1))
    }
}