/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.gameplay.AchievementEvent
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable
import javafx.collections.FXCollections

/**
 * Responsible for registering and updating achievements.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AchievementManager : UserProfileSavable {

    private val log = Logger.get(javaClass)

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
        log.debug("Registered new achievement \"${a.name}\"")
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