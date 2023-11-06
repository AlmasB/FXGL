/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
@file:Suppress("JAVA_MODULE_DOES_NOT_DEPEND_ON_MODULE")
package com.almasb.fxgl.particle

import com.almasb.fxgl.entity.Entity
import com.almasb.fxgl.entity.GameWorld
import com.almasb.fxgl.entity.component.ComponentHelper
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 * @author Jean-Rene Lavoie (jeanrlavoie@gmail.com)
 */
class ParticleComponentTest {

    private lateinit var world: GameWorld
    private lateinit var particule: ParticleComponent

    @BeforeEach
    fun setUp() {
        world = GameWorld()
        particule = ParticleComponent(ParticleEmitter())
    }

    @Test
    fun `Create ParticuleComponent with zIndex`() {
        assertNull(particule.entity)
        assertNotNull(particule.parent)
        assertThat(particule.parent.zIndex, `is`(0))

        val e = Entity()
        e.zIndex = 100

        ComponentHelper.setEntity(particule, e)
        world.addEntity(e)
        particule.onAdded()
        particule.onUpdate(1.0)

        assertThat(particule.parent.zIndex, `is`(100))

        e.zIndex = 200
        particule.onUpdate(1.0)

        assertThat(particule.parent.zIndex, `is`(100))
    }

}