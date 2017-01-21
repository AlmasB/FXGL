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

import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleObjectProperty
import java.util.concurrent.Callable

/**
 * A single quest.
 * A quest can have multiple objectives but at least 1.
 * A quest can be in one of three states [QuestState].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Quest(val name: String, val objectives: List<QuestObjective>) {

    private val state = ReadOnlyObjectWrapper<QuestState>(QuestState.ACTIVE)

    fun stateProperty(): ReadOnlyObjectProperty<QuestState> = state.readOnlyProperty

    /**
     * @return current state of this quest
     */
    fun getState() = state.get()

    init {
        if (objectives.isEmpty())
            throw IllegalArgumentException("Quest must have at least 1 objective")

        val failedBinding = objectives.map { it.stateProperty() }
                .foldRight(Bindings.createBooleanBinding(Callable { false }), { state, binding ->
                    state.isEqualTo(QuestState.FAILED).or(binding)
                })

        val completedBinding = objectives.map { it.stateProperty() }
                .foldRight(Bindings.createBooleanBinding(Callable { true }), { state, binding ->
                    state.isEqualTo(QuestState.COMPLETED).and(binding)
                })

        val intermediateBinding = Bindings.`when`(completedBinding).then(QuestState.COMPLETED).otherwise(QuestState.ACTIVE)
        val finalBinding = Bindings.`when`(failedBinding).then(QuestState.FAILED).otherwise(intermediateBinding)

        state.bind(finalBinding)
    }
}