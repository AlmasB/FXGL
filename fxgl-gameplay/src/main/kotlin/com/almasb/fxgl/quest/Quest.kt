/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.quest

import com.almasb.fxgl.core.collection.PropertyMap
import com.almasb.fxgl.logging.Logger
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.util.Duration
import java.util.concurrent.Callable

/**
 * A single quest.
 * A quest can have multiple objectives but to start, it needs at least 1.
 * A quest is always in one of four states [QuestState].
 * Valid transitions:
 * NOT_STARTED -> ACTIVE
 * ACTIVE -> COMPLETED
 * ACTIVE -> FAILED
 * FAILED -> ACTIVE
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Quest(val name: String) {

    private val log = Logger.get(javaClass)

    private val objectives = FXCollections.observableArrayList<QuestObjective>()
    private val objectivesReadOnly = FXCollections.unmodifiableObservableList(objectives)

    /**
     * @return read-only copy of the quest's objectives
     */
    fun objectivesProperty(): ObservableList<QuestObjective> = objectivesReadOnly

    private val stateProp = ReadOnlyObjectWrapper(QuestState.NOT_STARTED)

    val state: QuestState
        get() = stateProp.get()

    fun stateProperty(): ReadOnlyObjectProperty<QuestState> = stateProp.readOnlyProperty

    /**
     * @return true if any of the states apart from NOT_STARTED
     */
    val hasStarted: Boolean
        get() = state != QuestState.NOT_STARTED

    @JvmOverloads fun addIntObjective(desc: String, varName: String, varValue: Int, duration: Duration = Duration.ZERO): QuestObjective {
        return IntQuestObjective(desc, varName, varValue, duration).also { addObjective(it) }
    }

    @JvmOverloads fun addBooleanObjective(desc: String, varName: String, varValue: Boolean, duration: Duration = Duration.ZERO): QuestObjective {
        return BooleanQuestObjective(desc, varName, varValue, duration).also { addObjective(it) }
    }

    private fun addObjective(objective: QuestObjective) {
        objectives += objective

        if (hasStarted)
            rebindStateToObjectives()
    }

    fun removeObjective(objective: QuestObjective) {
        objectives -= objective

        if (hasStarted)
            rebindStateToObjectives()
    }

    /**
     * Can only be called from NOT_STARTED state.
     */
    internal fun start() {
        if (objectives.isEmpty()) {
            log.warning("Cannot start quest $name because it has no objectives")
            return
        }

        if (hasStarted) {
            log.warning("Cannot start quest $name because it has already been started")
            return
        }

        rebindStateToObjectives()
    }

    private fun rebindStateToObjectives() {
        val failedBinding = objectives.map { it.stateProperty() }
                .foldRight(Bindings.createBooleanBinding(Callable { false })) { state, binding ->
                    state.isEqualTo(QuestState.FAILED).or(binding)
                }

        val completedBinding = objectives.map { it.stateProperty() }
                .foldRight(Bindings.createBooleanBinding(Callable { true })) { state, binding ->
                    state.isEqualTo(QuestState.COMPLETED).and(binding)
                }

        val intermediateBinding = Bindings.`when`(completedBinding).then(QuestState.COMPLETED).otherwise(QuestState.ACTIVE)
        val finalBinding = Bindings.`when`(failedBinding).then(QuestState.FAILED).otherwise(intermediateBinding)

        stateProp.bind(finalBinding)
    }
}

/**
 * A single quest objective.
 *
 * Valid transitions:
 * ACTIVE -> COMPLETED
 * ACTIVE -> FAILED
 * FAILED -> ACTIVE
 */
sealed class QuestObjective
@JvmOverloads
constructor(

        /**
         * Text that tells the player how to achieve this objective.
         */
        val description: String,

        /**
         * How much time is given to complete this objective.
         * Default: 0 - unlimited.
         */
        val expireDuration: Duration = Duration.ZERO) {

    private val stateProp = ReadOnlyObjectWrapper(QuestState.ACTIVE)

    val state: QuestState
        get() = stateProp.get()

    fun stateProperty(): ReadOnlyObjectProperty<QuestState> = stateProp.readOnlyProperty

    protected val successProp = ReadOnlyBooleanWrapper()

    private val successListener = javafx.beans.value.ChangeListener<Boolean> { _, _, isReached ->
        if (isReached) {
            clean()
            stateProp.value = QuestState.COMPLETED
        }
    }

    init {
        successProp.addListener(successListener)
    }

    fun complete() {
        if (state != QuestState.ACTIVE) {
            return
        }

        unbind()
        successProp.value = true
    }

    fun fail() {
        if (state != QuestState.ACTIVE) {
            return
        }

        unbind()
        successProp.value = false
        clean()
        stateProp.value = QuestState.FAILED
    }

    /**
     * Transition from FAILED -> ACTIVE.
     */
    fun reactivate(vars: PropertyMap) {
        if (state != QuestState.FAILED) {
            return
        }

        stateProp.value = QuestState.ACTIVE
        successProp.addListener(successListener)
        bindTo(vars)
    }

    abstract fun bindTo(vars: PropertyMap)

    internal fun unbind() {
        successProp.unbind()
    }

    private fun clean() {
        successProp.removeListener(successListener)
    }
}

private class IntQuestObjective
@JvmOverloads constructor(
        /**
         * Text that tells the player how to achieve this objective.
         */
        description: String,

        /**
         * Variable name of an int property from the world properties to track.
         */
        val varName: String,

        /**
         * Number of times the objective needs to be completed.
         * For example, for objective "collect 30 herbs", the value needs to be set to 30.
         */
        val varValue: Int,

        /**
         * How much time is given to complete this objective.
         * Default: 0 - unlimited.
         */
        expireDuration: Duration = Duration.ZERO

) : QuestObjective(description, expireDuration) {

    override fun bindTo(vars: PropertyMap) {
        successProp.bind(
                vars.intProperty(varName).greaterThanOrEqualTo(varValue)
        )
    }
}

private class BooleanQuestObjective
@JvmOverloads constructor(
        /**
         * Text that tells the player how to achieve this objective.
         */
        description: String,

        /**
         * Variable name of a boolean property from the world properties to track.
         */
        val varName: String,

        /**
         * Boolean value that needs to be met.
         */
        val varValue: Boolean,

        /**
         * How much time is given to complete this objective.
         * Default: 0 - unlimited.
         */
        expireDuration: Duration = Duration.ZERO

) : QuestObjective(description, expireDuration) {

    override fun bindTo(vars: PropertyMap) {
        successProp.bind(
                vars.booleanProperty(varName).isEqualTo(SimpleBooleanProperty(varValue))
        )
    }
}

enum class QuestState {
    NOT_STARTED, ACTIVE, COMPLETED, FAILED
}