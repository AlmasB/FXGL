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

import com.almasb.ents.Entity;
import com.almasb.ents.EntityWorldListener;
import com.almasb.fxeventbus.EventBus;
import com.almasb.fxgl.asset.FXGLAssets;
import com.almasb.fxgl.event.*;
import com.almasb.fxgl.gameplay.GameWorld;
import com.almasb.fxgl.gameplay.SaveLoadManager;
import com.almasb.fxgl.input.FXGLInputEvent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.io.IOResult;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.logging.SystemLogger;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.scene.*;
import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.settings.UserProfileSavable;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.FXGLUncaughtExceptionHandler;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * To use FXGL extend this class and implement necessary methods.
 * The initialization process can be seen below (irrelevant phases are omitted):
 * <p>
 * <ol>
 * <li>Instance fields of YOUR subclass of GameApplication</li>
 * <li>initSettings()</li>
 * <li>Services configuration (after this you can safely call getService())</li>
 * <li>initAchievements()</li>
 * <li>initInput()</li>
 * <li>preInit()</li>
 * <li>initIntroVideo() (if enabled)</li>
 * <li>initMenuFactory() (if enabled)</li>
 * <p>The following phases are NOT executed on UI thread</p>
 * <li>initAssets()</li>
 * <li>initGame() OR loadState()</li>
 * <li>initPhysics()</li>
 * <li>initUI()</li>
 * <li>Start of main game loop execution on UI thread</li>
 * </ol>
 * <p>
 * Unless explicitly stated, methods are not thread-safe and must be
 * executed on JavaFX Application (UI) Thread.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class GameApplication extends FXGLApplication implements UserProfileSavable {

    private static Logger log = SystemLogger.INSTANCE;

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

    private void setState(ApplicationState appState) {
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

    /**
     * @return game world
     */
    public final GameWorld getGameWorld() {
        return FXGL.getGame().getGameWorld();
    }

    /**
     * @return physics world
     */
    public final PhysicsWorld getPhysicsWorld() {
        return FXGL.getGame().getPhysicsWorld();
    }

    /**
     * @return game scene
     */
    public final GameScene getGameScene() {
        return FXGL.getGame().getGameScene();
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
    private FXGLScene mainMenuScene;

    /**
     * In-game menu, this is shown when menu key pressed during the game.
     */
    private FXGLScene gameMenuScene;

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
    public Serializable saveState() {
        log.warning("Called saveState(), but it wasn't overridden!");
        throw new UnsupportedOperationException("Default implementation is not available");
    }

    /**
     * Called when MenuEvent.LOAD occurs.
     * Note: if you enable menus, you are responsible for providing
     * appropriate deserialization of your game state, even if it's ad-hoc no-op.
     *
     * @param data previously saved data
     * @throws UnsupportedOperationException if was not overridden
     */
    public void loadState(Serializable data) {
        log.warning("Called loadState(), but it wasn't overriden!");
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

    private void initFPSOverlay() {
        if (getSettings().isFPSShown()) {
            Text fpsText = UIFactory.newText("", 24);
            fpsText.setTranslateY(getSettings().getHeight() - 40);
            fpsText.textProperty().bind(getMasterTimer().fpsProperty().asString("FPS: [%d]\n")
                    .concat(getMasterTimer().performanceFPSProperty().asString("Performance: [%d]")));
            getGameScene().addUINode(fpsText);
        }
    }

    /**
     * Main loop update phase, most of game logic.
     *
     * @param tpf time per frame
     */
    protected abstract void onUpdate(double tpf);

    private void initEventHandlers() {
        log.debug("Initializing global event handlers");

        EventBus bus = getEventBus();

        getMasterTimer().setUpdateListener(event -> {
            getInput().onUpdateEvent(event);
            getAudioPlayer().onUpdateEvent(event);

            getGameWorld().onUpdateEvent(event);
            getPhysicsWorld().onUpdateEvent(event);

            onUpdate(event.tpf());

            // notify rest
            bus.fireEvent(event);
        });

        // Input

        bus.addEventHandler(FXGLInputEvent.ANY, event -> {
            getInput().onInputEvent(event);
        });

        // Save/Load events

        bus.addEventHandler(SaveEvent.ANY, event -> {
            getInput().save(event.getProfile());
            getDisplay().save(event.getProfile());
            getAudioPlayer().save(event.getProfile());
            getAchievementManager().save(event.getProfile());
        });

        bus.addEventHandler(LoadEvent.ANY, event -> {
            getInput().load(event.getProfile());
            getDisplay().load(event.getProfile());
            getAudioPlayer().load(event.getProfile());
            getAchievementManager().load(event.getProfile());
        });


        // World

        bus.addEventHandler(FXGLEvent.RESET, event -> {
            getInput().clearAll();
            getGameWorld().reset();
            getMasterTimer().reset();
        });

        bus.addEventHandler(FXGLEvent.RESUME, event -> {
            getInput().clearAll();
            getMasterTimer().start();
        });

        bus.addEventHandler(FXGLEvent.PAUSE, event -> {
            getInput().clearAll();
            getMasterTimer().stop();
        });

        getGameWorld().addWorldListener(getPhysicsWorld());
        getGameWorld().addWorldListener(getGameScene());

        // we need to add this listener
        // to publish entity events via our event bus
        getGameWorld().addWorldListener(new EntityWorldListener() {
            @Override
            public void onEntityAdded(Entity entity) {
                bus.fireEvent(WorldEvent.entityAdded(entity));
            }

            @Override
            public void onEntityRemoved(Entity entity) {
                bus.fireEvent(WorldEvent.entityRemoved(entity));
            }
        });

        // Scene

        getGameScene().addEventHandler(MouseEvent.ANY, event -> {
            FXGLInputEvent e = new FXGLInputEvent(event,
                    getGameScene().screenToGame(new Point2D(event.getSceneX(), event.getSceneY())));
            bus.fireEvent(e);
        });
        getGameScene().addEventHandler(KeyEvent.ANY, event -> {
            bus.fireEvent(new FXGLInputEvent(event, Point2D.ZERO));
        });

        // Audio
        bus.addEventHandler(NotificationEvent.ANY, event -> {
            getAudioPlayer().playSound(FXGLAssets.SOUND_NOTIFICATION);
        });

        bus.addEventHandler(AchievementEvent.ACHIEVED, event -> {
            getNotificationService().pushNotification("You got an achievement! " + event.getAchievement().getName());
        });

        bus.addEventHandler(AchievementProgressEvent.PROGRESS, event -> {
            getNotificationService().pushNotification("Achievement " + event.getAchievement().getName() + "\n"
                    + "Progress: " + event.getValue() + "/" + event.getMax());
        });

        // FXGL App

        bus.addEventHandler(DisplayEvent.CLOSE_REQUEST, e -> exit());
        bus.addEventHandler(DisplayEvent.DIALOG_OPENED, e -> {
            if (getState() == ApplicationState.INTRO ||
                    getState() == ApplicationState.LOADING)
                return;

            if (!isMenuOpen())
                pause();

            getInput().clearAll();
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

    /**
     * Creates Main and Game menu scenes.
     * Registers them with the Display service.
     * Adds key binding so that scenes can be switched on menu key press.
     */
    private void initMenuScenes() {
        mainMenuScene = sceneFactory.newMainMenu(this);
        gameMenuScene = sceneFactory.newGameMenu(this);

        getDisplay().registerScene(mainMenuScene);
        getDisplay().registerScene(gameMenuScene);

        getGameScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == getSettings().getMenuKey()
                    && canSwitchGameMenu) {
                getEventBus().fireEvent(new MenuEvent(MenuEvent.PAUSE));
                canSwitchGameMenu = false;
            }
        });

        gameMenuScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == getSettings().getMenuKey()
                    && canSwitchGameMenu) {
                getEventBus().fireEvent(new MenuEvent(MenuEvent.RESUME));
                canSwitchGameMenu = false;
            }
        });

        getGameScene().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == getSettings().getMenuKey())
                canSwitchGameMenu = true;
        });
        gameMenuScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == getSettings().getMenuKey())
                canSwitchGameMenu = true;
        });
    }

    /**
     * Registers menu event handlers, so that we can perform
     * some actions when menu events occur.
     */
    private void initMenuEventHandlers() {
        getEventBus().addEventHandler(MenuEvent.NEW_GAME, event -> startNewGame());
        getEventBus().addEventHandler(MenuEvent.EXIT, event -> exit());
        getEventBus().addEventHandler(MenuEvent.EXIT_TO_MAIN_MENU, event -> {
            pause();
            reset();
            setState(ApplicationState.MAIN_MENU);
        });

        getEventBus().addEventHandler(MenuEvent.PAUSE, event -> {
            pause();
            setState(ApplicationState.GAME_MENU);
        });
        getEventBus().addEventHandler(MenuEvent.RESUME, event -> {
            resume();
        });

        getEventBus().addEventHandler(MenuEvent.SAVE, event -> {
            getDisplay().showInputBox("Enter save file name", DialogPane.ALPHANUM, saveFileName -> {
                IOResult io = saveLoadManager.save(saveState(), saveFileName);

                if (!io.isOK())
                    getDisplay().showMessageBox("Failed to save:\n" + io.getErrorMessage());
            });
        });

        getEventBus().addEventHandler(MenuDataEvent.LOAD, event -> {
            String saveFileName = event.getData();

            IOResult<Serializable> io = saveLoadManager.load(saveFileName);

            if (io.hasData()) {
                startLoadedGame(io.getData());
            } else {
                getDisplay().showMessageBox("Failed to load:\n" + io.getErrorMessage());
            }
        });

        getEventBus().addEventHandler(MenuEvent.CONTINUE, event -> {
            IOResult<Serializable> io = saveLoadManager.loadLastModifiedSaveFile();

            if (io.hasData()) {
                startLoadedGame(io.getData());
            } else {
                getDisplay().showMessageBox("Failed to load:\n" + io.getErrorMessage());
            }
        });

        getEventBus().addEventHandler(MenuDataEvent.DELETE, event -> {
            String fileName = event.getData();

            boolean ok = saveLoadManager.delete(fileName);
            if (!ok) {
                getDisplay().showMessageBox("Failed to delete:\n" + fileName);
            }
        });
    }

    private void configureMenu() {
        initMenuScenes();
        initMenuEventHandlers();
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
                showProfileDialog();
        } else {
            startNewGame();
        }
    }

    /**
     * Show profile dialog so that user selects existing or creates new profile.
     * The dialog is only dismissed when profile is chosen either way.
     */
    private void showProfileDialog() {
        IOResult<List<String> > io = SaveLoadManager.loadProfileNames();

        List<String> profileNames;

        if (io.hasData()) {
            profileNames = io.getData();
        } else {
            log.warning(io::getErrorMessage);
            profileNames = Collections.emptyList();
        }

        ChoiceBox<String> profilesBox = UIFactory.newChoiceBox(FXCollections.observableArrayList(profileNames));

        if (!profileNames.isEmpty())
            profilesBox.getSelectionModel().selectFirst();

        Button btnNew = UIFactory.newButton("NEW");
        Button btnSelect = UIFactory.newButton("SELECT");
        btnSelect.disableProperty().bind(profilesBox.valueProperty().isNull());

        btnNew.setOnAction(e -> {
            getDisplay().showInputBox("New Profile", DialogPane.ALPHANUM, name -> {
                profileName = name;
                saveLoadManager = new SaveLoadManager(profileName);
                getEventBus().fireEvent(new MenuDataEvent(MenuDataEvent.PROFILE_SELECTED, profileName));
            });
        });

        btnSelect.setOnAction(e -> {
            profileName = profilesBox.getValue();
            saveLoadManager = new SaveLoadManager(profileName);

            boolean ok = false;

            IOResult<UserProfile> result = saveLoadManager.loadProfile();
            if (result.hasData()) {
                ok = loadFromProfile(result.getData());
            }

            if (!ok) {
                getDisplay().showErrorBox("Profile is corrupted: " + profileName, this::showProfileDialog);
            } else {
                getEventBus().fireEvent(new MenuDataEvent(MenuDataEvent.PROFILE_SELECTED, profileName));
            }
        });

        getDisplay().showBox("Create new profile or select existing", profilesBox, btnNew, btnSelect);
    }

    private void bindScreenshotKey() {
        getInput().addAction(new UserAction("Screenshot") {
            @Override
            protected void onActionBegin() {
                boolean ok = getDisplay().saveScreenshot();

                getNotificationService().pushNotification(ok
                        ? "Screenshot saved" : "Screenshot failed");
            }
        }, KeyCode.P);
    }

    private void initFXGL() {
        initAchievements();
        // we call this early to process user input bindings
        // so we can correctly display them in menus
        bindScreenshotKey();
        initInput();
        // scan for annotated methods and register them too
        getInput().scanForUserActions(this);

        initEventHandlers();

        defaultProfile = createProfile();

        preInit();
    }

    @Override
    public final void start(Stage stage) throws Exception {
        super.start(stage);

        // services are now ready, switch to normal logger
        log = FXGL.getLogger(GameApplication.class);

        log.debug("Starting Game Application");

        sceneFactory = initSceneFactory();

        loadingScene = sceneFactory.newLoadingScene();

        getDisplay().registerScene(loadingScene);
        getDisplay().registerScene(getGameScene());

        initFXGL();

        onStageShow();
        stage.show();

        if (getSettings().isMenuEnabled() && !getSettings().isIntroEnabled())
            showProfileDialog();

        log.debug("Showing stage");
        log.debug("Root size: " + stage.getScene().getRoot().getLayoutBounds().getWidth() + "x" + stage.getScene().getRoot().getLayoutBounds().getHeight());
        log.debug("Scene size: " + stage.getScene().getWidth() + "x" + stage.getScene().getHeight());
        log.debug("Stage size: " + stage.getWidth() + "x" + stage.getHeight());
    }

    private class InitAppTask extends Task<Void> {
        private Serializable data;

        private InitAppTask() {
            this.data = null;
        }

        /**
         * @param data the data to load from, null if new game
         */
        private InitAppTask(Serializable data) {
            this.data = data;
        }

        @Override
        protected Void call() throws Exception {
            update("Initializing Assets", 0);
            initAssets();

            update("Initializing Game", 1);
            if (data == null)
                initGame();
            else
                loadState(data);

            update("Initializing Physics", 2);
            initPhysics();

            update("Initializing UI", 3);
            initUI();
            initFPSOverlay();

            update("Initialization Complete", 4);
            return null;
        }

        private void update(String message, int step) {
            log.debug(message);
            updateMessage(message);
            updateProgress(step, 4);
        }

        @Override
        protected void succeeded() {
            getEventBus().fireEvent(FXGLEvent.initAppComplete());
            resume();
        }

        @Override
        protected void failed() {
            Throwable error = getException();
            error = error == null ? new RuntimeException("Initialization failed") : error;

            Thread.getDefaultUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), error);
        }
    }

    /**
     * Initialize user application.
     */
    private void initApp(Task<?> initTask) {
        log.debug("Initializing App");
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
     */
    protected void startNewGame() {
        log.debug("Starting new game");
        initApp(new InitAppTask());
    }

    /**
     * (Re-)initializes the user application from the given data file and starts the game.
     *
     * @param data save data to load from
     */
    protected void startLoadedGame(Serializable data) {
        log.debug("Starting loaded game");
        initApp(new InitAppTask(data));
    }

    /**
     * Pauses the main loop execution.
     */
    protected void pause() {
        log.debug("Pausing main loop");
        getEventBus().fireEvent(FXGLEvent.pause());

        setState(ApplicationState.PAUSED);
    }

    /**
     * Resumes the main loop execution.
     */
    protected void resume() {
        log.debug("Resuming main loop");
        getEventBus().fireEvent(FXGLEvent.resume());

        setState(ApplicationState.PLAYING);
    }

    /**
     * Reset the application.
     */
    private void reset() {
        log.debug("Resetting FXGL application");
        getEventBus().fireEvent(FXGLEvent.reset());
    }

    /**
     * Exit the application.
     */
    protected void exit() {
        log.debug("Exiting Normally");

        // if it is null then we are running without menus
        if (profileName != null)
            saveLoadManager.saveProfile(createProfile());

        getEventBus().fireEvent(FXGLEvent.exit());

        log.close();
        stop();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Stores the default profile data. This is used to restore default settings.
     */
    private UserProfile defaultProfile;

    /**
     * Stores current selected profile name for this game.
     */
    private String profileName;

    /**
     * Create a user profile with current settings.
     *
     * @return user profile
     */
    public final UserProfile createProfile() {
        UserProfile profile = new UserProfile(getSettings().getTitle(), getSettings().getVersion());

        save(profile);
        getEventBus().fireEvent(new SaveEvent(profile));

        return profile;
    }

    /**
     * Load from given user profile.
     *
     * @param profile the profile
     * @return true if loaded successfully, false if couldn't load
     */
    public final boolean loadFromProfile(UserProfile profile) {
        if (!profile.isCompatible(getSettings().getTitle(), getSettings().getVersion()))
            return false;

        load(profile);
        getEventBus().fireEvent(new LoadEvent(LoadEvent.LOAD_PROFILE, profile));
        return true;
    }

    /**
     * Restores default settings, e.g. audio, video, controls.
     */
    public final void restoreDefaultSettings() {
        getEventBus().fireEvent(new LoadEvent(LoadEvent.RESTORE_SETTINGS, defaultProfile));
    }

    private SaveLoadManager saveLoadManager;

    /**
     * @return save load manager
     */
    public SaveLoadManager getSaveLoadManager() {
        if (saveLoadManager == null) {
            throw new IllegalStateException("SaveLoadManager is not ready");
        }

        return saveLoadManager;
    }

    /**
     * Returns target width of the application. This is the
     * width that was set using GameSettings.
     * Note that the resulting
     * width of the scene might be different due to end user screen, in
     * which case transformations will be automatically scaled down
     * to ensure identical image on all screens.
     *
     * @return target width
     */
    public final double getWidth() {
        return getSettings().getWidth();
    }

    /**
     * Returns target height of the application. This is the
     * height that was set using GameSettings.
     * Note that the resulting
     * height of the scene might be different due to end user screen, in
     * which case transformations will be automatically scaled down
     * to ensure identical image on all screens.
     *
     * @return target height
     */
    public final double getHeight() {
        return getSettings().getHeight();
    }

    /**
     * Returns the visual area within the application window,
     * excluding window borders. Note that it will return the
     * rectangle with set target width and height, not actual
     * screen width and height. Meaning on smaller screens
     * the area will correctly return the GameSettings' width and height.
     * <p>
     * Equivalent to new Rectangle2D(0, 0, getWidth(), getHeight()).
     *
     * @return screen bounds
     */
    public final Rectangle2D getScreenBounds() {
        return new Rectangle2D(0, 0, getWidth(), getHeight());
    }

    /**
     * @return current tick
     */
    public final long getTick() {
        return getMasterTimer().getTick();
    }

    /**
     * @return current time since start of game in nanoseconds
     */
    public final long getNow() {
        return getMasterTimer().getNow();
    }

    private long playtime = 0;
    private long startTime = System.nanoTime();

    @Override
    public void save(UserProfile profile) {
        log.debug("Saving data to profile");

        UserProfile.Bundle bundle = new UserProfile.Bundle("game");
        bundle.put("playtime", System.nanoTime() - startTime + playtime);

        bundle.log();
        profile.putBundle(bundle);
    }

    @Override
    public void load(UserProfile profile) {
        log.debug("Loading data from profile");
        UserProfile.Bundle bundle = profile.getBundle("game");
        bundle.log();

        playtime = bundle.get("playtime");
    }
}
