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
import com.almasb.ents.Entity;
import com.almasb.ents.EntityWorldListener;
import com.almasb.fxeventbus.EventBus;
import com.almasb.fxgl.devtools.DeveloperTools;
import com.almasb.fxgl.devtools.profiling.Profiler;
import com.almasb.fxgl.event.*;
import com.almasb.fxgl.gameplay.GameWorld;
import com.almasb.fxgl.gameplay.SaveLoadManager;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.io.DataFile;
import com.almasb.fxgl.io.SaveFile;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.logging.SystemLogger;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.scene.*;
import com.almasb.fxgl.scene.menu.MenuEventListener;
import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.settings.UserProfileSavable;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.FXGLUncaughtExceptionHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDateTime;

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

    private void initGlobalEventHandlers() {
        log.debug("Initializing global event handlers");

        EventBus bus = getEventBus();

        Font fpsFont = Font.font("Lucida Console", 20);

        // the debug data max chars is ~110, so just add a margin
        StringBuilder sb = new StringBuilder(128);

        getMasterTimer().setUpdateListener(event -> {
            getInput().onUpdateEvent(event);
            getAudioPlayer().onUpdateEvent(event);

            getGameWorld().onUpdateEvent(event);
            getPhysicsWorld().onUpdateEvent(event);
            getGameScene().onUpdateEvent(event);

            onUpdate(event.tpf());

            // notify rest
            bus.fireEvent(event);

            if (getSettings().isFPSShown()) {
                GraphicsContext g = getGameScene().getGraphicsContext();

                g.setFont(fpsFont);
                g.setFill(Color.RED);

                // clear the contents
                sb.setLength(0);
                sb.append("FPS: ").append(getMasterTimer().getFPS())
                        .append("\nPerformance: ").append(getMasterTimer().getPerformanceFPS())
                        .append("\nNow Mem (MB): ").append((int) profiler.getCurrentMemoryUsage())
                        .append("\nAvg Mem (MB): ").append((int) profiler.getAvgMemoryUsage())
                        .append("\nMin Mem (MB): ").append((int) profiler.getMinMemoryUsage())
                        .append("\nMax Mem (MB): ").append((int) profiler.getMaxMemoryUsage());

                g.fillText(sb.toString(), 0, getHeight() - 120);
            }
        });

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

        bus.addEventHandler(FXGLEvent.PAUSE, event -> {
            getInput().onPause();
            getMasterTimer().onPause();

            setState(ApplicationState.PAUSED);
        });

        bus.addEventHandler(FXGLEvent.RESUME, event -> {
            getInput().onResume();
            getMasterTimer().onResume();

            setState(ApplicationState.PLAYING);
        });

        bus.addEventHandler(FXGLEvent.RESET, event -> {
            getGameWorld().reset();
            getPhysicsWorld().reset();
            getGameScene().onWorldReset();

            getInput().onReset();
            getMasterTimer().onReset();
        });

        bus.addEventHandler(FXGLEvent.EXIT, event -> {
            saveProfile();
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
            getInput().onMouseEvent(event, getGameScene().getViewport());
        });
        getGameScene().addEventHandler(KeyEvent.ANY, event -> {
            getInput().onKeyEvent(event);
        });

        bus.addEventHandler(NotificationEvent.ANY, event -> {
            getAudioPlayer().onNotificationEvent(event);
        });

        bus.addEventHandler(AchievementEvent.ANY, event -> {
            getNotificationService().onAchievementEvent(event);
        });

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

    /**
     * Creates Main and Game menu scenes.
     * Registers them with the Display service.
     * Adds key binding so that scenes can be switched on menu key press.
     */
    private void configureMenu() {
        mainMenuScene = sceneFactory.newMainMenu(this);
        gameMenuScene = sceneFactory.newGameMenu(this);

        MenuEventHandler handler = new MenuEventHandler();
        mainMenuScene.setListener(handler);
        gameMenuScene.setListener(handler);

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

    private class MenuEventHandler implements MenuEventListener {
        @Override
        public void onNewGame() {
            startNewGame();
        }

        @Override
        public void onContinue() {
            saveLoadManager.loadLastModifiedSaveFileTask()
                    .then(file -> saveLoadManager.loadTask(file))
                    .onSuccess(GameApplication.this::startLoadedGame)
                    .executeAsyncWithDialogFX(new ProgressDialog("Loading..."));
        }

        @Override
        public void onResume() {
            resume();
        }

        private void doSave(String saveFileName) {
            DataFile dataFile = saveState();
            SaveFile saveFile = new SaveFile(saveFileName, LocalDateTime.now());

            saveLoadManager.saveTask(dataFile, saveFile)
                    .executeAsyncWithDialogFX(new ProgressDialog("Saving data: " + saveFileName));
        }

        @Override
        public void onSave() {
            getDisplay().showInputBoxWithCancel("Enter save file name", DialogPane.ALPHANUM, saveFileName -> {

                if (saveFileName.isEmpty())
                    return;

                if (saveLoadManager.saveFileExists(saveFileName)) {
                    getDisplay().showConfirmationBox("Overwrite save [" + saveFileName + "]?", yes -> {

                        if (yes)
                            doSave(saveFileName);
                    });
                } else {
                    doSave(saveFileName);
                }
            });
        }

        @Override
        public void onLoad(SaveFile saveFile) {

            getDisplay().showConfirmationBox("Load save [" + saveFile.getName() + "]?\n"
                    + "Unsaved progress will be lost!", yes -> {

                if (yes) {
                    saveLoadManager.loadTask(saveFile)
                            .onSuccess(GameApplication.this::startLoadedGame)
                            .executeAsyncWithDialogFX(new ProgressDialog("Loading: " + saveFile.getName()));
                }
            });
        }

        @Override
        public void onDelete(SaveFile saveFile) {

            getDisplay().showConfirmationBox("Delete save [" + saveFile.getName() + "]?", yes -> {

                if (yes) {
                    saveLoadManager.deleteSaveFileTask(saveFile)
                            .executeAsyncWithDialogFX(new ProgressDialog("Deleting: " + saveFile.getName()));
                }
            });
        }

        @Override
        public void onLogout() {

            getDisplay().showConfirmationBox("Log out?", yes -> {

                if (yes) {
                    saveProfile();
                    showProfileDialog();
                }
            });
        }

        @Override
        public void onMultiplayer() {
            showMultiplayerDialog();
        }

        @Override
        public void onExit() {

            getDisplay().showConfirmationBox("Exit the game?", yes -> {

                if (yes)
                    exit();
            });
        }

        @Override
        public void onExitToMainMenu() {

            getDisplay().showConfirmationBox("Exit to Main Menu?\n"
                    + "All unsaved progress will be lost!", yes -> {

                if (yes) {
                    pause();
                    reset();
                    setState(ApplicationState.MAIN_MENU);
                }
            });
        }
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

    private void showMultiplayerDialog() {
        Button btnHost = UIFactory.newButton("Host...");
        btnHost.setOnAction(e -> {
            getDisplay().showMessageBox("NOT SUPPORTED YET");
        });

        Button btnConnect = UIFactory.newButton("Connect...");
        btnConnect.setOnAction(e -> {
            getDisplay().showMessageBox("NOT SUPPORTED YET");
        });

        getDisplay().showBox("Multiplayer Options", UIFactory.newText(""), btnHost, btnConnect);
    }

    /**
     * Show profile dialog so that user selects existing or creates new profile.
     * The dialog is only dismissed when profile is chosen either way.
     */
    private void showProfileDialog() {
        ChoiceBox<String> profilesBox = UIFactory.newChoiceBox(FXCollections.observableArrayList());

        Button btnNew = UIFactory.newButton("NEW");
        Button btnSelect = UIFactory.newButton("SELECT");
        btnSelect.disableProperty().bind(profilesBox.valueProperty().isNull());
        Button btnDelete = UIFactory.newButton("DELETE");
        btnDelete.disableProperty().bind(profilesBox.valueProperty().isNull());

        btnNew.setOnAction(e -> {
            getDisplay().showInputBox("New Profile", DialogPane.ALPHANUM, name -> {
                profileName.set(name);
                saveLoadManager = new SaveLoadManager(name);

                getEventBus().fireEvent(new ProfileSelectedEvent(name, false));

                saveProfile();
            });
        });

        btnSelect.setOnAction(e -> {
            String name = profilesBox.getValue();

            saveLoadManager = new SaveLoadManager(name);

            saveLoadManager.loadProfileTask()
                    .onSuccess(profile -> {
                        boolean ok = loadFromProfile(profile);

                        if (!ok) {
                            getDisplay().showErrorBox("Profile is corrupted: " + name, this::showProfileDialog);
                        } else {
                            profileName.set(name);

                            saveLoadManager.loadLastModifiedSaveFileTask()
                                    .onSuccess(file -> {
                                        getEventBus().fireEvent(new ProfileSelectedEvent(name, true));
                                    })
                                    .onFailure(error -> {
                                        getEventBus().fireEvent(new ProfileSelectedEvent(name, false));
                                    })
                                    .executeAsyncWithDialogFX(new ProgressDialog("Loading last save file"));
                        }
                    })
                    .onFailure(error -> {
                        getDisplay().showErrorBox("Profile is corrupted: " + name + "\nError: "
                                + error.toString(), this::showProfileDialog);
                    })
                    .executeAsyncWithDialogFX(new ProgressDialog("Loading Profile: "+ name));
        });

        btnDelete.setOnAction(e -> {
            String name = profilesBox.getValue();

            SaveLoadManager.deleteProfileTask(name)
                    .onSuccess(n -> showProfileDialog())
                    .onFailure(error -> getDisplay().showErrorBox(error.toString(), this::showProfileDialog))
                    .executeAsyncWithDialogFX(new ProgressDialog("Deleting profile: " + name));
        });

        SaveLoadManager.loadProfileNamesTask()
                .onSuccess(names -> {
                    profilesBox.getItems().addAll(names);

                    if (!profilesBox.getItems().isEmpty()) {
                        profilesBox.getSelectionModel().selectFirst();
                    }

                    getDisplay().showBox("Select profile or create new", profilesBox, btnSelect, btnNew, btnDelete);
                })
                .onFailure(e -> {
                    log.warning(e.toString());

                    getDisplay().showBox("Select profile or create new", profilesBox, btnSelect, btnNew, btnDelete);
                })
                .executeAsyncWithDialogFX(new ProgressDialog("Loading profiles"));
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

    private void bindDeveloperKey() {
        getInput().addAction(new UserAction("Developer Options") {
            @Override
            protected void onActionBegin() {
                log.debug("Scene graph contains " + DeveloperTools.INSTANCE.getChildrenSize(getGameScene().getRoot())
                        + " nodes");
            }
        }, KeyCode.DIGIT0, InputModifier.CTRL);
    }

    private void initFXGL() {
        initAchievements();
        // we call this early to process user input bindings
        // so we can correctly display them in menus
        bindScreenshotKey();
        bindDeveloperKey();
        initInput();
        // scan for annotated methods and register them too
        getInput().scanForUserActions(this);

        initGlobalEventHandlers();

        defaultProfile = createProfile();

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
            showProfileDialog();

        if (getSettings().isProfilingEnabled()) {
            profiler = FXGL.newProfiler();
            profiler.start();

            getEventBus().addEventHandler(FXGLEvent.EXIT, e -> {
                profiler.stop();
                profiler.print();
            });
        }

        log.info("GameApplication start took: " + (System.nanoTime() - start) / 1000000000.0 + " sec");
    }

    /**
     * Initialize user application.
     */
    private void initApp(Task<?> initTask) {
        log.debug("Initializing App");

        // on first run this is no-op, as for rest this ensures
        // that even without menus and during direct calls to start*Game()
        // the system is clean
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

    /**
     * Stores the default profile data. This is used to restore default settings.
     */
    private UserProfile defaultProfile;

    /**
     * Stores current selected profile name for this game.
     */
    private ReadOnlyStringWrapper profileName = new ReadOnlyStringWrapper("");

    /**
     * @return profile name property (read-only)
     */
    public final ReadOnlyStringProperty profileNameProperty() {
        return profileName.getReadOnlyProperty();
    }

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

    private void saveProfile() {
        // if it is empty then we are running without menus
        if (!profileName.get().isEmpty()) {
            saveLoadManager.saveProfileTask(createProfile())
                    .onFailure(e -> log.warning("Failed to save profile: " + profileName.get() + " - " + e))
                    // we execute synchronously to avoid incomplete save since we might be shutting down
                    .execute();
        }
    }

    @Override
    public void save(UserProfile profile) {
        // if there is a need for data save
//        log.debug("Saving data to profile");
//
//        UserProfile.Bundle bundle = new UserProfile.Bundle("game");
//        bundle.put("...", ...);
//
//        bundle.log();
//        profile.putBundle(bundle);
    }

    @Override
    public void load(UserProfile profile) {
//        log.debug("Loading data from profile");
//        UserProfile.Bundle bundle = profile.getBundle("game");
//        bundle.log();
    }
}
