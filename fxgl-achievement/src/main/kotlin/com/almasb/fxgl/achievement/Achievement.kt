/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper

/**
 * A game achievement.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
class Achievement(

        /**
         * Name of this achievement, as shown in UI.
         */
        val name: String,

        /**
         * Contains info on how to unlock the achievement.
         */
        val description: String,

        /**
         * The name of the variable to track (from GameState).
         */
        val varName: String,

        /**
         * If the variable value is greater than this value the achievement will be unlocked.
         * Can be of type int, double or boolean.
         */
        val varValue: Any) {

    private val achieved = ReadOnlyBooleanWrapper(false)

    internal fun setAchieved() {
        if (isAchieved)
            return

        achieved.set(true)
    }

    /**
     * @return achieved boolean property (read-only)
     */
    fun achievedProperty(): ReadOnlyBooleanProperty {
        return achieved.readOnlyProperty
    }

    /**
     * @return true iff the achievement has been unlocked
     */
    val isAchieved: Boolean
        get() = achievedProperty().get()

    override fun toString(): String {
        return "$name:achieved($isAchieved)"
    }
}
