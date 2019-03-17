/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.entity.Entity
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SensorCollisionHandlerTest {

    @Test
    fun `Default noop`() {
        val handler = object : SensorCollisionHandler() {}
        handler.onCollisionBegin(Entity())
        handler.onCollision(Entity())
        handler.onCollisionEnd(Entity())
    }
}