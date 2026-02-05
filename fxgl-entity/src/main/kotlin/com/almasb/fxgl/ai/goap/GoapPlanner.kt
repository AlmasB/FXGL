/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.core.collection.PropertyMap
import java.util.*

/**
 * Plans what actions can be completed in order to fulfill a goal state.
 *
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
object GoapPlanner {

    /**
     * Plan what sequence of actions can fulfill the goal.
     * Returns an empty queue if a plan could not be found,
     * or a list of the actions that must be performed, in order, to fulfill the goal.
     */
    fun plan(availableActions: Set<GoapAction>,
             currentState: PropertyMap,
             goalState: PropertyMap): Queue<GoapAction> {

        // reset the actions so we can start fresh with them
        // TODO:
        //availableActions.forEach { it.cancel() }

        // check what actions can run
        // TODO:
        //val usableActions = availableActions.filter { it.canRun() }.toSet()
        val usableActions = availableActions.toSet()

        // we now have all actions that can run, stored in usableActions

        // build up the tree and record the leaf nodes that provide a solution to the goal
        val leaves = ArrayList<Node>()

        // build graph
        val start = Node(null, 0f, currentState, null)
        val success = buildGraph(start, leaves, usableActions, goalState)

        if (!success) {
            return ArrayDeque()
        }

        // get the cheapest leaf
        val cheapest = leaves.minBy { it.runningCost }

        // get its node and work back through the parents
        val result = ArrayList<GoapAction>()
        var n: Node? = cheapest
        while (n != null) {
            if (n.action != null) {
                result.add(n.action!!)
            }
            n = n.parent
        }

        // we now have this action list in correct order
        return ArrayDeque(result.reversed())
    }

    /**
     * Returns true if at least one solution was found.
     * The possible paths are stored in the leaves list.
     * Each leaf has a 'runningCost' value where the lowest cost will be the best action sequence.
     */
    private fun buildGraph(parent: Node,
                           leaves: MutableList<Node>,
                           usableActions: Set<GoapAction>,
                           goal: PropertyMap): Boolean {

        var foundOne = false

        // prefer low cost actions over high cost
        val sortedActions = usableActions.sortedBy { it.cost }

        // go through each action available at this node and see if we can use it here
        for (action in sortedActions) {

            // if the parent state has the conditions for this action's preconditions, we can use it here
            if (action.preconditions.isIn(parent.state)) {

                // apply the action's effects to the parent state
                val currentState = populateState(parent.state, action.effects)

                val node = Node(parent, parent.runningCost + action.cost, currentState, action)

                if (goal.isIn(currentState)) {
                    // we found a solution!
                    leaves.add(node)
                    foundOne = true
                } else {
                    // not at a solution yet, so test all the remaining actions and branch out the tree
                    val subset = usableActions - action

                    val found = buildGraph(node, leaves, subset, goal)
                    if (found)
                        foundOne = true
                }
            }
        }

        return foundOne
    }

    /**
     * @return a new state by applying the [stateChange] to the [currentState] (which does not change)
     */
    private fun populateState(currentState: PropertyMap, stateChange: PropertyMap): PropertyMap {
        val newState = currentState.copy()
        newState.addAll(stateChange)
        return newState
    }

    // TODO: currently only supports boolean values
    private fun PropertyMap.isIn(other: PropertyMap): Boolean {
        var result = true

        this.forEach { key, value ->

            // if doesn't exist in other
            if (!other.exists(key)) {
                result = false
                return@forEach
            }

            // or doesn't match the value in other
            if (value != other.getValue(key)) {
                result = false
                return@forEach
            }
        }

        return result
    }

    /**
     * An internal type, used for building up the graph and holding the running costs of actions.
     */
    private class Node(
        var parent: Node?,
        var runningCost: Float,
        var state: PropertyMap,
        var action: GoapAction?
    )
}

