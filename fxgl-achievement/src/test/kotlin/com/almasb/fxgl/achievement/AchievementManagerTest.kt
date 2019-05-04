/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.event.EventBus
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

    private val a1 = Achievement("TestAchievement", "TestDescription", "", 0)

    @BeforeEach
    fun setUp() {
        achievementManager = AchievementManager()

        // TODO: reflect set via a common fxgl-test func, make them private
        achievementManager.achievementStores = emptyList()
        achievementManager.eventBus = EventBus()
    }

    @Test
    fun `Register achievement`() {
        achievementManager.registerAchievement(a1)

        assertThat(achievementManager.achievements, contains(a1))
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
        val a2 = Achievement("TestAchievement", "TestDescription", "", 0)

        achievementManager.registerAchievement(a1)

        assertThrows(IllegalArgumentException::class.java, {
            achievementManager.registerAchievement(a2)
        })
    }

    @Test
    fun `Achievement is unlocked when var reaches required value`() {
        val a = Achievement("TestAchievement", "TestDescription", "varName", 2)

        achievementManager.registerAchievement(a)

        val map = PropertyMap()
        map.setValue("varName", 0)

        achievementManager.bindToVars(map)

        assertFalse(a.isAchieved)

        map.setValue("varName", 1)
        assertFalse(a.isAchieved)

        map.setValue("varName", 2)
        assertTrue(a.isAchieved)
    }

//    @Test
//    fun `Serialization`() {
//        val profile = UserProfile("1", "1")
//
//        achievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription", "", 0))
//        achievementManager.registerAchievement(Achievement("TestAchievement2", "TestDescription", "", 0))
//        achievementManager.getAchievementByName("TestAchievement").setAchieved()
//        achievementManager.save(profile)
//
//        val newAchievementManager = AchievementManager()
//        newAchievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription", "", 0))
//        newAchievementManager.registerAchievement(Achievement("TestAchievement2", "TestDescription", "", 0))
//        newAchievementManager.load(profile)
//
//        assertThat(newAchievementManager.getAchievements().size, `is`(2))
//        assertTrue(newAchievementManager.getAchievementByName("TestAchievement").isAchieved)
//        assertFalse(newAchievementManager.getAchievementByName("TestAchievement2").isAchieved)
//    }
}