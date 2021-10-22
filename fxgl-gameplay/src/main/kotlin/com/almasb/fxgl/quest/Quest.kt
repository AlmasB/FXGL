/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.quest

import com.almasb.fxgl.core.collection.PropertyMap
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.util.Duration
import java.util.concurrent.Callable

/**
 * TODO: allow adding / removing objectives after construction.
 *
 * A single quest.
 * A quest can have multiple objectives but at least 1.
 * A quest is always in one of four states [QuestState].
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class Quest(val name: String,
            val objectives: List<QuestObjective>) {

    private val stateProp = ReadOnlyObjectWrapper(QuestState.NOT_STARTED)

    val state: QuestState
        get() = stateProp.get()

    fun stateProperty(): ReadOnlyObjectProperty<QuestState> = stateProp.readOnlyProperty

    // TODO: check transitions between states
    internal fun start() {
        stateProp.value = QuestState.ACTIVE

        require(objectives.isNotEmpty()) { "Quest must have at least 1 objective" }

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
 * TODO: allow user to manually fail an objective.
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

    private val stateProp = ReadOnlyObjectWrapper(QuestState.NOT_STARTED)

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

    abstract fun bindTo(vars: PropertyMap)

    internal fun unbind() {
        successProp.unbind()
    }

    private fun clean() {
        successProp.removeListener(successListener)
        //failBinding?.expire()
    }

//  TODO:
//    private fun setState(state: QuestState) {
//        require(state != QuestState.ACTIVE) { "Quest objective cannot be reactivated!" }
//
//        clean()
//        this.state.set(state)
//    }
//
//    private var failBinding: com.almasb.fxgl.time.TimerAction? = null
//
//    init {
//        if (expireDuration !== Duration.ZERO) {
//            //failBinding = FXGL.getMasterTimer().runOnceAfter({ setState(QuestState.FAILED) }, expireDuration)
//        }
//    }
}

class IntQuestObjective
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

class BooleanQuestObjective
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