/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.scene

import com.almasb.sslogger.Logger
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections

/**
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
class SceneMachine(start: FXGLScene) {

    private val log = Logger.get(javaClass)

    private val subStates = FXCollections.observableArrayList<SubScene>()

    private val appState = SimpleObjectProperty<FXGLScene>(start)

    val currentState: Scene
        get() = if (subStates.isEmpty()) appState.value else subStates.last()

    fun currentFXGLSceneProperty() = appState
    fun subScenesProperty() = subStates

    /**
     * Can only be called when no substates are present.
     * Can only be called by internal FXGL API.
     */
    internal fun setState(newState: FXGLScene) {
        if (!subStates.isEmpty()) {
            log.warning("Cannot change states with active substates")
            return
        }

        val prevState = appState.value

        prevState.exit()

        // new state
        appState.value = newState
        log.debug("" + prevState + " -> " + newState)

        appState.value.enter(prevState)
    }

    internal fun onUpdate(tpf: Double) {
        currentState.update(tpf)
    }

    fun pushState(newState: SubScene) {
        log.debug("Push state: $newState")

        // substate, so prevState does not exit
        val prevState = currentState
        prevState.input.clearAll()

        log.debug("" + prevState + " -> " + newState)

        // new state
        subStates.add(newState)

        newState.enter(prevState)
    }

    fun popState() {
        if (subStates.isEmpty()) {
            throw IllegalStateException("Cannot pop state: Substates are empty!")
        }

        val prevState = subStates.last()
        log.debug("Pop state: $prevState")

        prevState.exit()

        subStates.removeAt(subStates.size - 1)

        log.debug("" + currentState + " <- " + prevState)
    }


}