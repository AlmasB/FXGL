/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.easyio.EasyIO;
import com.almasb.fxeventbus.EventBus;
import com.almasb.fxgl.devtools.profiling.Profiler;
import com.almasb.fxgl.event.*;
import com.almasb.fxgl.gameplay.GameWorld;
import com.almasb.fxgl.gameplay.SaveLoadManager;
import com.almasb.fxgl.io.DataFile;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.logging.SystemLogger;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.scene.*;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.FXGLUncaughtExceptionHandler;
import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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

    {
        log.debug("Starting JavaFX");
        setDefaultUncaughtExceptionHandler(new FXGLUncaughtExceptionHandler());
    }

    /**
     * Set handler for runtime uncaught exceptions.
     *
     * @param handler exception handler
     */
    public final void setDefaultUncaughtExceptionHandler(ExceptionHandler handler) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            pause();
            log.fatal("Uncaught Exception:");
            log.fatal(SystemLogger.INSTANCE.errorTraceAsString(error));
            log.fatal("Application will now exit");
            handler.handle(error);
            exit();
        });
    }

    private ObjectProperty<ApplicationState> state = new SimpleObjectProperty<>(ApplicationState.STARTUP);

    /**
     * @return current application state
     */
    public final ApplicationState getState() {
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
                break;
            case LOADING:
                getDisplay().setScene(loadingScene);
                break;
            case MAIN_MENU:
                getDisplay().setScene(mainMenuScene);
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

    private GameWorld gameWorld;
    private PhysicsWorld physicsWorld;
    private GameScene gameScene;

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

    private SceneFactory sceneFactory;

    /**
     * Override to provide custom intro/loading/menu scenes.
     *
     * @return scene factory
     */
    protected SceneFactory initSceneFactory() {
        return new SceneFactory();
    }

    /**
     * Intro scene, this is shown when the application started,
     * before menus and game.
     */
    private IntroScene introScene;

    /**
     * This scene is shown during app initialization,
     * i.e. when assets / game are loaded on bg thread.
     */
    private LoadingScene loadingScene;

    /**
     * Main menu, this is the menu shown at the start of game.
     */
    private FXGLMenu mainMenuScene;

    /**
     * In-game menu, this is shown when menu key pressed during the game.
     */
    private FXGLMenu gameMenuScene;

    /**
     * Main game profiler.
     */
    private Profiler profiler;

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
    protected abstract void initInput();

    /**
     * This is called after core services are initialized
     * but before any game init. Called only once
     * per application lifetime.
     */
    protected void preInit() {

    }

    /**
     * Initialize game assets, such as Texture, Sound, Music, etc.
     */
    protected abstract void initAssets();

    /**
     * Called when MenuEvent.SAVE occurs.
     * Note: if you enable menus, you are responsible for providing
     * appropriate serialization of your game state, even if it's ad-hoc null.
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
     * appropriate deserialization of your game state, even if it's ad-hoc no-op.
     *
     * @param dataFile previously saved data
     * @throws UnsupportedOperationException if was not overridden
     */
    protected void loadState(DataFile dataFile) {
        log.warning("Called loadState(), but it wasn't overridden!");
        throw new UnsupportedOperationException("Default implementation is not available");
    }

    /**
     * Initialize game objects.
     */
    protected abstract void initGame();

    /**
     * Initialize collision handlers, physics properties.
     */
    protected abstract void initPhysics();

    /**
     * Initialize UI objects.
     */
    protected abstract void initUI();

    /**
     * Main loop update phase, most of game logic.
     *
     * @param tpf time per frame
     */
    protected abstract void onUpdate(double tpf);

    /**
     * Called after main loop tick has been completed.
     * It can be used to de-register callbacks / listeners
     * and call various methods that otherwise might interfere
     * with main loop.
     *
     * @param tpf time per frame (same as main update tpf)
     */
    protected void onPostUpdate(double tpf) {

    }

    private void initGlobalEventHandlers() {
        log.debug("Initializing global event handlers");

        EventBus bus = getEventBus();

        Font fpsFont = Font.font("Lucida Console", 20);

        // Main tick

        getMasterTimer().addUpdateListener(getInput());
        getMasterTimer().addUpdateListener(getAudioPlayer());
        getMasterTimer().addUpdateListener(getGameWorld());
        getMasterTimer().addUpdateListener(event -> {
            onUpdate(event.tpf());

            if (getSettings().isFPSShown()) {
                GraphicsContext g = getGameScene().getGraphicsContext();

                g.setFont(fpsFont);
                g.setFill(Color.RED);
                g.fillText(profiler.getInfo(), 0, getHeight() - 120);
            }
        });

        AnimationTimer postUpdateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onPostUpdate(getMasterTimer().tpf());
            }
        };

        // Save/Load events

        bus.addEventHandler(SaveEvent.ANY, event -> {
            getInput().save(event.getProfile());
            getDisplay().save(event.getProfile());
            getAudioPlayer().save(event.getProfile());
            getAchievementManager().save(event.getProfile());
            getMasterTimer().save(event.getProfile());
        });

        bus.addEventHandler(LoadEvent.ANY, event -> {
            getInput().load(event.getProfile());
            getDisplay().load(event.getProfile());
            getAudioPlayer().load(event.getProfile());
            getAchievementManager().load(event.getProfile());

            if (event.getEventType() != LoadEvent.RESTORE_SETTINGS) {
                getMasterTimer().load(event.getProfile());
            }
        });

        // Core listeners

        addFXGLListener(getInput());
        addFXGLListener(getMasterTimer());
        addFXGLListener(new FXGLListener() {
            @Override
            public void onPause() {
                postUpdateTimer.stop();
                setState(ApplicationState.PAUSED);
            }

            @Override
            public void onResume() {
                postUpdateTimer.start();
                setState(ApplicationState.PLAYING);
            }

            @Override
            public void onReset() {
                getGameWorld().reset();
            }

            @Override
            public void onExit() {
                if (getSettings().isMenuEnabled())
                    menuHandler.saveProfile();
            }
        });

        getGameWorld().addWorldListener(getPhysicsWorld());
        getGameWorld().addWorldListener(getGameScene());

        // Scene

        getGameScene().addEventHandler(MouseEvent.ANY, event ->
                getInput().onMouseEvent(event, getGameScene().getViewport())
        );
        getGameScene().addEventHandler(KeyEvent.ANY, getInput()::onKeyEvent);

        bus.addEventHandler(NotificationEvent.ANY, getAudioPlayer()::onNotificationEvent);

        bus.addEventHandler(AchievementEvent.ANY, getNotificationService()::onAchievementEvent);

        // FXGL App

        bus.addEventHandler(DisplayEvent.CLOSE_REQUEST, e -> exit());
        bus.addEventHandler(DisplayEvent.DIALOG_OPENED, e -> {
            if (getState() == ApplicationState.INTRO ||
                    getState() == ApplicationState.LOADING)
                return;

            if (!isMenuOpen())
                pause();

            getInput().onReset();
        });
        bus.addEventHandler(DisplayEvent.DIALOG_CLOSED, e -> {
            if (getState() == ApplicationState.INTRO ||
                    getState() == ApplicationState.LOADING)
                return;

            if (!isMenuOpen())
                resume();
        });
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

    private boolean canSwitchGameMenu = true;

    private void onMenuKey(boolean pressed) {
        if (!pressed) {
            canSwitchGameMenu = true;
            return;
        }

        if (canSwitchGameMenu) {
            if (getState() == ApplicationState.GAME_MENU) {
                canSwitchGameMenu = false;
                resume();
            } else if (getState() == ApplicationState.PLAYING) {
                canSwitchGameMenu = false;
                pause();
                setState(ApplicationState.GAME_MENU);
            } else {
                log.warning("Menu key pressed in unknown state: " + getState());
            }
        }
    }

    private MenuEventHandler menuHandler;

    /**
     * Creates Main and Game menu scenes.
     * Registers them with the Display service.
     * Adds key binding so that scenes can be switched on menu key press.
     */
    private void configureMenu() {
        menuHandler = new MenuEventHandler(this);

        mainMenuScene = sceneFactory.newMainMenu(this);
        gameMenuScene = sceneFactory.newGameMenu(this);

        mainMenuScene.setListener(menuHandler);
        gameMenuScene.setListener(menuHandler);

        getDisplay().registerScene(mainMenuScene);
        getDisplay().registerScene(gameMenuScene);

        EventHandler<KeyEvent> menuKeyHandler = event -> {
            if (event.getCode() == getSettings().getMenuKey()) {
                onMenuKey(event.getEventType() == KeyEvent.KEY_PRESSED);
            }
        };

        getGameScene().addEventHandler(KeyEvent.ANY, menuKeyHandler);
        gameMenuScene.addEventHandler(KeyEvent.ANY, menuKeyHandler);
    }

    private void configureIntro() {
        introScene = sceneFactory.newIntro();
        introScene.setOnFinished(this::showGame);
        getDisplay().registerScene(introScene);
    }

    /**
     * Called right before the main stage is shown.
     */
    private void onStageShow() {
        if (getSettings().isIntroEnabled()) {
            configureIntro();
            setState(ApplicationState.INTRO);

            introScene.startIntro();
        } else {
            showGame();
        }
    }

    private void showGame() {
        if (getSettings().isMenuEnabled()) {
            configureMenu();
            setState(ApplicationState.MAIN_MENU);

            // we haven't shown the dialog yet so show now
            if (getSettings().isIntroEnabled())
                menuHandler.showProfileDialog();
        } else {
            startNewGame();
        }
    }

    private void initFXGL() {
        initAchievements();

        // we call this early to process user input bindings
        // so we can correctly display them in menus
        // 1. register system actions
        SystemActions.INSTANCE.bind(getInput());

        // 2. register user actions
        initInput();

        // 3. scan for annotated methods and register them too
        getInput().scanForUserActions(this);

        initGlobalEventHandlers();

        preInit();
    }

    @Override
    public final void start(Stage stage) throws Exception {
        super.start(stage);

        long start = System.nanoTime();

        // services are now ready, switch to normal logger
        log = FXGL.getLogger(GameApplication.class);
        log.debug("Starting Game Application");

        EasyIO.INSTANCE.setDefaultExceptionHandler(getDefaultCheckedExceptionHandler());
        EasyIO.INSTANCE.setDefaultExecutor(getExecutor());

        gameWorld = FXGL.getInstance(GameWorld.class);
        physicsWorld = FXGL.getInstance(PhysicsWorld.class);
        gameScene = FXGL.getInstance(GameScene.class);

        sceneFactory = initSceneFactory();

        loadingScene = sceneFactory.newLoadingScene();

        getDisplay().registerScene(loadingScene);
        getDisplay().registerScene(getGameScene());

        initFXGL();

        onStageShow();
        stage.show();

        if (getSettings().isMenuEnabled() && !getSettings().isIntroEnabled())
            menuHandler.showProfileDialog();

        if (getSettings().isProfilingEnabled()) {
            profiler = FXGL.newProfiler();
            profiler.start();

            getEventBus().addEventHandler(FXGLEvent.EXIT, e -> {
                profiler.stop();
                profiler.print();
            });
        }

        SystemLogger.INSTANCE.infof("GameApplication start took: %.3f sec", (System.nanoTime() - start) / 1000000000.0);
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

        log.debug("Starting FXGL Init Thread");
        Thread thread = new Thread(initTask, "FXGL Init Thread");
        thread.start();
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
     * @param dataFile save data to loadTask from
     */
    protected void startLoadedGame(DataFile dataFile) {
        log.debug("Starting loaded game");
        initApp(new InitAppTask(this, dataFile));
    }

    public ReadOnlyStringProperty profileNameProperty() {
        return menuHandler.profileNameProperty();
    }

    public void restoreDefaultSettings() {
        menuHandler.restoreDefaultSettings();
    }

    /**
     * @return save load manager
     * @deprecated access to save load manager should be done through menus, this method will be removed
     */
    @Deprecated
    public SaveLoadManager getSaveLoadManager() {
        return menuHandler.getSaveLoadManager();
    }
}
