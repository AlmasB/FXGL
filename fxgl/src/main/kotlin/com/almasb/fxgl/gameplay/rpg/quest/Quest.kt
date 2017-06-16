/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.gameplay.rpg.quest

import javafx.beans.binding.Bindings
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
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