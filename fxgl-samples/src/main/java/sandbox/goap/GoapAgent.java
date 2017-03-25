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

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */

public class GoapAgent {

    private Entity gameObject;

    private FSM stateMachine;

    private FSMState idleState; // finds something to do
    private FSMState moveToState; // moves to a target
    private FSMState performActionState; // performs an action

    private HashSet<GoapAction> availableActions;
    private Queue<GoapAction> currentActions;

    private Goap dataProvider; // this is the implementing class that provides our world data and listens to feedback on planning

    private GoapPlanner planner;

    public GoapAgent(Entity entity, Goap dataProvider, List<GoapAction> actions) {
        gameObject = entity;
        this.dataProvider = dataProvider;

        stateMachine = new FSM();
        availableActions = new HashSet<>(actions);
        currentActions = new ArrayDeque<>();
        planner = new GoapPlanner();
        findDataProvider();
        createIdleState();
        createMoveToState();
        createPerformActionState();
        stateMachine.pushState(idleState);
    }

    public void update() {
        stateMachine.update(gameObject);
    }

    public void addAction(GoapAction a) {
        availableActions.add(a);
    }

//    public GoapAction getAction(Type action) {
//        for (GoapAction g : availableActions) {
//            if (g.GetType().Equals(action))
//                return g;
//        }
//        return null;
//    }

    public void removeAction(GoapAction action) {
        availableActions.remove(action);
    }

    private boolean hasActionPlan() {
        return currentActions.size() > 0;
    }

    private void createIdleState() {
        idleState = (fsm, gameObj) -> {
            // GOAP planning

            // get the world state and the goal we want to plan for
            State worldState = dataProvider.getWorldState();
            State goal = dataProvider.createGoalState();

            // Plan
            Queue<GoapAction> plan = planner.plan(gameObject, availableActions, worldState, goal);
            if (plan != null) {
                // we have a plan, hooray!
                currentActions = plan;
                dataProvider.planFound(goal, plan);

                fsm.popState(); // move to PerformAction state
                fsm.pushState(performActionState);

            } else {
                // ugh, we couldn't get a plan
                //Debug.Log("<color=orange>Failed Plan:</color>" + prettyPrint(goal));
                dataProvider.planFailed(goal);
                fsm.popState(); // move back to IdleAction state
                fsm.pushState(idleState);
            }

        };
    }

    private void createMoveToState() {
        moveToState = (fsm, gameObject) -> {
            // move the game object

            GoapAction action = currentActions.peek();
            if (action.requiresInRange() && action.target == null) {
                //Debug.Log("<color=red>Fatal error:</color> Action requires a target but has none. Planning failed. You did not assign the target in your Action.checkProceduralPrecondition()");
                fsm.popState(); // move
                fsm.popState(); // perform
                fsm.pushState(idleState);
                return;
            }

            // get the agent to move itself
            if (dataProvider.moveAgent(action)) {
                fsm.popState();
            }

			/*MovableComponent movable = (MovableComponent) gameObj.GetComponent(typeof(MovableComponent));
            if (movable == null) {
				Debug.Log("<color=red>Fatal error:</color> Trying to move an Agent that doesn't have a MovableComponent. Please give it one.");
				fsm.popState(); // move
				fsm.popState(); // perform
				fsm.pushState(idleState);
				return;
			}

			float step = movable.moveSpeed * Time.deltaTime;
			gameObj.transform.position = Vector3.MoveTowards(gameObj.transform.position, action.target.transform.position, step);

			if (gameObj.transform.position.Equals(action.target.transform.position) ) {
				// we are at the target location, we are done
				action.setInRange(true);
				fsm.popState();
			}*/
        };
    }

    private void createPerformActionState() {

        performActionState = (fsm, gameObj) -> {
            // perform the action

            if (!hasActionPlan()) {
                // no actions to perform
                //Debug.Log("<color=red>Done actions</color>");
                fsm.popState();
                fsm.pushState(idleState);
                dataProvider.actionsFinished();
                return;
            }

            GoapAction action = currentActions.peek();
            if (action.isDone()) {
                // the action is done. Remove it so we can perform the next one
                currentActions.remove();
            }

            if (hasActionPlan()) {
                // perform the next action
                action = currentActions.peek();
                boolean inRange = action.requiresInRange() ? action.isInRange() : true;

                if (inRange) {
                    // we are in range, so perform the action
                    boolean success = action.perform(gameObj);

                    if (!success) {
                        // action failed, we need to plan again
                        fsm.popState();
                        fsm.pushState(idleState);
                        dataProvider.planAborted(action);
                    }
                } else {
                    // we need to move there first
                    // push moveTo state
                    fsm.pushState(moveToState);
                }

            } else {
                // no actions left, move to Plan state
                fsm.popState();
                fsm.pushState(idleState);
                dataProvider.actionsFinished();
            }

        };
    }

    private void findDataProvider() {

         // TODO
//        foreach(Component comp : gameObject.GetComponents(typeof(Component))) {
//            if (typeof(IGoap).IsAssignableFrom(comp.GetType())) {
//                dataProvider = (IGoap) comp;
//                return;
//            }
//        }
    }

//    private void loadActions() {
//        GoapAction[] actions = gameObject.GetComponents < GoapAction > ();
//        for (GoapAction a : actions) {
//            availableActions.add(a);
//        }
//        //Debug.Log("Found actions: " + prettyPrint(actions));
//    }


















//    public static string prettyPrint(HashSet<KeyValuePair<string, object>> state) {
//        String s = "";
//        foreach(KeyValuePair < string, object > kvp in state) {
//            s += kvp.Key + ":" + kvp.Value.ToString();
//            s += ", ";
//        }
//        return s;
//    }
//
//    public static string prettyPrint(Queue<GoapAction> actions) {
//        String s = "";
//        foreach(GoapAction a in actions) {
//            s += a.GetType().Name;
//            s += "-> ";
//        }
//        s += "GOAL";
//        return s;
//    }
//
//    public static string prettyPrint(GoapAction[] actions) {
//        String s = "";
//        foreach(GoapAction a in actions) {
//            s += a.GetType().Name;
//            s += ", ";
//        }
//        return s;
//    }
//
//    public static string prettyPrint(GoapAction action) {
//        String s = "" + action.GetType().Name;
//        return s;
//    }
}
