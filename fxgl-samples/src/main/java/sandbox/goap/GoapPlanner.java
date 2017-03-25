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

package sandbox.goap;

import com.almasb.fxgl.ecs.Entity;

import java.util.*;

/**
 * Plans what actions can be completed in order to fulfill a goal state.
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class GoapPlanner {

    /**
     * Plan what sequence of actions can fulfill the goal.
     * Returns an empty queue if a plan could not be found,
     * or a list of the actions
     * that must be performed, in order, to fulfill the goal.
     */
    public Queue<GoapAction> plan(Entity agent,
                                  HashSet<GoapAction> availableActions,
                                  State worldState,
                                  State goal) {

        // reset the actions so we can start fresh with them
        availableActions.forEach(a -> a.reset());

        // check what actions can run using their checkProceduralPrecondition
        HashSet<GoapAction> usableActions = new HashSet<>();
        availableActions.forEach(a -> {
            if (a.checkProceduralPrecondition(agent))
                usableActions.add(a);
        });

        // we now have all actions that can run, stored in usableActions

        // build up the tree and record the leaf nodes that provide a solution to the goal
        List<Node> leaves = new ArrayList<>();

        // build graph
        Node start = new Node(null, 0, worldState, null);
        boolean success = buildGraph(start, leaves, usableActions, goal);

        if (!success) {
            return new ArrayDeque<>();
        }

        // get the cheapest leaf
        Node cheapest = leaves.stream()
                .min(Comparator.comparingDouble(n -> n.runningCost))
                .get();


        // get its node and work back through the parents
        List<GoapAction> result = new ArrayList<>();
        Node n = cheapest;
        while (n != null) {
            if (n.action != null) {
                result.add(0, n.action); // insert the action in the front
            }
            n = n.parent;
        }

        // we now have this action list in correct order

        // hooray we have a plan!
        return new ArrayDeque<>(result);
    }

    /**
     * Returns true if at least one solution was found.
     * The possible paths are stored in the leaves list.
     * Each leaf has a 'runningCost' value where the lowest cost will be the best action
     * sequence.
     */
    private boolean buildGraph(Node parent, List<Node> leaves, 
                               HashSet<GoapAction> usableActions,
                               State goal) {
        boolean foundOne = false;

        // go through each action available at this node and see if we can use it here
        for (GoapAction action : usableActions) {

            // if the parent state has the conditions for this action's preconditions, we can use it here
            if (action.getPreconditions().isIn(parent.state)) {

                // apply the action's effects to the parent state
                State currentState = populateState(parent.state, action.getEffects());

                Node node = new Node(parent, parent.runningCost + action.cost, currentState, action);

                if (goal.isIn(currentState)) {
                    // we found a solution!
                    leaves.add(node);
                    foundOne = true;
                } else {
                    // not at a solution yet, so test all the remaining actions and branch out the tree
                    HashSet<GoapAction> subset = actionSubset(usableActions, action);
                    boolean found = buildGraph(node, leaves, subset, goal);
                    if (found)
                        foundOne = true;
                }
            }
        }

        return foundOne;
    }

    /**
     * Create a subset of the actions excluding the removeMe one. Creates a new set.
     */
    private HashSet<GoapAction> actionSubset(HashSet<GoapAction> actions, GoapAction removeMe) {
        HashSet<GoapAction> subset = new HashSet<>();
        for(GoapAction a : actions) {
            if (!a.equals(removeMe))
                subset.add(a);
        }
        return subset;
    }

    /**
     * Apply the stateChange to the currentState.
     */
    private State populateState(State currentState, State stateChange) {
        // copy
        State newState = new State(currentState);
        newState.update(stateChange);

        return newState;
    }

    /**
     * Used for building up the graph and holding the running costs of actions.
     */
    private class Node {
        Node parent;
        float runningCost;
        State state;
        GoapAction action;

        Node(Node parent, float runningCost, State state, GoapAction action) {
            this.parent = parent;
            this.runningCost = runningCost;
            this.state = state;
            this.action = action;
        }
    }
}


