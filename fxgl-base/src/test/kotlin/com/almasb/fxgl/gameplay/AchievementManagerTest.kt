/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.gameplay.achievement.Achievement
import com.almasb.fxgl.gameplay.achievement.AchievementManager
import com.almasb.fxgl.saving.UserProfile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AchievementManagerTest {

    private lateinit var achievementManager: AchievementManager

    @BeforeEach
    fun setUp() {
        achievementManager = AchievementManager()
    }

    @Test
    fun `Register achievement`() {
        val a1 = Achievement("TestAchievement", "TestDescription", "", 0)

        achievementManager.registerAchievement(a1)

        assertThat(achievementManager.getAchievements(), contains(a1))
        assertThat(achievementManager.getAchievementByName("TestAchievement"), `is`(a1))
    }

    @Test
    fun `Fail if achievement not found`() {
        assertThrows(IllegalArgumentException::class.java, {
            achievementManager.getAchievementByName("NoSuchAchievement")
        })
    }

    @Test
    fun `Cannot have achievements with same name`() {
        val a1 = Achievement("TestAchievement", "TestDescription", "", 0)
        val a2 = Achievement("TestAchievement", "TestDescription", "", 0)

        achievementManager.registerAchievement(a1)

        assertThrows(IllegalArgumentException::class.java, {
            achievementManager.registerAchievement(a2)
        })
    }

    @Test
    fun `Serialization`() {
        val profile = UserProfile("1", "1")

        achievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription", "", 0))
        achievementManager.registerAchievement(Achievement("TestAchievement2", "TestDescription", "", 0))
        achievementManager.getAchievementByName("TestAchievement").setAchieved()
        achievementManager.save(profile)

        val newAchievementManager = AchievementManager()
        newAchievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription", "", 0))
        newAchievementManager.registerAchievement(Achievement("TestAchievement2", "TestDescription", "", 0))
        newAchievementManager.load(profile)

        assertThat(newAchievementManager.getAchievements().size, `is`(2))
        assertTrue(newAchievementManager.getAchievementByName("TestAchievement").isAchieved)
        assertFalse(newAchievementManager.getAchievementByName("TestAchievement2").isAchieved)
    }
}