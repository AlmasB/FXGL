/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.core.collection.PropertyMap
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
class GoapTest {

    @Test
    fun `plan`() {
        val action1 = GoapAction("Equip sword")
        val action2 = GoapAction("Attack with sword")
        val action3 = GoapAction("Pick up sword")

        action1.addPrecondition("pickUpSword", true)
        action1.addEffect("equipSword", true)

        action2.addPrecondition("equipSword", true)
        action2.addEffect("attackWithSword", true)

        action3.addEffect("pickUpSword", true)

        val current = PropertyMap()
        val goal = PropertyMap()
        goal.setValue("attackWithSword", true)

        val actions = GoapPlanner.plan(
            setOf(action1, action2, action3),
            current,
            goal
        )

        assertThat(actions, contains(action3, action1, action2))
    }
}