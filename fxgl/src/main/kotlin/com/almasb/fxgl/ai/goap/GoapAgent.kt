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

package com.almasb.fxgl.ai.goap

import com.almasb.fxgl.ecs.Entity
import java.util.*

/**
 * Any agent that wants to use GOAP must implement
 * this interface.
 * It provides information to the GOAP
 * planner so it can plan what actions to use.
 *
 * It also provides an interface for the planner to give
 * feedback to the Agent and report success/failure.
 *
 * Adapted from https://github.com/sploreg/goap
 * Original source: C#, author: Brent Anthony Owens.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
interface GoapAgent {

    /**
     * The starting state of the Agent and the world.
     * Supply what states are needed for actions to run.
     */
    fun obtainWorldState(entity: Entity): State

    /**
     * Give the planner a new goal so it can figure out
     * the actions needed to fulfill it.
     */
    fun createGoalState(entity: Entity): State

    /**
     * No sequence of actions could be found for the supplied goal.
     * You will need to try another goal.
     */
    fun planFailed(entity: Entity, failedGoal: State)

    /**
     * A plan was found for the supplied goal.
     * These are the actions the Agent will perform, in order.
     */
    fun planFound(entity: Entity, goal: State, actions: Queue<GoapAction>)

    /**
     * All actions are complete and the goal was reached.
     */
    fun actionsFinished(entity: Entity)

    /**
     * One of the actions caused the plan to abort.
     * That action is returned.
     */
    fun planAborted(entity: Entity, aborter: GoapAction)
}