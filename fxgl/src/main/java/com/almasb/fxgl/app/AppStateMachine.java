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

    private static final Logger log = FXGLLogger.get(AppStateMachine.class);

    private final AppState loading;
    private final AppState play;

    // These 3 states are optional
    private final AppState intro;
    private final AppState mainMenu;
    private final AppState gameMenu;

    private AppState appState;

    private Deque<SubState> subStates = new ArrayDeque<>();

    private GameApplication app;

    AppStateMachine(GameApplication app) {
        this.app = app;

        log.debug("Initializing application states");

        // STARTUP is default
        appState = FXGL.getInstance(StartupState.class);

        loading = FXGL.getInstance(LoadingState.class);
        play = FXGL.getInstance(PlayState.class);

        // fxglTODO: init dialog sub state here?
        DialogSubState.INSTANCE.getView();

        intro = app.getSettings().isIntroEnabled() ? FXGL.getInstance(IntroState.class) : null;
        mainMenu = app.getSettings().isMenuEnabled() ? FXGL.getInstance(MainMenuState.class) : null;
        gameMenu = app.getSettings().isMenuEnabled() ? FXGL.getInstance(GameMenuState.class) : null;
    }

    /**
     * Can only be called when no substates are present.
     * Can only be called by internal FXGL API.
     */
    void setState(AppState newState) {
        if (!subStates.isEmpty()) {
            log.warning("Cannot change states with active substates");
            return;
            //throw IllegalStateException("Cannot change states with active substates")
        }

        AppState prevState = appState;
        prevState.exit();

        log.debug(prevState + " -> " + newState);

        // new state
        appState = newState;
        app.getDisplay().setScene(appState.getScene());
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

        // new state
        subStates.push(newState);
        app.getDisplay().getCurrentScene().getRoot().getChildren().add(newState.getView());
        newState.enter(prevState);
    }

    public void popState() {
        if (subStates.isEmpty()) {
            log.warning("Cannot pop state: Substates are empty!");
            return;
        }

        SubState prevState = subStates.pop();

        log.debug("Pop state: " + prevState);

        prevState.exit();

        log.debug(getCurrentState() + " <- " + prevState);

        app.getDisplay().getCurrentScene().getRoot().getChildren().remove(prevState.getView());
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
        return DialogSubState.INSTANCE;
    }

    void startIntro() {
        setState(intro);
    }

    void startLoad(DataFile dataFile) {
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

    /**
     * @return true if can show close dialog
     */
    public boolean canShowCloseDialog() {
        // do not allow close dialog if
        // 1. a dialog is shown
        // 2. we are loading a game
        // 3. we are showing intro
        return getCurrentState() != DialogSubState.INSTANCE
                && getCurrentState() != getLoadingState()
                && (!FXGL.getApp().getSettings().isIntroEnabled() || getCurrentState() != getIntroState());
    }
}
