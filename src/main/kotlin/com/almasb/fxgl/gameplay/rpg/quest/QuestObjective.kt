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

package com.almasb.fxgl.gameplay.rpg.quest

import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.time.TimerAction
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleObjectProperty
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

    private val state = SimpleObjectProperty<QuestState>(QuestState.ACTIVE)

    // TODO: make read only
    fun stateProperty() = state

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

    private var failBinding: TimerAction? = null

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