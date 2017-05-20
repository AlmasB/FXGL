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

package com.almasb.fxgl.ai.fsm;

import com.almasb.fxgl.ai.msg.Telegram;
import com.almasb.fxgl.ai.msg.Telegraph;

/**
 * A state machine manages the state transitions of its entity.
 * Additionally, the state machine may be delegated by the entity to handle its messages.
 *
 * @param <E> the type of the entity owning this state machine
 * @param <S> the type of the states of this state machine
 * @author davebaol
 */
public interface StateMachine<E, S extends State<E>> extends Telegraph {

    /**
     * Updates the state machine.
     * <p>
     * Implementation classes should invoke first the {@code update} method of the global state (if any) then the {@code update}
     * method of the current state.
     * </p>
     */
    void update();

    /**
     * Performs a transition to the specified state.
     *
     * @param newState the state to transition to
     */
    void changeState(S newState);

    /**
     * Changes the state back to the previous state.
     *
     * @return {@code true} in case there was a previous state that we were able to revert to.
     * In case there is no previous state,
     * no state change occurs and {@code false} will be returned.
     */
    boolean revertToPreviousState();

    /**
     * Sets the initial state of this state machine.
     *
     * @param state the initial state.
     */
    void setInitialState(S state);

    /**
     * Sets the global state of this state machine.
     *
     * @param state the global state.
     */
    void setGlobalState(S state);

    /**
     * Returns the current state of this state machine.
     */
    S getCurrentState();

    /**
     * Returns the global state of this state machine.
     * <p>
     * Implementation classes should invoke the {@code update} method of the global state every time the FSM is updated. Also, they
     * should never invoke its {@code enter} and {@code exit} method.
     * </p>
     */
    S getGlobalState();

    /**
     * Returns the last state of this state machine.
     */
    S getPreviousState();

    /**
     * Indicates whether the state machine is in the given state.
     *
     * @param state the state to be compared with the current state
     * @return true if the current state's type is equal to the type of the class passed as a parameter.
     */
    boolean isInState(S state);

    /**
     * Handles received telegrams.
     * <p>
     * Implementation classes should first route the telegram to the current state. If the current state does not deal with the
     * message, it should be routed to the global state.
     * </p>
     *
     * @param telegram the received telegram
     * @return true if telegram has been successfully handled; false otherwise.
     */
    boolean handleMessage(Telegram telegram);
}
