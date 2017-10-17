/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.entity.diff.InjectableComponent
import com.almasb.fxgl.entity.diff.InjectableControl
import com.almasb.fxgl.entity.diff.SubTypeInjectableControl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InjectionTest {

    @Test
    fun `Component fields are injected into control`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))
        entity.addControl(InjectableControl())

        assertTrue(true)
        assertTrue(entity.hasControl(InjectableControl::class.java))
    }

    @Test
    fun `Component fields are injected into component`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addComponent(InjectableComponent())

        assertTrue(entity.hasComponent(InjectableComponent::class.java))
    }

    @Test
    fun `Subtype fields are injected`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))
        entity.addControl(SubTypeInjectableControl())

        assertThat(entity.hasControl(SubTypeInjectableControl::class.java), `is`(true))
    }

    @Test
    fun `Throw if component not present`() {
        val entity = Entity()
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))

        assertThrows(RuntimeException::class.java, {
            entity.addControl(InjectableControl())
        })
    }

    @Test
    fun `Throw if control not present`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))

        assertThrows(RuntimeException::class.java, {
            entity.addControl(InjectableControl())
        })
    }
}