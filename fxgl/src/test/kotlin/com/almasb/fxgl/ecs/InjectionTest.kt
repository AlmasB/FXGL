/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

import com.almasb.fxgl.ecs.diff.InjectableControl
import com.almasb.fxgl.ecs.diff.SubTypeInjectableControl
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class InjectionTest {

    @Test
    fun `Component fields are injected`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))
        entity.addControl(InjectableControl())

        assertThat(entity.hasControl(InjectableControl::class.java), `is`(true))
    }

    @Test
    fun `Subtype fields are injected`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))
        entity.addControl(SubTypeInjectableControl())

        assertThat(entity.hasControl(SubTypeInjectableControl::class.java), `is`(true))
    }

    @Test(expected = RuntimeException::class)
    fun `Throw if component not present`() {
        val entity = Entity()
        entity.addControl(EntityTest.CustomDataControl("InjectControl"))
        entity.addControl(InjectableControl())
    }

    @Test(expected = RuntimeException::class)
    fun `Throw if control not present`() {
        val entity = Entity()
        entity.addComponent(EntityTest.CustomDataComponent("Inject"))
        entity.addControl(InjectableControl())
    }
}