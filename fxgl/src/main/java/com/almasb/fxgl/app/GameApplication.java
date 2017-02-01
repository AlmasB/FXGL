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

import com.almasb.fxgl.devtools.profiling.Profiler;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.event.IntroFinishedEvent;
import com.almasb.fxgl.eventbus.Subscriber;
import com.almasb.fxgl.gameplay.GameState;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.logging.SystemLogger;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.fxgl.scene.*;
import com.almasb.fxgl.scene.menu.MenuEventListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

import java.util.Map;

/**
 * To use FXGL extend this class and implement necessary methods.
 * The initialization process can be seen below (irrelevant phases are omitted):
 * <p>
 * <ol>
 * <li>Instance fields of YOUR subclass of GameApplication</li>
 * <li>initSettings()</li>
 * <li>Services configuration (after this you can safely call FXGL.getService())</li>
 * <li>initAchievements()</li>
 * <li>initInput()</li>
 * <li>preInit()</li>
 * <p>The following phases are NOT executed on UI thread</p>
 * <li>initAssets()</li>
 * <li>initGame() OR loadState()</li>
 * <li>initPhysics()</li>
 * <li>initUI()</li>
 * <li>Start of main game loop execution on UI thread</li>
 * </ol>
 * <p>
 * Unless explicitly stated, methods are not thread-safe and must be
 * executed on the JavaFX Application (UI) Thread.
 * By default all callbacks are executed on the JavaFX Application (UI) Thread.
 * <p>
 *     Callback / listener notes: instance of GameApplication will always be
 *     notified last along the chain of callbacks.
 *     However, as per documentation, events are always fired after listeners.
 * </p>
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class GameApplication extends FXGLApplication {

    private Logger log = SystemLogger.INSTANCE;

    private ObjectProperty<ApplicationState> state = new SimpleObjectProperty<>(ApplicationState.STARTUP);

    /* The following fields are injected by tasks */

    GameState gameState;

    GameWorld gameWorld;

    PhysicsWorld physicsWorld;

    GameScene gameScene;

    /**
     * Intro scene, this is shown when the application started,
     * before menus and game.
     */
    IntroScene introScene;

    /**
     * This scene is shown during app initialization,
     * i.e. when assets / game are loaded on bg thread.
     */
    LoadingScene loadingScene;

    /**
     * Main menu, this is the menu shown at the start of game.
     */
    FXGLMenu mainMenuScene;

    /**
     * In-game menu, this is shown when menu key pressed during the game.
     */
    FXGLMenu gameMenuScene;

    /**
     * Handler for menu events.
     */
    private MenuEventHandler menuHandler;

    /**
     * Main game profiler.
     */
    Profiler profiler;

    /**
     * The app constructor is called automatically by the JavaFX platform.
     */
    public GameApplication() {
        log.debug("Starting JavaFX");
    }

    /**
     * @return current application state
     */
    ApplicationState getState() {
        return state.get();
    }

    /**
     * Set application state.
     * Setting a state will also trigger scene change associated
     * with that state.
     *
     * @param appState application state
     */
    void setState(ApplicationState appState) {
        log.debug("State: " + getState() + " -> " + appState);
        state.set(appState);
        switch (appState) {
            case INTRO:
                getDisplay().setScene(introScene);

                introFinishedSubscriber = getEventBus().addEventHandler(IntroFinishedEvent.ANY, e -> onIntroFinished());
                introScene.startIntro();
                break;

            case LOADING:
                getDisplay().setScene(loadingScene);
                break;

            case MAIN_MENU:
                getDisplay().setScene(mainMenuScene);
                if (!menuHandler.isProfileSelected())
                    menuHandler.showProfileDialog();
                break;

            case GAME_MENU:
                getDisplay().setScene(gameMenuScene);
                break;

            case PLAYING:
                getDisplay().setScene(getGameScene());
                break;

            case PAUSED:
                // no need to do anything
                break;

            default:
                log.warning("Attempted to set illegal state: " + appState);
                break;
        }
    }

    /**
     * @return game state
     */
    public final GameState getGameState() {
        return gameState;
    }

    /**
     * @return game world
     */
    public final GameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     * @return physics world
     */
    public final PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    /**
     * @return game scene
     */
    public final GameScene getGameScene() {
        return gameScene;
    }

    /**
     * Override to provide custom intro/loading/menu scenes.
     *
     * @return scene factory
     */
    protected SceneFactory initSceneFactory() {
        return new SceneFactory();
    }

    /**
     * Override to register your achievements.
     *
     * <pre>
     * Example:
     *
     * AchievementManager am = getAchievementManager();
     * am.registerAchievement(new Achievement("Score Master", "Score 20000 points"));
     * </pre>
     */
    protected void initAchievements() {
        // no default implementation
    }

    /**
     * Initialize input, i.e. bind key presses, bind mouse buttons.
     *
     * <p>
     * Note: This method is called prior to any game init to
     * register input mappings in the menus.
     * </p>
     * <pre>
     * Example:
     *
     * Input input = getInput();
     * input.addAction(new UserAction("Move Left") {
     *      protected void onAction() {
     *          playerControl.moveLeft();
     *      }
     * }, KeyCode.A);
     * </pre>
     */
    protected void initInput() {
        // no default implementation
    }

    /**
     * This is called after core services are initialized
     * but before any game init.
     * Called only once per application lifetime.
     */
    protected void preInit() {
        // no default implementation
    }

    /**
     * Initialize game assets, such as Texture, Sound, Music, etc.
     */
    protected void initAssets() {
        // no default implementation
    }

    /**
     * Called when MenuEvent.SAVE occurs.
     * Note: if you enable menus, you are responsible for providing
     * appropriate serialization of your game state.
     * Otherwise an exception will be thrown when save is called.
     *
     * @return data with required info about current state
     * @throws UnsupportedOperationException if was not overridden
     */
    protected DataFile saveState() {
        log.warning("Called saveState(), but it wasn't overridden!");
        throw new UnsupportedOperationException("Default implementation is not available");
    }

    /**
     * Called when MenuEvent.LOAD occurs.
     * Note: if you enable menus, you are responsible for providing
     * appropriate deserialization of your game state.
     * Otherwise an exception will be thrown when load is called.
     *
     * @param dataFile previously saved data
     * @throws UnsupportedOperationException if was not overridden
     */
    protected void loadState(DataFile dataFile) {
        log.warning("Called loadState(), but it wasn't overridden!");
        throw new UnsupportedOperationException("Default implementation is not available");
    }

    /**
     * Can be overridden to provide global variables.
     *
     * @param vars map containing CVars (global variables)
     */
    protected void initGameVars(Map<String, Object> vars) {
        // no default implementation
    }

    /**
     * Initialize game objects.
     */
    protected void initGame() {
        // no default implementation
    }

    /**
     * Initialize collision handlers, physics properties.
     */
    protected void initPhysics() {
        // no default implementation
    }

    /**
     * Initialize UI objects.
     */
    protected void initUI() {
        // no default implementation
    }

    /**
     * Main loop update phase, most of game logic.
     *
     * @param tpf time per frame
     */
    protected void onUpdate(double tpf) {
        // no default implementation
    }

    /**
     * Called after main loop tick has been completed.
     * It can be used to de-register callbacks / listeners
     * and call various methods that otherwise might interfere
     * with main loop.
     *
     * @param tpf time per frame (same as main update tpf)
     */
    protected void onPostUpdate(double tpf) {
        // no default implementation
    }

    /**
     * @return true if any menu is open
     */
    public boolean isMenuOpen() {
        return getState() == ApplicationState.GAME_MENU
                || getState() == ApplicationState.MAIN_MENU;
    }

    /**
     * @return true if game is paused or menu is open
     */
    public boolean isPaused() {
        return isMenuOpen() || getState() == ApplicationState.PAUSED;
    }

    private Subscriber introFinishedSubscriber;

    private void onIntroFinished() {
        if (getSettings().isMenuEnabled()) {
            setState(ApplicationState.MAIN_MENU);
        } else {
            startNewGame();
        }

        if (introFinishedSubscriber != null) {
            introFinishedSubscriber.unsubscribe();
            // we no longer need intro, mark for cleanup
            introScene = null;
            introFinishedSubscriber = null;
        }
    }

    @Override
    void configureApp() {

        // services are now ready, switch to normal logger
        log = FXGL.getLogger(GameApplication.class);
        log.debug("Configuring GameApplication");

        long start = System.nanoTime();

        runTask(PreInitTask.class);
        runTask(InitScenesTask.class);
        runTask(InitEventHandlersTask.class);
        runTask(InitProfilerTask.class);

        // intro runs async so we have to wait with a callback
        // Stage -> (Intro) -> (Menu) -> Game
        // if not enabled, call finished directly
        if (getSettings().isIntroEnabled()) {
            setState(ApplicationState.INTRO);
        } else {
            onIntroFinished();
        }

        SystemLogger.INSTANCE.infof("Game configuration took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);
    }

    /**
     * Initialize user application.
     */
    private void initApp(Task<?> initTask) {
        log.debug("Initializing App");

        // on first run this is no-op, as for rest this ensures
        // that even without menus and during direct calls to start*Game()
        // the system is clean, also reset performs System.gc() to clear stuff we used in init
        pause();
        reset();
        setState(ApplicationState.LOADING);

        loadingScene.bind(initTask);

        getExecutor().execute(initTask);
    }

    /**
     * (Re-)initializes the user application as new and starts the game.
     * Note: cannot be called during callbacks.
     */
    protected void startNewGame() {
        log.debug("Starting new game");
        initApp(new InitAppTask(this));
    }

    /**
     * (Re-)initializes the user application from the given data file and starts the game.
     * Note: cannot be called during callbacks.
     *
     * @param dataFile save data to load from
     */
    protected void startLoadedGame(DataFile dataFile) {
        log.debug("Starting loaded game");
        initApp(new InitAppTask(this, dataFile));
    }

    /**
     * @return menu event handler associated with this game
     * @throws IllegalStateException if menus are not enabled
     */
    public MenuEventListener getMenuListener() {
        if (!getSettings().isMenuEnabled())
            throw new IllegalStateException("Menus are not enabled");

        if (menuHandler == null)
            menuHandler = new MenuEventHandler(this);
        return menuHandler;
    }
}
