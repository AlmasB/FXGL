/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.entity.component.Component
import com.almasb.fxgl.entity.component.InjectableComponent
import com.almasb.fxgl.entity.component.InjectableControl
import com.almasb.fxgl.entity.component.SubTypeInjectableControl
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
        entity.addComponent(EntityTest.CustomDataControl("InjectControl"))
        entity.addComponent(InjectableControl())

        assertTrue(true)
        assertTrue(entity.hasComponent(InjectableControl::class.java))
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
        entity.addComponent(EntityTest.CustomDataControl("InjectControl"))
        entity.addComponent(SubTypeInjectableControl())

        assertThat(entity.hasComponent(SubTypeInjectableControl::class.java), `is`(true))
    }

    @Test
    fun `Throw if component not present`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataControl("InjectControl"))

        assertThrows(RuntimeException::class.java, {
            entity.addComponent(InjectableControl())
        })
    }

    @Test
    fun `Throw if control not present`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))

        assertThrows(RuntimeException::class.java, {
            entity.addComponent(InjectableControl())
        })
    }

    @Test
    fun `Ignore injection if target field is not Entity`() {
        val entity = Entity()
        entity.addComponent(NameControl())
    }

    class NameControl : Component() {

        // we first check that type is Entity before injecting
        lateinit var name: String
    }
}