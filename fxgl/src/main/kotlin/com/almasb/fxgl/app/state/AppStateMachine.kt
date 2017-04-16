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
import com.almasb.fxgl.app.FXGL
import com.almasb.fxgl.app.FXGL.Companion.getDisplay
import com.almasb.fxgl.core.logging.FXGLLogger
import com.almasb.fxgl.time.UpdateEvent
import com.almasb.fxgl.time.UpdateEventListener
import java.util.*

/**
 *
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class AppStateMachine : UpdateEventListener {

    private val log = FXGLLogger.get(javaClass)

    var applicationState = ApplicationState.STARTUP
        private set

    var appState: AppState = StartupState
        private set

    private val subStates = ArrayDeque<SubState>()

    fun start() {
        log.debug("Starting AppStateMachine")
        appState.onEnter(appState)

        FXGL.getMasterTimer().startMainLoop()
    }

    /**
     * Can only be called when no substates are present.
     */
    fun setState(newState: ApplicationState) {
        if (subStates.isNotEmpty()) {
            log.warning("Cannot change states with active substates")
            return
           //throw IllegalStateException("Cannot change states with active substates")
        }

        log.debug("$applicationState -> $newState")

        applicationState = newState

        val prevState = this.appState
        prevState.onExit()
        prevState.input().clearAll()

        // new state
        this.appState = newState.state()

        getDisplay().setScene(this.appState.scene())

        this.appState.onEnter(prevState)
    }

    fun pushState(state: SubState) {
        log.debug("Push state: $state")

        val prevState = getCurrentState()

        prevState.input().clearAll()

        subStates.push(state)
        getDisplay().currentScene.root.children.add(state.view())

        state.onEnter(prevState)
    }

    fun popState() {
        if (subStates.isEmpty()) {
            log.warning("Cannot pop state: Substates are empty!")
            return
        }

        val state = subStates.pop()

        log.debug("Pop state: $state")

        state.onExit()

        getDisplay().currentScene.root.children.remove(state.view())
    }

    fun getCurrentState(): State {
        return if (subStates.isEmpty()) appState else subStates.peek()
    }

    override fun onUpdateEvent(event: UpdateEvent) {
        val state = getCurrentState()
        state.input().onUpdateEvent(event)
        state.onUpdate(event.tpf())
    }
}