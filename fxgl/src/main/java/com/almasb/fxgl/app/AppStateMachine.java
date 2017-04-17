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

package com.almasb.fxgl.app;

import com.almasb.fxgl.core.logging.FXGLLogger;
import com.almasb.fxgl.core.logging.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AppStateMachine {

    private static final Logger log = FXGLLogger.get(AppStateMachine.class);

    private ApplicationState applicationState = ApplicationState.STARTUP;

    ApplicationState getApplicationState() {
        return applicationState;
    }

    private AppState appState = applicationState.state();

    AppState getAppState() {
        return appState;
    }

    private Deque<SubState> subStates = new ArrayDeque<>();

    /**
     * Can only be called when no substates are present.
     */
    void setState(ApplicationState newState) {
        if (!subStates.isEmpty()) {
            log.warning("Cannot change states with active substates");
            return;
            //throw IllegalStateException("Cannot change states with active substates")
        }

        log.debug(applicationState + " -> " + newState);

        applicationState = newState;

        AppState prevState = appState;
        prevState.onExit();
        prevState.getInput().clearAll();

        // new state
        appState = newState.state();

        FXGL.getDisplay().setScene(appState.getScene());

        this.appState.onEnter(prevState);
    }

    public void onUpdate(double tpf) {
        State state = getCurrentState();
        state.getInput().onUpdate(tpf);
        state.getTimer().onUpdate(tpf);
        state.onUpdate(tpf);
    }

    void pushState(SubState state) {
        log.debug("Push state: " + state);

        // substate, so prevState does not exit
        State prevState = getCurrentState();
        prevState.getInput().clearAll();

        subStates.push(state);
        FXGL.getDisplay().getCurrentScene().getRoot().getChildren().add(state.getView());

        state.onEnter(prevState);
    }

    void popState() {
        if (subStates.isEmpty()) {
            log.warning("Cannot pop state: Substates are empty!");
            return;
        }

        SubState state = subStates.pop();

        log.debug("Pop state: " + state);

        state.onExit();
        state.getInput().clearAll();

        FXGL.getDisplay().getCurrentScene().getRoot().getChildren().remove(state.getView());
    }

    State getCurrentState() {
        return (subStates.isEmpty()) ? appState : subStates.peek();
    }
}
