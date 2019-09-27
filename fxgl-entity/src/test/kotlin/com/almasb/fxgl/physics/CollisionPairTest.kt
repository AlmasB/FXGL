/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.physics

import com.almasb.fxgl.entity.Entity
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class CollisionPairTest {

    private enum class T {
        T1, T2
    }

    @Test
    fun `The order of collision handler is the order of params`() {
        val pair = CollisionPair()
        val e1 = Entity()
        e1.type = T.T1
        val e2 = Entity()
        e2.type = T.T2

        var count = 0

        pair.init(e1, e2, object : CollisionHandler(T.T2, T.T1) {
            override fun onCollisionBegin(a: Entity, b: Entity) {
                assertThat(a, `is`(e2))
                assertThat(b, `is`(e1))
                count++
            }
        })

        pair.collisionBegin()
        assertThat(count, `is`(1))

        pair.reset()

        pair.init(e1, e2, object : CollisionHandler(T.T1, T.T2) {
            override fun onCollisionBegin(a: Entity, b: Entity) {
                assertThat(a, `is`(e1))
                assertThat(b, `is`(e2))
                count++
            }
        })

        pair.collisionBegin()
        assertThat(count, `is`(2))
    }

    @Test
    fun `Equality`() {
        val e1 = Entity()
        e1.type = T.T1
        val e2 = Entity()
        e2.type = T.T2

        val pair1 = CollisionPair()
        assertThat(pair1.hashCode(), `is`(0))

        pair1.init(e1, e2, object : CollisionHandler(T.T1, T.T2) {})

        val pair2 = CollisionPair()
        pair2.init(e1, e2, object : CollisionHandler(T.T1, T.T2) {})

        assertThat(pair1, `is`(pair2))
        assertTrue(pair1.hashCode() == pair2.hashCode())

        pair2.init(e2, e1, object : CollisionHandler(T.T2, T.T1) {})

        assertThat(pair1, `is`(pair2))
        assertTrue(pair1.hashCode() == pair2.hashCode())
    }
}