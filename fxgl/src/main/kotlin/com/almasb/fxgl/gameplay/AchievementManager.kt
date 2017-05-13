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
import com.almasb.fxgl.gameplay.AchievementEvent
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.saving.UserProfileSavable
import com.almasb.fxgl.saving.UserProfile
import com.google.inject.Inject
import javafx.collections.FXCollections

/**
 * Responsible for registering and updating achievements.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AchievementManager : UserProfileSavable {

    private val log = FXGL.getLogger(javaClass)

    private val achievements = FXCollections.observableArrayList<Achievement>()

    /**
     * Registers achievement in the system.
     * Note: this method can only be called from initAchievements() to function
     * properly.
     *
     * @param a the achievement
     */
    fun registerAchievement(a: Achievement) {
        val count = achievements.filter { it.name == a.name }.size

        if (count > 0)
            throw IllegalArgumentException("Achievement with name \"${a.name}\" exists")


        a.setOnAchieved(Runnable { FXGL.getEventBus().fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, a)) })
        achievements.add(a)
        log.debug { "Registered new achievement \"${a.name}\"" }
    }

    /**
     * @param name achievement name
     *
     * @return registered achievement
     *
     * @throws IllegalArgumentException if achievement is not registered
     */
    fun getAchievementByName(name: String): Achievement {
        for (a in achievements)
            if (a.name == name)
                return a

        throw IllegalArgumentException("Achievement with name \"$name\" is not registered!")
    }

    /**
     * @return unmodifiable list of achievements
     */
    fun getAchievements() = FXCollections.unmodifiableObservableList(achievements)

    override fun save(profile: UserProfile) {
        log.debug("Saving data to profile")

        val bundle = Bundle("achievement")

        achievements.forEach { a -> bundle.put(a.name, a.isAchieved) }
        bundle.log()

        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        log.debug("Loading data from profile")

        val bundle = profile.getBundle("achievement")
        bundle.log()

        achievements.forEach { a ->
            val achieved = bundle.get<Boolean>(a.name)
            if (achieved)
                a.setAchieved()
        }
    }
}