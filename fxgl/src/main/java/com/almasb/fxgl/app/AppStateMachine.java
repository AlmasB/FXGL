/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.fxgl.scene.SceneFactory;

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

        SceneFactory sceneFactory = FXGL.getSettings().getSceneFactory();

        // STARTUP is default
        appState = new StartupState(app);

        loading = new LoadingState(app, sceneFactory);
        play = new PlayState(sceneFactory);

        // reasonable hack to trigger dialog state init before intro and menus
        DialogSubState.INSTANCE.getView();

        intro = app.getSettings().isIntroEnabled() ? new IntroState(app, sceneFactory) : null;
        mainMenu = app.getSettings().isMenuEnabled() ? new MainMenuState(sceneFactory) : null;
        gameMenu = app.getSettings().isMenuEnabled() ? new GameMenuState(sceneFactory) : null;
    }

    /**
     * Can only be called when no substates are present.
     * Can only be called by internal FXGL API.
     */
    void setState(AppState newState) {
        if (!subStates.isEmpty()) {
            log.warning("Cannot change states with active substates");
            return;
        }

        AppState prevState = appState;
        prevState.exit();

        log.debug(prevState + " -> " + newState);

        // new state
        appState = newState;
        app.setScene(appState.getScene());
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
        app.getScene().getRoot().getChildren().add(newState.getView());
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

        app.getScene().getRoot().getChildren().remove(prevState.getView());
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
