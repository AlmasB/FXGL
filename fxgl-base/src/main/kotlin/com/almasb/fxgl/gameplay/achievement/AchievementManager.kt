/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.achievement

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.core.logging.Logger
import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable
import javafx.collections.FXCollections
import javafx.collections.ObservableList

/**
 * Responsible for registering and updating achievements.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AchievementManager : UserProfileSavable {

    private val log = Logger.get(javaClass)

    private val achievements = FXCollections.observableArrayList<Achievement>()
    private val achievementsReadOnly by lazy { FXCollections.unmodifiableObservableList(achievements) }

    /**
     * Registers achievement in the system.
     * Note: this method can only be called from initAchievements() to function properly.
     *
     * @param a the achievement
     */
    fun registerAchievement(a: Achievement) {
        if (achievements.find { it.name == a.name } != null)
            throw IllegalArgumentException("Achievement with name \"${a.name}\" exists")

        achievements.add(a)
        log.debug("Registered new achievement \"${a.name}\"")
    }

    /**
     * @param name achievement name
     * @return registered achievement
     * @throws IllegalArgumentException if achievement is not registered
     */
    fun getAchievementByName(name: String): Achievement {
        return achievements.find { it.name == name }
                ?: throw IllegalArgumentException("Achievement with name \"$name\" is not registered!")
    }

    /**
     * @return unmodifiable list of achievements
     */
    fun getAchievements(): ObservableList<Achievement> = achievementsReadOnly

    internal fun rebindAchievements() {
        achievements.forEach {
            when(it.varValue) {
                is Int -> {
                    var listener: PropertyChangeListener<Int>? = null

                    listener = object : PropertyChangeListener<Int> {
                        var halfReached = false

                        override fun onChange(prev: Int, now: Int) {

                            if (!halfReached && now >= it.varValue / 2) {
                                halfReached = true
                                FXGL.getEventBus().fireEvent(AchievementProgressEvent(it, now.toDouble(), it.varValue.toDouble()))
                            }

                            if (now >= it.varValue) {
                                it.setAchieved()
                                FXGL.getEventBus().fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, it))
                                FXGL.getApp().gameState.removeListener(it.varName, listener!!)
                            }
                        }
                    }

                    FXGL.getApp().gameState.addListener<Int>(it.varName, listener)
                }

                is Double -> {
                    var listener: PropertyChangeListener<Double>? = null

                    listener = object : PropertyChangeListener<Double> {
                        var halfReached = false

                        override fun onChange(prev: Double, now: Double) {

                            if (!halfReached && now >= it.varValue / 2) {
                                halfReached = true
                                FXGL.getEventBus().fireEvent(AchievementProgressEvent(it, now, it.varValue))
                            }

                            if (now >= it.varValue) {
                                it.setAchieved()
                                FXGL.getEventBus().fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, it))
                                FXGL.getApp().gameState.removeListener(it.varName, listener!!)
                            }
                        }
                    }

                    FXGL.getApp().gameState.addListener<Double>(it.varName, listener)
                }

                is Boolean -> {
                    var listener: PropertyChangeListener<Boolean>? = null

                    listener = object : PropertyChangeListener<Boolean> {

                        override fun onChange(prev: Boolean, now: Boolean) {
                            if (now) {
                                it.setAchieved()
                                FXGL.getEventBus().fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, it))
                                FXGL.getApp().gameState.removeListener(it.varName, listener!!)
                            }
                        }
                    }

                    FXGL.getApp().gameState.addListener<Boolean>(it.varName, listener)
                }

                else -> throw IllegalArgumentException("Unknown value type for achievement: " + it.varValue)
            }
        }
    }

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