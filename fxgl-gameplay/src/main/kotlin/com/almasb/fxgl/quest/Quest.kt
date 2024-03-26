/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.quest

import com.almasb.fxgl.core.Updatable
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
class Quest
@JvmOverloads constructor(name: String, val vars: PropertyMap = PropertyMap()) : Updatable {

    private val log = Logger.get(javaClass)

    private val nameProp = SimpleStringProperty(name)

    var name: String
        get() = nameProp.value
        set(value) { nameProp.value = value }

    fun nameProperty() = nameProp

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
    val isStarted: Boolean
        get() = state != QuestState.NOT_STARTED

    @JvmOverloads fun addIntObjective(desc: String, varName: String, varValue: Int, duration: Duration = Duration.ZERO): QuestObjective {
        return IntQuestObjective(desc, vars, varName, varValue, duration).also { addObjective(it) }
    }

    @JvmOverloads fun addBooleanObjective(desc: String, varName: String, varValue: Boolean, duration: Duration = Duration.ZERO): QuestObjective {
        return BooleanQuestObjective(desc, vars, varName, varValue, duration).also { addObjective(it) }
    }

    private fun addObjective(objective: QuestObjective) {
        objectives += objective

        if (isStarted)
            rebindStateToObjectives()
    }

    fun removeObjective(objective: QuestObjective) {
        objectives -= objective

        if (isStarted)
            rebindStateToObjectives()
    }

    /**
     * Can only be called from NOT_STARTED state.
     * Binds quest state to the combined state of its objectives.
     */
    internal fun start() {
        if (objectives.isEmpty()) {
            log.warning("Cannot start quest $name because it has no objectives")
            return
        }

        if (isStarted) {
            log.warning("Cannot start quest $name because it has already been started")
            return
        }

        rebindStateToObjectives()
    }

    override fun onUpdate(tpf: Double) {
        objectives.forEach { it.onUpdate(tpf) }
    }

    /**
     * Sets the state to NOT_STARTED and unbinds objectives from the variables they are tracking.
     */
    internal fun stop() {
        stateProp.unbind()
        stateProp.value = QuestState.NOT_STARTED

        objectives.forEach { it.unbindFromVars() }
    }

    private fun rebindStateToObjectives() {
        objectives.forEach { it.bindToVars() }

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
         * Variables map, from which to check whether the objective is complete.
         */
        protected val vars: PropertyMap,

        /**
         * How much time is given to complete this objective.
         * Default: 0 - unlimited.
         */
        val expireDuration: Duration = Duration.ZERO) : Updatable {

    private val stateProp = ReadOnlyObjectWrapper(QuestState.ACTIVE)

    val state: QuestState
        get() = stateProp.get()

    fun stateProperty(): ReadOnlyObjectProperty<QuestState> = stateProp.readOnlyProperty

    private val timeRemainingProp = ReadOnlyDoubleWrapper(expireDuration.toSeconds())

    /**
     * @return time remaining (in seconds) to complete this objective,
     * returns 0.0 if unlimited
     */
    val timeRemaining: Double
        get() = timeRemainingProp.value

    fun timeRemainingProperty(): ReadOnlyDoubleProperty = timeRemainingProp.readOnlyProperty

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

    override fun onUpdate(tpf: Double) {
        if (state != QuestState.ACTIVE)
            return

        // ignore if no duration is set
        if (expireDuration.lessThanOrEqualTo(Duration.ZERO))
            return

        val remaining = timeRemaining - tpf

        if (remaining <= 0) {
            timeRemainingProp.value = 0.0
            fail()
        } else {
            timeRemainingProp.value = remaining
        }
    }

    fun complete() {
        if (state != QuestState.ACTIVE) {
            return
        }

        unbindFromVars()
        successProp.value = true
    }

    fun fail() {
        if (state != QuestState.ACTIVE) {
            return
        }

        unbindFromVars()
        successProp.value = false
        clean()
        stateProp.value = QuestState.FAILED
    }

    /**
     * Transition from FAILED -> ACTIVE.
     */
    fun reactivate() {
        if (state != QuestState.FAILED) {
            return
        }

        stateProp.value = QuestState.ACTIVE
        timeRemainingProp.value = expireDuration.toSeconds()
        successProp.addListener(successListener)
        bindToVars()
    }

    /**
     * Bind the state to variables, so that the state
     * is updated as variables change.
     */
    internal abstract fun bindToVars()

    internal fun unbindFromVars() {
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

        vars: PropertyMap,

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

) : QuestObjective(description, vars, expireDuration) {

    override fun bindToVars() {
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

        vars: PropertyMap,

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

) : QuestObjective(description, vars, expireDuration) {

    override fun bindToVars() {
        successProp.bind(
                vars.booleanProperty(varName).isEqualTo(SimpleBooleanProperty(varValue))
        )
    }
}

enum class QuestState {
    NOT_STARTED, ACTIVE, COMPLETED, FAILED
}