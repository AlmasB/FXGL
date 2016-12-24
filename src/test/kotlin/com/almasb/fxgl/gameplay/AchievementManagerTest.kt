/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.MockApplicationModule
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AchievementManagerTest {

    companion object {
        @BeforeClass
        @JvmStatic fun before() {
            FXGL.configure(MockApplicationModule.get())
        }
    }

    private lateinit var achievementManager: AchievementManager

    @Before
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

    @Test(expected = IllegalArgumentException::class)
    fun `Fail if achievement not found`() {
        val a1 = Achievement("TestAchievement", "TestDescription")

        achievementManager.registerAchievement(a1)
        achievementManager.getAchievementByName("NoSuchAchievement")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Cannot have achievements with same name`() {
        val a1 = Achievement("TestAchievement", "TestDescription")
        val a2 = Achievement("TestAchievement", "TestDescription")

        achievementManager.registerAchievement(a1)
        achievementManager.registerAchievement(a2)
    }

    @Test
    fun `Save to bundle`() {
        // TODO:
    }

    @Test
    fun `Load from bundle`() {
        // TODO:
    }
}