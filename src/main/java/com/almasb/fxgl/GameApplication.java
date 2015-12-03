/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl;

import com.almasb.fxgl.app.FXGLApplication;
import com.almasb.fxgl.donotuse.QTEManager;
import com.almasb.fxgl.event.*;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.scene.*;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.settings.UserProfile;
import com.almasb.fxgl.time.MasterTimer;
import com.almasb.fxgl.ui.NotificationManager;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.FXGLUncaughtExceptionHandler;
import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.Serializable;
import java.util.Optional;

/**
 * To use FXGL extend this class and implement necessary methods.
 * The initialization process can be seen below (irrelevant phases are omitted):
 * <p>
 * <ol>
 * <li>Instance fields of YOUR subclass of GameApplication</li>
 * <li>initSettings()</li>
 * <li>All FXGL managers (after this you can safely call get*Manager()</li>
 * <li>initInput()</li>
 * <li>initMenuFactory() (if enabled)</li>
 * <li>initIntroVideo() (if enabled)</li>
 * <li>initAssets()</li>
 * <li>initGame()</li>
 * <li>initPhysics()</li>
 * <li>initUI()</li>
 * <li>Start of main game loop execution</li>
 * </ol>
 * <p>
 * Unless explicitly stated, methods are not thread-safe and must be
 * executed on JavaFX Application Thread.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class GameApplication extends FXGLApplication {

    {
        log.finer("Game_clinit()");
        setDefaultUncaughtExceptionHandler(new FXGLUncaughtExceptionHandler());
    }

    /**
     * Set handler for runtime uncaught exceptions
     *
     * @param handler exception handler
     */
    public final void setDefaultUncaughtExceptionHandler(ExceptionHandler handler) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            pause();
            log.severe("Uncaught Exception:");
            log.severe(FXGLLogger.errorTraceAsString(error));
            log.severe("Application will now exit");
            handler.handle(error);
            exit();
        });
    }

    private ObjectProperty<GameState> state = new SimpleObjectProperty<>();

    public final GameState getState() {
        return state.get();
    }

    private void setState(GameState gameState) {
        log.finer("State: " + getState() + " -> " + gameState);
        state.set(gameState);
        switch (gameState) {
            case INTRO:
                getDisplay().setScene(introScene);
                break;
            case MAIN_MENU:
                getDisplay().setScene(mainMenuScene);
                break;
            case GAME_MENU:
                getDisplay().setScene(gameMenuScene);
                break;
            case PLAYING:
                getDisplay().setScene(gameScene);
                break;
        }
    }

    @Inject
    private GameWorld gameWorld;

    /**
     * @return game world
     */
    public final GameWorld getGameWorld() {
        return gameWorld;
    }

    @Inject
    private PhysicsWorld physicsWorld;

    /**
     * @return physics world
     */
    public final PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    /**
     * Game scene, this is where all in-game objects are shown.
     */
    @Inject
    private GameScene gameScene;

    /**
     * @return game scene
     */
    public final GameScene getGameScene() {
        return gameScene;
    }

    /**
     * Intro scene, this is shown when the application started,
     * before menus and game.
     */
    private IntroScene introScene;

    /**
     * Main menu, this is the menu shown at the start of game
     */
    private FXGLScene mainMenuScene;

    /**
     * In-game menu, this is shown when menu key pressed during the game
     */
    private FXGLScene gameMenuScene;

    private QTEManager qteManager;
    private NotificationManager notificationManager;
    private AchievementManager achievementManager;

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
     * Initiliaze input, i.e.
     * bind key presses / key typed, bind mouse.
     * <p>
     * This method is called prior to any game init.
     * <p>
     * <pre>
     * Example:
     *
     * Input input = getInput();
     * input.addAction(new UserAction("Move Left") {
     *      protected void onAction() {
     *          player.translate(-5, 0);
     *      }
     * }, KeyCode.A);
     * </pre>
     */
    protected abstract void initInput();

    /**
     * Override to use your custom intro video.
     *
     * @return intro animation
     */
    protected IntroFactory initIntroFactory() {
        return new IntroFactory() {
            @Override
            public IntroScene newIntro(ReadOnlyGameSettings settings) {
                return new FXGLIntroScene(settings);
            }
        };
    }

    /**
     * Override to user your custom menus.
     *
     * @return menu factory for creating main and game menus
     */
    protected MenuFactory initMenuFactory() {
        return getSettings().getMenuStyle().getFactory();
    }

    /**
     * Initialize game assets, such as Texture, Sound, Music, etc.
     *
     * @throws Exception
     */
    protected abstract void initAssets() throws Exception;

    /**
     * Called when MenuEvent.SAVE occurs.
     * <p>
     * Default implementation returns null.
     *
     * @return data with required info about current state
     */
    public Serializable saveState() {
        log.warning("Called saveState(), but it wasn't overriden!");
        return null;
    }

    /**
     * Called when MenuEvent.LOAD occurs.
     *
     * @param data previously saved data
     */
    public void loadState(Serializable data) {
        log.warning("Called loadState(), but it wasn't overriden!");
    }

    /**
     * Initialize game objects.
     */
    protected abstract void initGame();

    /**
     * Initiliaze collision handlers, physics properties.
     */
    protected abstract void initPhysics();

    /**
     * Initiliaze UI objects.
     */
    protected abstract void initUI();

    /**
     * Main loop update phase, most of game logic.
     */
    protected abstract void onUpdate();

    private void initEventHandlers() {
        getEventBus().addEventHandler(UpdateEvent.ANY, event -> onUpdate());

        getEventBus().addEventHandler(DisplayEvent.CLOSE_REQUEST, e -> exit());
        getEventBus().addEventHandler(DisplayEvent.DIALOG_OPENED, e -> {
            if (!isMenuOpen())
                pause();
        });
        getEventBus().addEventHandler(DisplayEvent.DIALOG_CLOSED, e -> {
            if (!isMenuOpen())
                resume();
        });
    }

    /**
     * @return true if any menu is open
     */
    public boolean isMenuOpen() {
        return getState() == GameState.GAME_MENU
                || getState() == GameState.MAIN_MENU;
    }

    private boolean canSwitchGameMenu = true;

    /**
     * Creates Main and Game menu scenes.
     * Registers menu event handlers.
     */
    private void configureMenu() {
        MenuFactory menuFactory = initMenuFactory();

        mainMenuScene = menuFactory.newMainMenu(this);
        gameMenuScene = menuFactory.newGameMenu(this);

        getDisplay().registerScene(mainMenuScene);
        getDisplay().registerScene(gameMenuScene);

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
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

        gameScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == getSettings().getMenuKey())
                canSwitchGameMenu = true;
        });
        gameMenuScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == getSettings().getMenuKey())
                canSwitchGameMenu = true;
        });

        getEventBus().addEventHandler(MenuEvent.NEW_GAME, event -> startNewGame());
        getEventBus().addEventHandler(MenuEvent.EXIT, event -> exit());
        getEventBus().addEventHandler(MenuEvent.EXIT_TO_MAIN_MENU, event -> {
            pause();
            reset();
            setState(GameState.MAIN_MENU);
        });

        getEventBus().addEventHandler(MenuEvent.PAUSE, event -> {
            pause();
            setState(GameState.GAME_MENU);
        });
        getEventBus().addEventHandler(MenuEvent.RESUME, event -> {
            resume();
            setState(GameState.PLAYING);
        });

        getEventBus().addEventHandler(MenuEvent.SAVE, event -> {
            String saveFileName = event.getData().map(name -> (String) name).orElse("");
            if (!saveFileName.isEmpty()) {
                boolean ok = getSaveLoadManager().save(saveState(), saveFileName).isOK();
                if (!ok)
                    getDisplay().showMessageBox("Failed to save");
            }
        });

        getEventBus().addEventHandler(MenuEvent.LOAD, event -> {
            String saveFileName = event.getData().map(name -> (String) name)
                    .orElse("");

            Optional<Serializable> saveFile = saveFileName.isEmpty()
                    ? getSaveLoadManager().loadLastModifiedFile()
                    : getSaveLoadManager().load(saveFileName);

            saveFile.ifPresent(this::startLoadedGame);
        });
    }

    private void configureIntro() {
        introScene = initIntroFactory().newIntro(getSettings());
        introScene.setOnFinished(this::showGame);
        getDisplay().registerScene(introScene);
    }

    /**
     * Called right before the main stage is shown.
     */
    void onStageShow() {
        if (getSettings().isIntroEnabled()) {
            configureIntro();
            setState(GameState.INTRO);

            introScene.startIntro();
        } else {
            showGame();
        }
    }

    private void showGame() {
        if (getSettings().isMenuEnabled()) {
            configureMenu();
            setState(GameState.MAIN_MENU);
        } else {
            startNewGame();
        }
    }

    @Override
    public final void start(Stage stage) throws Exception {
        super.start(stage);
        log.finer("Game_start()");

        UIFactory.init(getService(ServiceType.ASSET_LOADER).loadFont(getSettings().getDefaultFontName()));

        //qteManager = new QTEManager();

        notificationManager = new NotificationManager(getGameScene().getRoot());
        achievementManager = new AchievementManager();

        getDisplay().registerScene(gameScene);

        initAchievements();
        // we call this early to process user input bindings
        // so we can correctly display them in menus
        initInput();

        initEventHandlers();

        defaultProfile = createProfile();
        getSaveLoadManager().loadProfile().ifPresent(this::loadFromProfile);

        onStageShow();
        stage.show();

        log.finer("Showing stage");
        log.finer("Root size: " + stage.getScene().getRoot().getLayoutBounds().getWidth() + "x" + stage.getScene().getRoot().getLayoutBounds().getHeight());
        log.finer("Scene size: " + stage.getScene().getWidth() + "x" + stage.getScene().getHeight());
        log.finer("Stage size: " + stage.getWidth() + "x" + stage.getHeight());
    }

    /**
     * Initialize user application.
     *
     * @param data the data to load from, null if new game
     */
    private void initApp(Serializable data) {
        log.finer("Initializing app");

        try {
            initAssets();

            if (data == null)
                initGame();
            else
                loadState(data);

            initPhysics();
            initUI();

            if (getSettings().isFPSShown()) {
                Text fpsText = UIFactory.newText("", 24);
                fpsText.setTranslateY(getSettings().getHeight() - 40);
                fpsText.textProperty().bind(getTimerManager().fpsProperty().asString("FPS: [%d]\n")
                        .concat(getTimerManager().performanceFPSProperty().asString("Performance: [%d]")));
                getGameScene().addUINode(fpsText);
            }

            getEventBus().fireEvent(FXGLEvent.initAppComplete());

            setState(GameState.PLAYING);

        } catch (Exception e) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    /**
     * (Re-)initializes the user application as new and starts the game.
     */
    final void startNewGame() {
        log.finer("Starting new game");
        initApp(null);
    }

    /**
     * (Re-)initializes the user application from the given data file and starts the game.
     *
     * @param data save data to load from
     */
    final void startLoadedGame(Serializable data) {
        log.finer("Starting loaded game");
        reset();
        initApp(data);
    }

    /**
     * Pauses the main loop execution.
     */
    final void pause() {
        log.finer("Pausing main loop");
        getEventBus().fireEvent(FXGLEvent.pause());
    }

    /**
     * Resumes the main loop execution.
     */
    final void resume() {
        log.finer("Resuming main loop");
        getEventBus().fireEvent(FXGLEvent.resume());
    }

    /**
     * Reset the application.
     */
    final void reset() {
        log.finer("Resetting FXGL application");
        getEventBus().fireEvent(FXGLEvent.reset());
    }

    /**
     * Exits the application.
     * <p>
     * This method will be automatically called when main window is closed.
     */
    final void exit() {
        log.finer("Exiting Normally");
        getEventBus().fireEvent(FXGLEvent.exit());

        FXGLLogger.close();
        Platform.exit();
        System.exit(0);
    }

    /**
     * Stores the default profile data. This is used to restore default settings.
     */
    private UserProfile defaultProfile;

    /**
     * Create a user profile with current settings.
     *
     * @return user profile
     */
    public final UserProfile createProfile() {
        UserProfile profile = new UserProfile(getSettings().getTitle(), getSettings().getVersion());

        getEventBus().fireEvent(new SaveEvent(profile));

        return profile;
    }

    /**
     * Load from given user profile
     *
     * @param profile the profile
     */
    public final void loadFromProfile(UserProfile profile) {
        if (!profile.isCompatible(getSettings().getTitle(), getSettings().getVersion()))
            return;

        getEventBus().fireEvent(new LoadEvent(profile));
    }

    /**
     * Load from default user profile. Restores default settings.
     */
    public final void loadFromDefaultProfile() {
        loadFromProfile(defaultProfile);
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
        return getTimerManager().getTick();
    }

    /**
     * @return current time since start of game in nanoseconds
     */
    public final long getNow() {
        return getTimerManager().getNow();
    }

    /**
     * @return timer manager
     */
    public final MasterTimer getTimerManager() {
        return getMasterTimer();
    }

    /**
     * @return QTE manager
     */
    public final QTEManager getQTEManager() {
        return qteManager;
    }

    /**
     *
     * @return notification manager
     */
    public final NotificationManager getNotificationManager() {
        return notificationManager;
    }

    /**
     *
     * @return achievement manager
     */
    public final AchievementManager getAchievementManager() {
        return achievementManager;
    }
}
