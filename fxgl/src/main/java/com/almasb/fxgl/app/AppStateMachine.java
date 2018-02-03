/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.core.collection.Array;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.saving.DataFile;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Initializes application states.
 * Manages transitions, updates of all states.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class AppStateMachine {

    private static final Logger log = Logger.get(AppStateMachine.class);

    private Array<StateChangeListener> listeners = new Array<>();

    private final AppState loading;
    private final AppState play;
    private final State dialog;

    // These 3 states are optional
    private final AppState intro;
    private final AppState mainMenu;
    private final AppState gameMenu;

    private AppState appState;

    private Deque<SubState> subStates = new ArrayDeque<>();

    AppStateMachine(AppState loading,
                    AppState play,
                    State dialog,
                    AppState intro,
                    AppState mainMenu,
                    AppState gameMenu,
                    AppState initial) {

        this.loading = loading;
        this.play = play;
        this.dialog = dialog;
        this.intro = intro;
        this.mainMenu = mainMenu;
        this.gameMenu = gameMenu;

        this.appState = initial;
    }

    public void addListener(StateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(StateChangeListener listener) {
        listeners.removeValueByIdentity(listener);
    }

    /**
     * Can only be called when no substates are present.
     * Can only be called by internal FXGL API.
     */
    private void setState(AppState newState) {
        if (!subStates.isEmpty()) {
            log.warning("Cannot change states with active substates");
            return;
        }

        AppState prevState = appState;

        prevState.exit();

        for (StateChangeListener listener : listeners)
            listener.beforeExit(prevState);

        for (StateChangeListener listener : listeners)
            listener.beforeEnter(newState);

        // new state
        appState = newState;
        log.debug(prevState + " -> " + newState);

        for (StateChangeListener listener : listeners)
            listener.exited(prevState);

        for (StateChangeListener listener : listeners)
            listener.entered(newState);

        appState.enter(prevState);
    }

    void onUpdate(double tpf) {
        getCurrentState().update(tpf);
    }

    public void pushState(SubState newState) {
        log.debug("Push state: " + newState);

        // substate, so prevState does not exit
        State prevState = getCurrentState();
        prevState.getInput().clearAll();

        log.debug(prevState + " -> " + newState);

        for (StateChangeListener listener : listeners)
            listener.beforeEnter(newState);

        // new state
        subStates.push(newState);

        for (StateChangeListener listener : listeners)
            listener.entered(newState);

        newState.enter(prevState);
    }

    public void popState() {
        if (subStates.isEmpty()) {
            throw new IllegalStateException("Cannot pop state: Substates are empty!");
        }

        SubState prevState = subStates.getFirst();
        log.debug("Pop state: " + prevState);

        prevState.exit();

        for (StateChangeListener listener : listeners)
            listener.beforeExit(prevState);

        subStates.pop();

        for (StateChangeListener listener : listeners)
            listener.exited(prevState);

        log.debug(getCurrentState() + " <- " + prevState);
    }

    public State getCurrentState() {
        return (subStates.isEmpty()) ? appState : subStates.peek();
    }

    public State getIntroState() {
        if (intro == null)
            throw new IllegalStateException("Intro is not enabled");

        return intro;
    }

    public State getLoadingState() {
        return loading;
    }

    public State getMainMenuState() {
        if (mainMenu == null)
            throw new IllegalStateException("Menu is not enabled");

        return mainMenu;
    }

    public State getGameMenuState() {
        if (gameMenu == null)
            throw new IllegalStateException("Menu is not enabled");

        return gameMenu;
    }

    public State getPlayState() {
        return play;
    }

    public State getDialogState() {
        return dialog;
    }

    void startIntro() {
        setState(intro);
    }

    void startLoad(DataFile dataFile) {
        // TODO: this needs to move, state machine shouldn't care about data files
        // or know about concrete states
        if (loading instanceof LoadingState)
            ((LoadingState) loading).setDataFile(dataFile);

        setState(loading);
    }

    void startGameMenu() {
        setState(gameMenu);
    }

    void startMainMenu() {
        setState(mainMenu);
    }

    /**
     * Set state to PLAYING.
     */
    void startPlay() {
        setState(play);
    }

    /**
     * @return true if app is in play state
     */
    public boolean isInPlay() {
        return getCurrentState() == getPlayState();
    }

    public boolean isInGameMenu() {
        return getCurrentState() == getGameMenuState();
    }
}
