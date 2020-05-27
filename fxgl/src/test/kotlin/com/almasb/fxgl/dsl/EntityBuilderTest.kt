/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.dsl

import com.almasb.fxgl.core.math.Vec2
import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.SpawnData
import com.almasb.fxgl.entity.components.CollidableComponent
import javafx.geometry.Point2D
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions
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

        val e = builder
                .from(data)
                .build()

        assertThat(e.position, `is`(Point2D(15.0, 22.0)))
        assertThat(e.type, `is`<Any>(EBTType.TWO))
    }

    @Test
    fun `from does not fail if type is not enum`() {
        val data = SpawnData(15.0, 22.0).put("type", "someValue")

        val e = builder
                .from(data)
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

        assertThat(e.z, `is`(333))
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
}