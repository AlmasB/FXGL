/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AchievementTest {

    @Test
    fun `Achievement`() {
        val a = Achievement("testName", "test description on how to achieve", "varNameToTrack", 500)

        assertThat(a.name, `is`("testName"))
        assertThat(a.description, `is`("test description on how to achieve"))
        assertThat(a.varName, `is`("varNameToTrack"))
        assertThat(a.varValue as Int, `is`<Int>(500))

        assertThat(a.isAchieved, `is`(false))

        a.setAchieved()

        assertThat(a.achievedProperty().value, `is`(true))

        // does nothing
        a.setAchieved()

        assertThat(a.toString(), `is`("testName:achieved(true)"))
    }
}