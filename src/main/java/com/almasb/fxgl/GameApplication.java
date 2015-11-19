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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.asset.AudioManager;
import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.QTEManager;
import com.almasb.fxgl.physics.PhysicsManager;
import com.almasb.fxgl.settings.*;
import com.almasb.fxgl.time.TimerManager;
import com.almasb.fxgl.ui.FXGLIntroScene;
import com.almasb.fxgl.ui.IntroFactory;
import com.almasb.fxgl.ui.IntroScene;
import com.almasb.fxgl.ui.MenuFactory;
import com.almasb.fxgl.ui.UIFactory;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.FXGLCheckedExceptionHandler;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.FXGLUncaughtExceptionHandler;
import com.almasb.fxgl.util.Version;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
public abstract class GameApplication extends Application {

    private static GameApplication instance;

    /**
     * @return singleton instance of the current game application
     * @deprecated applications should not rely on global access.
     * This method will be removed in future versions.
     */
    @Deprecated
    public static final GameApplication getInstance() {
        return instance;
    }

    static {
        FXGLLogger.init(Level.CONFIG);
        Version.print();
    }

    /*
     * Order of execution.
     *
     * 1. the following initializer block (clinit)
     * 2. instance fields
     * 3. ctor()
     * 4. init()
     * 5. start()
     */ {
        // make sure first thing we do is get back the reference from JavaFX
        // so that anything can now use getInstance()
        log.finer("clinit()");
        instance = this;
        setDefaultUncaughtExceptionHandler(new FXGLUncaughtExceptionHandler());
        setDefaultCheckedExceptionHandler(new FXGLCheckedExceptionHandler());
    }

    private static ExceptionHandler defaultCheckedExceptionHandler;

    /**
     * @return handler for checked exceptions
     */
    public static ExceptionHandler getDefaultCheckedExceptionHandler() {
        return defaultCheckedExceptionHandler;
    }

    /**
     * Set handler for checked exceptions
     *
     * @param handler exception handler
     */
    public static final void setDefaultCheckedExceptionHandler(ExceptionHandler handler) {
        defaultCheckedExceptionHandler = error -> {
            log.warning("Checked Exception:");
            log.warning(FXGLLogger.errorTraceAsString(error));
            handler.handle(error);
        };
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

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGL.GameApplication");

    /**
     * Settings for this game instance. This is an internal copy
     * of the settings so that they will not be modified during game lifetime.
     */
    private ReadOnlyGameSettings settings;

    /**
     * The main loop timer
     */
    private AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long internalTime) {
            processUpdate(internalTime);
        }
    };

    private GameWorld gameWorld = new GameWorld();

    /*
     * Various game state managers.
     */
    private SceneManager sceneManager;
    private AudioManager audioManager;
    private InputManager inputManager;
    private AssetManager assetManager;
    private PhysicsManager physicsManager;
    private TimerManager timerManager;
    private QTEManager qteManager;
    private SaveLoadManager saveLoadManager;

    /**
     * Initialize game settings.
     *
     * @param settings game settings
     */
    protected abstract void initSettings(GameSettings settings);

    /**
     * Initiliaze input, i.e.
     * bind key presses / key typed, bind mouse.
     * <p>
     * This method is called prior to any game init.
     * <p>
     * <pre>
     * Example:
     *
     * InputManager input = getInputManager();
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
            public IntroScene newIntro(SceneSettings settings) {
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

    /**
     * Default implementation does nothing.
     * <p>
     * This method is called during the transition from playing state
     * to menu state.
     */
    protected void onMenuOpen() {
    }

    /**
     * Default implementation does nothing.
     * <p>
     * This method is called during the transition from menu state
     * to playing state.
     */
    protected void onMenuClose() {
    }

    /**
     * Default implementation does nothing.
     * <p>
     * Override to add your own cleanup. This will be called
     * prior to application exit.
     */
    protected void onExit() {
    }

    /**
     * Ensure managers are of legal state and ready.
     */
    private void initManagers(Stage stage) {
        assetManager = AssetManager.INSTANCE;
        saveLoadManager = SaveLoadManager.INSTANCE;

        timerManager = new TimerManager();

        Scene scene = new Scene(new Pane());
        stage.setScene(scene);
        sceneManager = new SceneManager(this, scene);
        inputManager = new InputManager(getSceneManager().getGameScene());

        physicsManager = new PhysicsManager(settings.getHeight(), timerManager.tickProperty());

        audioManager = new AudioManager();
        qteManager = new QTEManager();

        // profile data listeners
        profileSavables.add(inputManager);
        profileSavables.add(audioManager);
        profileSavables.add(sceneManager);
    }

    /**
     * Registers world state listeners.
     */
    private void initWorld() {
        gameWorld.addWorldStateListener(inputManager);
        gameWorld.addWorldStateListener(timerManager);
        gameWorld.addWorldStateListener(physicsManager);
        gameWorld.addWorldStateListener(getGameScene());
        gameWorld.addWorldStateListener(audioManager);
    }

    /**
     * Configure main stage based on user settings.
     *
     * @param stage the stage
     */
    private void initStage(Stage stage) {
        stage.setTitle(settings.getTitle() + " " + settings.getVersion());
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            e.consume();

            UIFactory.getDialogBox().showConfirmationBox("Exit the game?", yes -> {
                if (yes)
                    exit();
            });
        });
        stage.getIcons().add(AssetManager.INSTANCE.loadAppIcon(settings.getIconFileName()));

        if (settings.isFullScreen()) {
            stage.setFullScreenExitHint("");
            // we don't want the user to be able to exit full screen manually
            // but only through settings menu
            // so we set key combination to something obscure which isn't likely
            // to be pressed
            stage.setFullScreenExitKeyCombination(KeyCombination.keyCombination("Shortcut+>"));
            stage.setFullScreen(true);
        }

        stage.setOnShowing(e -> getSceneManager().onStageShow());
        stage.sizeToScene();
    }

    @Override
    public final void start(Stage stage) throws Exception {
        log.finer("start()");

        GameSettings localSettings = new GameSettings();
        initSettings(localSettings);
        settings = localSettings.toReadOnly();

        Level logLevel = Level.ALL;
        switch (settings.getApplicationMode()) {
            case DEVELOPER:
                logLevel = Level.CONFIG;
                break;
            case RELEASE:
                logLevel = Level.SEVERE;
                break;
            case DEBUG: // fallthru
            default:
                break;
        }

        UIFactory.init(stage, AssetManager.INSTANCE.loadFont(settings.getDefaultFontName()));
        FXGLLogger.init(logLevel);

        log.info("Application Mode: " + settings.getApplicationMode());

        initManagers(stage);
        // we call this early to process user input bindings
        // so we can correctly display them in menus
        initInput();
        initWorld();
        initStage(stage);

        defaultProfile = createProfile();
        SaveLoadManager.INSTANCE.loadProfile().ifPresent(this::loadFromProfile);

        stage.show();

        log.finer("Showing stage");
        log.finer("Root size: " + stage.getScene().getRoot().getLayoutBounds().getWidth() + "x" + stage.getScene().getRoot().getLayoutBounds().getHeight());
        log.finer("Scene size: " + stage.getScene().getWidth() + "x" + stage.getScene().getHeight());
        log.finer("Stage size: " + stage.getWidth() + "x" + stage.getHeight());
    }

    /**
     * This is the internal FXGL update tick,
     * executed 60 times a second ~ every 0.166 (6) seconds.
     *
     * @param internalTime - The timestamp of the current frame given in nanoseconds (from JavaFX)
     */
    private void processUpdate(long internalTime) {
        // this will set up current tick and current time
        // for the rest of the game modules to use
        timerManager.tickStart(internalTime);

        gameWorld.update();
        onUpdate();

        // this is only end for our processing tick for basic profiling
        // the actual JavaFX tick ends when our new tick begins. So
        // JavaFX event callbacks will properly fire within the same "our" tick.
        timerManager.tickEnd();
    }

    /**
     * Initialize user application.
     */
    private void initApp() {
        log.finer("Initializing app");

        try {
            initAssets();
            initGame();
            initPhysics();
            initUI();

            if (getSettings().isFPSShown()) {
                Text fpsText = UIFactory.newText("", 24);
                fpsText.setTranslateY(getSettings().getHeight() - 40);
                fpsText.textProperty().bind(getTimerManager().fpsProperty().asString("FPS: [%d]\n")
                        .concat(getTimerManager().performanceFPSProperty().asString("Performance: [%d]")));
                getGameScene().addUINode(fpsText);
            }
        } catch (Exception e) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

    /**
     * (Re-)initializes the user application, resets the managers
     * and starts the game.
     */
    final void startNewGame() {
        log.finer("Starting new game");
        initApp();
        timer.start();
    }

    /**
     * Pauses the main loop execution.
     */
    final void pause() {
        log.finer("Pausing main loop");
        timer.stop();
        getInputManager().clearAllInput();
    }

    /**
     * Resumes the main loop execution.
     */
    final void resume() {
        log.finer("Resuming main loop");
        getInputManager().clearAllInput();
        timer.start();
    }

    /**
     * Reset the application and game world.
     */
    final void reset() {
        gameWorld.reset();
    }

    /**
     * Exits the application.
     * <p>
     * This method will be automatically called when main window is closed.
     */
    final void exit() {
        log.finer("Exiting Normally");
        onExit();
        FXGLLogger.close();
        Platform.exit();
        System.exit(0);
    }

    private List<UserProfileSavable> profileSavables = new ArrayList<>();

    private UserProfile defaultProfile;

    public final UserProfile createProfile() {
        UserProfile profile = new UserProfile();
        profileSavables.forEach(s -> s.save(profile));
        return profile;
    }

    public final void loadFromProfile(UserProfile profile) {
        profileSavables.forEach(l -> l.load(profile));
    }

    public final void loadFromDefaultProfile() {
        loadFromProfile(defaultProfile);
    }

    /**
     *
     * @return game world
     */
    public final GameWorld getGameWorld() {
        return gameWorld;
    }

    /**
     *
     * @return game scene
     */
    public final GameScene getGameScene() {
        return getSceneManager().getGameScene();
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
    public final TimerManager getTimerManager() {
        return timerManager;
    }

    /**
     * @return scene manager
     */
    public final SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * @return audio manager
     */
    public final AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * @return physics manager
     */
    public final PhysicsManager getPhysicsManager() {
        return physicsManager;
    }

    /**
     * @return input manager
     */
    public final InputManager getInputManager() {
        return inputManager;
    }

    /**
     * @return asset manager
     */
    public final AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     * @return save load manager
     */
    public final SaveLoadManager getSaveLoadManager() {
        return saveLoadManager;
    }

    /**
     * @return QTE manager
     */
    public final QTEManager getQTEManager() {
        return qteManager;
    }

    /**
     * @return read only copy of game settings
     */
    public final ReadOnlyGameSettings getSettings() {
        return settings;
    }
}
