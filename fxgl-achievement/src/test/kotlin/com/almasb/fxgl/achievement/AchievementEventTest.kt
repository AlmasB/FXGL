/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.sameInstance
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AchievementEventTest {

    @Test
    fun `Achievement`() {
        val a = Achievement("testName", "test description on how to achieve", "varNameToTrack", 500)

        val event = AchievementEvent(AchievementEvent.ACHIEVED, a)

        assertThat(event.achievement, sameInstance(a))

        val event2 = AchievementEvent(a)

        assertTrue(event2.eventType == AchievementEvent.ACHIEVED)

        assertThat(event.toString(), `is`("AchievementEvent[type=ACHIEVED, name=testName, description=test description on how to achieve]"))
    }
}