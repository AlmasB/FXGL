/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import com.almasb.fxgl.core.EngineService
import com.almasb.fxgl.core.Inject
import com.almasb.fxgl.core.collection.PropertyChangeListener
import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.core.serialization.Bundle
import com.almasb.fxgl.event.EventBus
import com.almasb.sslogger.Logger
import java.util.*

/**
 * Responsible for registering and updating achievements.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class AchievementManager : EngineService {

    private val log = Logger.get(javaClass)

    // this is a read-only list as populated by the user
    @Inject("achievements")
    private lateinit var achievementsFromSettings: List<Achievement>

    @Inject("eventBus")
    private lateinit var eventBus: EventBus

    private val achievements = mutableListOf<Achievement>()

    /**
     * @return unmodifiable list of achievements
     */
    val achievementsCopy: List<Achievement>
        get() = Collections.unmodifiableList(achievements)

    /**
     * @param name achievement name
     * @return registered achievement
     * @throws IllegalArgumentException if achievement is not registered
     */
    fun getAchievementByName(name: String): Achievement {
        return achievements.find { it.name == name }
                ?: throw IllegalArgumentException("Achievement with name [$name] is not registered!")
    }

    /**
     * Registers achievement in the system.
     * Note: this method can only be called from initAchievements() to function properly.
     *
     * @param a the achievement
     */
    internal fun registerAchievement(a: Achievement) {
        require(achievements.none { it.name == a.name }) {
            "Achievement with name [${a.name}] exists"
        }

        achievements.add(a)
        log.debug("Registered new achievement: ${a.name}")
    }

    override fun onMainLoopStarting() {
        achievementsFromSettings.forEach { registerAchievement(it) }
    }

    override fun onGameReady(vars: PropertyMap) {
        bindToVars(vars)
    }

    internal fun bindToVars(vars: PropertyMap) {
        // only interested in non-achieved achievements
        achievements.filter { !it.isAchieved }.forEach {

            when(it.varValue) {
                is Int -> {
                    var listener: PropertyChangeListener<Int>? = null

                    listener = object : PropertyChangeListener<Int> {
                        var halfReached = false

                        override fun onChange(prev: Int, now: Int) {

                            if (!halfReached && now >= it.varValue / 2) {
                                halfReached = true
                                //FXGL.getEventBus().fireEvent(AchievementProgressEvent(it, now.toDouble(), it.varValue.toDouble()))
                            }

                            if (now >= it.varValue) {
                                it.setAchieved()
                                eventBus.fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, it))

                                vars.removeListener(it.varName, listener!!)
                            }
                        }
                    }

                    vars.addListener(it.varName, listener)
                }

                is Double -> {
                    var listener: PropertyChangeListener<Double>? = null

                    listener = object : PropertyChangeListener<Double> {
                        var halfReached = false

                        override fun onChange(prev: Double, now: Double) {

                            if (!halfReached && now >= it.varValue / 2) {
                                halfReached = true
                                //FXGL.getEventBus().fireEvent(AchievementProgressEvent(it, now, it.varValue))
                            }

                            if (now >= it.varValue) {
                                it.setAchieved()
                                eventBus.fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, it))

                                vars.removeListener(it.varName, listener!!)
                            }
                        }
                    }

                    vars.addListener(it.varName, listener)
                }

                is Boolean -> {
                    var listener: PropertyChangeListener<Boolean>? = null

                    listener = object : PropertyChangeListener<Boolean> {

                        override fun onChange(prev: Boolean, now: Boolean) {
                            if (now) {
                                it.setAchieved()
                                eventBus.fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, it))

                                vars.removeListener(it.varName, listener!!)
                            }
                        }
                    }

                    vars.addListener(it.varName, listener)
                }

                else -> throw IllegalArgumentException("Unknown value type for achievement: " + it.varValue)
            }
        }
    }

    override fun onExit() {
    }

    override fun onUpdate(tpf: Double) {
    }

    override fun write(bundle: Bundle) {
        achievements.forEach { a -> bundle.put(a.name, a.isAchieved) }
    }

    override fun read(bundle: Bundle) {
        achievements.forEach { a ->
            val achieved = bundle.get<Boolean>(a.name)
            if (achieved)
                a.setAchieved()
        }
    }
}