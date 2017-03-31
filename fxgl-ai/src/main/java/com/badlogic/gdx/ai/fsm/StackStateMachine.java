/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.ai.fsm;

import com.almasb.fxgl.core.collection.Array;

/**
 * A {@link StateMachine} implementation that keeps track of all previous {@link State}s via a stack. This makes sense for example
 * in case of a hierarchical menu structure where each menu screen is one state and one wants to navigate back to the main menu
 * anytime, via {@link #revertToPreviousState()}.
 *
 * @param <E> the type of the entity owning this state machine
 * @param <S> the type of the states of this state machine
 * @author Daniel Holderbaum
 */
public class StackStateMachine<E, S extends State<E>> extends DefaultStateMachine<E, S> {

    private Array<S> stateStack;

    /**
     * Creates a {@code StackStateMachine} with no owner, initial state and global state.
     */
    public StackStateMachine() {
        this(null, null, null);
    }

    /**
     * Creates a {@code StackStateMachine} for the specified owner.
     *
     * @param owner the owner of the state machine
     */
    public StackStateMachine(E owner) {
        this(owner, null, null);
    }

    /**
     * Creates a {@code StackStateMachine} for the specified owner and initial state.
     *
     * @param owner        the owner of the state machine
     * @param initialState the initial state
     */
    public StackStateMachine(E owner, S initialState) {
        this(owner, initialState, null);
    }

    /**
     * Creates a {@code StackStateMachine} for the specified owner, initial state and global state.
     *
     * @param owner        the owner of the state machine
     * @param initialState the initial state
     * @param globalState  the global state
     */
    public StackStateMachine(E owner, S initialState, S globalState) {
        super(owner, initialState, globalState);
    }

    @Override
    public void setInitialState(S state) {
        if (stateStack == null) {
            stateStack = new Array<S>();
        }

        this.stateStack.clear();
        this.currentState = state;
    }

    @Override
    public S getCurrentState() {
        return currentState;
    }

    /**
     * Returns the last state of this state machine. That is the high-most state on the internal stack of previous states.
     */
    @Override
    public S getPreviousState() {
        if (stateStack.size() == 0) {
            return null;
        } else {
            return stateStack.last();
        }
    }

    @Override
    public void changeState(S newState) {
        changeState(newState, true);
    }

    /**
     * Changes the Change state back to the previous state. That is the high-most state on the internal stack of previous states.
     *
     * @return {@code True} in case there was a previous state that we were able to revert to. In case there is no previous state,
     * no state change occurs and {@code false} will be returned.
     */
    @Override
    public boolean revertToPreviousState() {
        if (stateStack.size() == 0) {
            return false;
        }

        S previousState = stateStack.pop();
        changeState(previousState, false);
        return true;
    }

    private void changeState(S newState, boolean pushCurrentStateToStack) {
        if (pushCurrentStateToStack && currentState != null) {
            stateStack.add(currentState);
        }

        // Call the exit method of the existing state
        if (currentState != null) currentState.exit(owner);

        // Change state to the new state
        currentState = newState;

        // Call the entry method of the new state
        currentState.enter(owner);
    }

}
