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

            if (!vars.exists(it.varName)) {
                log.warning("Achievement ${it.name} cannot find property ${it.varName}")
            } else {
                when (it.varValue) {
                    is Int -> {
                        registerIntAchievement(vars, it, it.varValue)
                    }

                    is Double -> {
                        registerDoubleAchievement(vars, it, it.varValue)
                    }

                    is Boolean -> {
                        registerBooleanAchievement(vars, it, it.varValue)
                    }

                    else -> throw IllegalArgumentException("Unknown value type for achievement: " + it.varValue)
                }
            }
        }
    }

    private fun registerIntAchievement(vars: PropertyMap, a: Achievement, value: Int) {
        var listener: PropertyChangeListener<Int>? = null

        listener = object : PropertyChangeListener<Int> {
            private var halfReached = false

            override fun onChange(prev: Int, now: Int) {

                if (!halfReached && now >= value / 2) {
                    halfReached = true
                    eventBus.fireEvent(AchievementProgressEvent(a, now.toDouble(), value.toDouble()))
                }

                if (now >= value) {
                    a.setAchieved()
                    eventBus.fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, a))

                    vars.removeListener(a.varName, listener!!)
                }
            }
        }

        vars.addListener(a.varName, listener)
    }

    private fun registerDoubleAchievement(vars: PropertyMap, a: Achievement, value: Double) {
        var listener: PropertyChangeListener<Double>? = null

        listener = object : PropertyChangeListener<Double> {
            private var halfReached = false

            override fun onChange(prev: Double, now: Double) {

                if (!halfReached && now >= value / 2) {
                    halfReached = true
                    eventBus.fireEvent(AchievementProgressEvent(a, now, value))
                }

                if (now >= value) {
                    a.setAchieved()
                    eventBus.fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, a))

                    vars.removeListener(a.varName, listener!!)
                }
            }
        }

        vars.addListener(a.varName, listener)
    }

    private fun registerBooleanAchievement(vars: PropertyMap, a: Achievement, value: Boolean) {
        var listener: PropertyChangeListener<Boolean>? = null

        listener = object : PropertyChangeListener<Boolean> {

            override fun onChange(prev: Boolean, now: Boolean) {
                if (now == value) {
                    a.setAchieved()
                    eventBus.fireEvent(AchievementEvent(AchievementEvent.ACHIEVED, a))

                    vars.removeListener(a.varName, listener!!)
                }
            }
        }

        vars.addListener(a.varName, listener)
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