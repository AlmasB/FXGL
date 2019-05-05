/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

/**
 * Marks a class that registers achievements.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface AchievementStore {

    fun initAchievements(achievements: List<Achievement>)
}