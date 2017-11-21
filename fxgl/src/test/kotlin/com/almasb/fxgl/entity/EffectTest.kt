/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.entity.control.EffectControl
import com.almasb.fxgl.physics.BoundingShape
import com.almasb.fxgl.physics.HitBox
import javafx.geometry.Point2D
import javafx.util.Duration
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class EffectTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    // TODO: can't test this since onUpdate() relies on FXGL.getApp().tpf()
//    fun `Effect`() {
//        val e = Entity()
//        val control = EffectControl()
//        e.addControl(control)
//
//        control.startEffect(TestEffect())
//
//        assertThat(e.getProperty("key"), `is`(10))
//
//        control.on
//        assertThat(e.getProperty("key"), `is`(10))
//    }

    private class TestEffect : Effect(Duration.seconds(1.0)) {
        override fun onStart(entity: Entity) {
            entity.setProperty("key", 10)
        }

        override fun onEnd(entity: Entity) {
            entity.setProperty("key", -10)
        }
    }
}