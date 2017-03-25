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

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

import java.util.Queue;

/**
 * Any agent that wants to use GOAP must implement
 * this interface.
 * It provides information to the GOAP
 * planner so it can plan what actions to use.
 *
 * It also provides an interface for the planner to give 
 * feedback to the Agent and report success/failure.
 *
 * https://github.com/sploreg/goap
 */
public interface Goap
{
    /**
     * The starting state of the Agent and the world.
     * Supply what states are needed for actions to run.
     */
    State getWorldState();

    /**
     * Give the planner a new goal so it can figure out 
     * the actions needed to fulfill it.
     */
    State createGoalState();

    /**
     * No sequence of actions could be found for the supplied goal.
     * You will need to try another goal
     */
    void planFailed(State failedGoal);

    /**
     * A plan was found for the supplied goal.
     * These are the actions the Agent will perform, in order.
     */
    void planFound(State goal, Queue<GoapAction> actions);

    /**
     * All actions are complete and the goal was reached. Hooray!
     */
    void actionsFinished();

    /**
     * One of the actions caused the plan to abort.
     * That action is returned.
     */
    void planAborted(GoapAction aborter);

    /**
     * Called during update. Move the agent towards the target in order
     * for the next action to be able to perform.
     * Return true if the Agent is at the target and the next action can perform.
     * False if it is not there yet.
     */
    boolean moveAgent(GoapAction nextAction);
}
