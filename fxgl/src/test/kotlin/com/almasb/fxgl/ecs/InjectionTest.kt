/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ecs

import com.almasb.fxgl.ecs.diff.InjectableControl
import org.hamcrest.core.Is
import org.junit.Assert
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

        Assert.assertThat(entity.hasControl(InjectableControl::class.java), Is.`is`(true))
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