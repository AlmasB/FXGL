/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.event.EventBus
import com.almasb.fxgl.test.InjectInTest.inject
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.contains
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.invoke.MethodHandles
import java.util.stream.Stream

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AchievementManagerTest {

    private lateinit var achievementManager: AchievementManager

    private val a1 = Achievement("TestAchievement", "TestDescription", "", 0)
    private val a2 = Achievement("TestAchievement2", "TestDescription2", "", 0)

    @BeforeEach
    fun setUp() {
        achievementManager = AchievementManager()
        val lookup = MethodHandles.lookup()

        inject(lookup, achievementManager, "achievementsFromSettings", listOf(a2))
        inject(lookup, achievementManager, "eventBus",  EventBus())
    }

    @Test
    fun `Settings integration test`() {
        assertTrue(achievementManager.achievementsCopy.isEmpty())

        achievementManager.onMainLoopStarting()

        assertThat(achievementManager.achievementsCopy, contains(a2))
    }

    @Test
    fun `Register achievement`() {
        achievementManager.registerAchievement(a1)

        assertThat(achievementManager.achievementsCopy, contains(a1))
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

        assertThrows(IllegalArgumentException::class.java) {
            achievementManager.registerAchievement(a2)
        }
    }

    @Test
    fun `Serialization`() {
        val bundle = Bundle("achievements")

        achievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription", "", 0))
        achievementManager.registerAchievement(Achievement("TestAchievement2", "TestDescription", "", 0))
        achievementManager.getAchievementByName("TestAchievement").setAchieved()
        achievementManager.write(bundle)

        val newAchievementManager = AchievementManager()
        newAchievementManager.registerAchievement(Achievement("TestAchievement", "TestDescription", "", 0))
        newAchievementManager.registerAchievement(Achievement("TestAchievement2", "TestDescription", "", 0))
        newAchievementManager.read(bundle)

        assertThat(newAchievementManager.achievementsCopy.size, `is`(2))
        assertTrue(newAchievementManager.getAchievementByName("TestAchievement").isAchieved)
        assertFalse(newAchievementManager.getAchievementByName("TestAchievement2").isAchieved)
    }

    @ParameterizedTest
    @MethodSource("varValueProvider")
    fun `Achievement is unlocked when var reaches required value`(varValue: Any, initialValue: Any, value1: Any) {
        val a = Achievement("TestAchievement", "TestDescription", "varName", varValue)

        achievementManager.registerAchievement(a)

        val map = PropertyMap()
        map.setValue("varName", initialValue)

        achievementManager.bindToVars(map)

        assertFalse(a.isAchieved)

        map.setValue("varName", value1)
        assertFalse(a.isAchieved)

        // check for game restart

        val map2 = PropertyMap()
        map2.setValue("varName", initialValue)

        achievementManager.onGameReady(map2)

        map2.setValue("varName", varValue)
        assertTrue(a.isAchieved)

        // old map should no longer trigger achievement events
        // currently it does since we do not clear listeners from the old map
        // but it should not be possible to modify the old map
//        map.setValue("varName", varValue)
//        assertFalse(a.isAchieved)
    }

    @Test
    fun `Bind to vars does not fail if manager has achievements for non-existing var`() {
        assertDoesNotThrow {
            val a = Achievement("TestAchievement", "TestDescription", "varName", 3)

            achievementManager.registerAchievement(a)

            val map = PropertyMap()

            achievementManager.bindToVars(map)
        }
    }

    @Test
    fun `Bind to vars throws if achievement value is not int double or bool`() {
        assertThrows(IllegalArgumentException::class.java) {
            val a = Achievement("TestAchievement", "TestDescription", "varName", "hi")

            achievementManager.registerAchievement(a)

            val map = PropertyMap()
            map.setValue("varName", "hi")

            achievementManager.bindToVars(map)
        }
    }

    companion object {
        @JvmStatic fun varValueProvider(): Stream<Arguments> {
            return Stream.of(
                    arguments(2, 1, 0),
                    arguments(50.0, 0.0, 10.0),
                    arguments(true, false, false)
            )
        }
    }
}