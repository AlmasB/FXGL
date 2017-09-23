/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGLMock
import com.almasb.fxgl.saving.UserProfile
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AchievementManagerTest {

    companion object {
        @BeforeAll
        @JvmStatic fun before() {
            FXGLMock.mock()
        }
    }

    private lateinit var achievementManager: AchievementManager

    @BeforeEach
    fun setUp() {
        achievementManager = FXGL.getInstance(AchievementManager::class.java)
    }

    @Test
    fun `Register achievement`() {
        val a1 = Achievement("TestAchievement", "TestDescription")

        achievementManager.registerAchievement(a1)

        assertThat(achievementManager.getAchievements(), hasItem(a1))
        assertThat(achievementManager.getAchievementByName("TestAchievement"), `is`(a1))
    }

    @Test
    fun `Fail if achievement not found`() {
        val a1 = Achievement("TestAchievement", "TestDescription")

        achievementManager.registerAchievement(a1)

        assertThrows(IllegalArgumentException::class.java, {
            achievementManager.getAchievementByName("NoSuchAchievement")
        })
    }

    @Test
    fun `Cannot have achievements with same name`() {
        val a1 = Achievement("TestAchievement", "TestDescription")
        val a2 = Achievement("TestAchievement", "TestDescription")

        achievementManager.registerAchievement(a1)

        assertThrows(IllegalArgumentException::class.java, {
            achievementManager.registerAchievement(a2)
        })
    }

    @Test
    fun `Serialization`() {
        val profile = UserProfile("1", "1")

        achievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription"))
        achievementManager.getAchievementByName("TestAchievement").setAchieved()
        achievementManager.save(profile)

        val newAchievementManager = FXGL.getInstance(AchievementManager::class.java)
        newAchievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription"))
        newAchievementManager.load(profile)

        assertThat(newAchievementManager.getAchievements().size, `is`(1))
        assertThat(newAchievementManager.getAchievementByName("TestAchievement").isAchieved, `is`(true))
    }
}