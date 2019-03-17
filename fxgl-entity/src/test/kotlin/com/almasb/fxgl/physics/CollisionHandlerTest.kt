/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.entity.Entity
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CollisionHandlerTest {

    @ParameterizedTest
    @MethodSource("handlerProvider")
    fun `Default noop`(handler: CollisionHandler) {
        handler.onHitBoxTrigger(Entity(), Entity(), HitBox(BoundingShape.circle(1.0)), HitBox(BoundingShape.circle(1.0)))
        handler.onCollisionBegin(Entity(), Entity())
        handler.onCollision(Entity(), Entity())
        handler.onCollisionEnd(Entity(), Entity())
    }

    companion object {
        @JvmStatic fun handlerProvider(): Stream<CollisionHandler> {
            return Stream.of(object : CollisionHandler("", "") {}, object : CollisionHandler("", "") {}.copyFor(1, 2))
        }
    }
}