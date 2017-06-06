/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.app.FXGL
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Duration

/**
 * A single quest objective.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class QuestObjective
@JvmOverloads
constructor(

        /**
         * Text that tells the user how to achieve this objective
         */
        val description: String,

        /**
         * Property that keeps track of number of times user has completed objective.
         */
        val valueProperty: IntegerProperty,

        /**
         * Number of times the objective needs to be completed.
         * Default: 1.
         */
        val times: Int = 1,

        /**
         * How much time is given to complete this objective.
         * Default: unlimited.
         */
        val expireDuration: Duration = Duration.ZERO) {

    private val state = ReadOnlyObjectWrapper<QuestState>(QuestState.ACTIVE)

    fun stateProperty(): ReadOnlyObjectProperty<QuestState> = state.readOnlyProperty

    fun getState() = state.get()

    private fun setState(state: QuestState) {
        if (state == QuestState.ACTIVE) {
            throw IllegalArgumentException("Quest objective cannot be reactivated!")
        }

        clean()
        this.state.set(state)
    }

    private val successBinding = valueProperty.greaterThanOrEqualTo(times)
    private val listener = javafx.beans.value.ChangeListener<Boolean> { o, old, isReached ->
        if (isReached) {
            setState(QuestState.COMPLETED)
        }
    }

    private var failBinding: com.almasb.fxgl.time.TimerAction? = null

    init {
        successBinding.addListener(listener)

        if (expireDuration !== Duration.ZERO) {
            failBinding = FXGL.getMasterTimer().runOnceAfter({ setState(QuestState.FAILED) }, expireDuration)
        }
    }

    private fun clean() {
        successBinding.removeListener(listener)
        failBinding?.expire()
    }
}