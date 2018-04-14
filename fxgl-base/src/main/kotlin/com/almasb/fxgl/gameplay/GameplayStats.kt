/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay

import com.almasb.fxgl.io.serialization.Bundle
import com.almasb.fxgl.saving.UserProfile
import com.almasb.fxgl.saving.UserProfileSavable
import javafx.beans.property.ReadOnlyLongProperty
import javafx.beans.property.ReadOnlyLongWrapper

/**
 * Data structure containing various gameplay related statistics,
 * e.g. time played, enemies killed, etc.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class GameplayStats : UserProfileSavable {

    private val playtime = ReadOnlyLongWrapper()

    fun playtimeProperty(): ReadOnlyLongProperty = playtime.readOnlyProperty

    /**
     * @return playtime in nanos
     */
    fun getPlaytime(): Long {
        return playtimeProperty().get()
    }

    fun getPlaytimeHours(): Long {
        return (getPlaytime().toDouble() / 1000000000.0 / 3600.0).toLong()
    }

    fun getPlaytimeMinutes(): Long {
        return (getPlaytime().toDouble() / 1000000000.0 / 60.0).toLong() % 60
    }

    fun getPlaytimeSeconds(): Long {
        return (getPlaytime() / 1000000000.0).toLong() % 60
    }

    fun onUpdate(tpf: Double) {
        playtime.value += secondsToNanos(tpf)
    }

    override fun save(profile: UserProfile) {
        val bundle = Bundle("gameplayStats")
        bundle.put("playtime", playtime.value)

        bundle.log()
        profile.putBundle(bundle)
    }

    override fun load(profile: UserProfile) {
        val bundle = profile.getBundle("gameplayStats")
        bundle.log()

        playtime.value = bundle.get<Long>("playtime")
    }

    /**
     * Converts seconds to nanoseconds.
     *
     * @param seconds value in seconds
     * @return value in nanoseconds
     */
    private fun secondsToNanos(seconds: Double) = (seconds * 1000000000L).toLong()
}