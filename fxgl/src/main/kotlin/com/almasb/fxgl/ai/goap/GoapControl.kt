/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.ecs.Control
import com.almasb.fxgl.ecs.Entity
import com.almasb.fxgl.ecs.component.Required
import com.almasb.fxgl.entity.Entities
import com.almasb.fxgl.entity.component.PositionComponent
import java.util.*

/**
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(PositionComponent::class)
class GoapControl(private val agent: GoapAgent // this is the implementing class that provides our world data and listens to feedback on planning
                  , private val moveSpeed: Double,
                  actions: Set<GoapAction>) : Control() {

    private val stateMachine = FSM()

    /**
     * Thinking (finds something to do).
     */
    private val idleState: FSMState

    /**
     * Moving to target.
     */
    private val moveToState: FSMState

    /**
     * Performing an action.
     */
    private val performActionState: FSMState

    private val availableActions = HashSet(actions)
    private var currentActions: Queue<GoapAction>

    private lateinit var position: PositionComponent

    init {
        currentActions = ArrayDeque<GoapAction>()

        idleState = createIdleState()
        moveToState = createMoveToState()
        performActionState = createPerformActionState()

        stateMachine.pushState(idleState)
    }

    override fun onAdded(entity: Entity) {
        position = Entities.getPosition(entity)
    }

    private var tpf: Double = 0.0

    override fun onUpdate(entity: Entity, tpf: Double) {
        this.tpf = tpf
        stateMachine.update(entity)
    }

    fun addAction(action: GoapAction) {
        availableActions.add(action)
    }

    fun removeAction(action: GoapAction) {
        availableActions.remove(action)
    }

    private fun hasActionPlan() = currentActions.isNotEmpty()

    /**
     * Called during update.
     * Move the agent towards the target in order
     * for the next action to be able to perform.
     * Return true if the Agent is at the target and the next action can perform.
     * False if it is not there yet.
     */
    private fun moveAgent(nextAction: GoapAction): Boolean {
        if (nextAction.target == null) {
            throw IllegalArgumentException("GoapAction: $nextAction has no target")
        }

        val targetPosition = nextAction.target!!.getComponent(PositionComponent::class.java)
                ?: throw IllegalArgumentException("GoapAction: $nextAction has target without PositionComponent")

        val moveDistance = moveSpeed * tpf

        if (position.distance(targetPosition) >= moveDistance) {
            position.translate(targetPosition.value.subtract(position.value).normalize().multiply(moveDistance))
            return false
        }

        nextAction.isInRange = true
        return true
    }

    private fun createIdleState(): FSMState {
        return object : FSMState {
            override fun update(fsm: FSM, entity: Entity) {
                // GOAP planning

                // get the world state and the goal we want to plan for
                val worldState = agent.obtainWorldState(entity)
                val goal = agent.createGoalState(entity)

                // Plan
                val plan = GoapPlanner.plan(entity, availableActions, worldState, goal)
                if (!plan.isEmpty()) {
                    // we have a plan, hooray!
                    currentActions = plan
                    agent.planFound(entity, goal, plan)

                    fsm.popState() // move to PerformAction state
                    fsm.pushState(performActionState)

                } else {
                    // ugh, we couldn't get a plan
                    agent.planFailed(entity, goal)
                    fsm.popState() // move back to IdleAction state
                    fsm.pushState(idleState)
                }
            }
        }
    }

    private fun createMoveToState(): FSMState {
        return object : FSMState {
            override fun update(fsm: FSM, entity: Entity) {

                val action = currentActions.peek()
                if (action.requiresInRange() && action.target == null) {
                    fsm.popState() // move
                    fsm.popState() // perform
                    fsm.pushState(idleState)
                    return
                }

                // get the agent to move itself
                if (moveAgent(action)) {
                    fsm.popState()
                }
            }
        }
    }

    private fun createPerformActionState(): FSMState {
        return object : FSMState {
            override fun update(fsm: FSM, entity: Entity) {
                if (!hasActionPlan()) {
                    // no actions to perform
                    fsm.popState()
                    fsm.pushState(idleState)
                    agent.actionsFinished(entity)
                    return
                }

                var action = currentActions.peek()
                if (action.isDone) {
                    // the action is done. Remove it so we can perform the next one
                    currentActions.remove()
                }

                if (hasActionPlan()) {
                    // perform the next action
                    action = currentActions.peek()
                    val inRange = if (action.requiresInRange()) action.isInRange else true

                    if (inRange) {
                        // we are in range, so perform the action
                        val success = action.perform(entity)

                        if (!success) {
                            // action failed, we need to plan again
                            fsm.popState()
                            fsm.pushState(idleState)
                            agent.planAborted(entity, action)
                        }
                    } else {
                        // we need to move there first
                        // push moveTo state
                        fsm.pushState(moveToState)
                    }

                } else {
                    // no actions left, move to Plan state
                    fsm.popState()
                    fsm.pushState(idleState)
                    agent.actionsFinished(entity)
                }
            }
        }
    }
}
