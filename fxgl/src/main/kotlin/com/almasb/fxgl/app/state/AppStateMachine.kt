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

package com.almasb.fxgl.app.state

import com.almasb.fxgl.app.ApplicationState
import com.almasb.fxgl.app.FXGL.Companion.getDisplay
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AppStateMachine {

    var applicationState = ApplicationState.STARTUP
        private set

    var appState: AppState = StartupState
        private set

    private val subStates = ArrayDeque<SubState>()

    /**
     * Can only be called when no substates are present.
     */
    fun setState(appState: ApplicationState) {
        if (subStates.isNotEmpty())
            throw IllegalStateException("Cannot change states with active substates")

        applicationState = appState

        this.appState.onExit()
        this.appState.input().clearAll()

        this.appState = appState.state()

        getDisplay().setScene(this.appState.scene())

        this.appState.onEnter()
    }

    fun pushState(state: SubState) {
        getCurrentState().input().clearAll()

        subStates.push(state)
        getDisplay().getCurrentScene().getRoot().getChildren().add(state.view())

        state.onEnter()
    }

    fun popState() {
        // TODO: check empty?
        val state = subStates.pop()

        state.onExit()

        getDisplay().getCurrentScene().getRoot().getChildren().remove(state.view())
    }

    fun getCurrentState(): State {
        return if (subStates.isEmpty()) appState else subStates.peek()
    }
}