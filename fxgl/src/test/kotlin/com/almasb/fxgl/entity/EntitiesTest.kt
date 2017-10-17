/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.geometry.Point2D
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EntitiesTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGL.setProperty("dev.showbbox", false)
        }
    }

    private enum class EntityType {
        TEST1
    }

    class TestComponent : Component()

    class TestControl : Control() {
        override fun onUpdate(entity: Entity?, tpf: Double) {
        }
    }

    @Test
    fun `Build`() {
        val e = Entities.builder()
                .at(100.0, 100.0)
                .type(EntityType.TEST1)
                .bbox(HitBox("test", BoundingShape.box(40.0, 40.0)))
                .with("hp", 40)
                .with(TestComponent())
                .with(TestControl())
                .build()

        assertThat(e.hasComponent(TestComponent::class.java), `is`(true))
        assertThat(e.hasControl(TestControl::class.java), `is`(true))
        assertThat(e.getProperty("hp"), `is`(40))
        assertThat(e.position, `is`(Point2D(100.0, 100.0)))
        assertThat(e.boundingBoxComponent.hitBoxesProperty().size, `is`(1))
        assertThat(e.boundingBoxComponent.hitBoxesProperty()[0].name, `is`("test"))
        assertThat(e.isType(EntityType.TEST1), `is`(true))
    }
}