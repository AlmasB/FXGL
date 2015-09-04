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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.almasb.fxgl.asset.AssetManager;
import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.effect.ParticleManager;
import com.almasb.fxgl.event.InputManager;
import com.almasb.fxgl.event.QTEManager;
import com.almasb.fxgl.physics.PhysicsManager;
import com.almasb.fxgl.time.TimerManager;
import com.almasb.fxgl.ui.FXGLGameMenu;
import com.almasb.fxgl.ui.FXGLMainMenu;
import com.almasb.fxgl.ui.Menu;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.Version;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * To use FXGL extend this class and implement necessary methods.
 *
 * Unless explicitly stated, methods are not thread-safe and must be
 * executed on JavaFX Application Thread.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public abstract class GameApplication extends Application {

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").severe("Unhandled Exception");
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").severe(FXGLLogger.errorTraceAsString(error));
            FXGLLogger.getLogger("FXGL.DefaultErrorHandler").severe("Closing due to Unhandled Exception");
            FXGLLogger.close();
            FXGLExceptionHandler.INSTANCE.handle(error);
            System.exit(0);
        });
        FXGLLogger.init(Level.CONFIG);
        Version.print();
    }

    private static GameApplication instance;

    /**
     *
     * @return singleton instance of the current game application
     */
    public static final GameApplication getInstance() {
        return instance;
    }

    /*
     * Order of execution.
     *
     * 1. the following initializer block (clinit)
     * 2. instance fields
     * 3. ctor()
     * 4. init()
     * 5. start()
     */
    {
        // make sure first thing we do is get back the reference from JavaFX
        // so that anything can now use getInstance()
        log.finer("clinit()");
        instance = this;
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

    /**
     * Various managers that handle different aspects of the application
     * and need to be updated together with game world tick.
     */
    private List<FXGLManager> managers = new ArrayList<>();

    private StageManager stageManager = new StageManager();
    protected final SceneManager sceneManager = new SceneManager();
    protected final InputManager inputManager = new InputManager();
    protected final AssetManager assetManager = AssetManager.INSTANCE;
    protected final PhysicsManager physicsManager = new PhysicsManager();
    protected final TimerManager timerManager = new TimerManager();
    protected final ParticleManager particleManager = new ParticleManager();
    protected final QTEManager qteManager = new QTEManager();
    protected final SaveLoadManager saveLoadManager = SaveLoadManager.INSTANCE;

    /**
     * Default random number generator
     */
    protected final Random random = new Random();

    /**
     * Initialize game settings
     *
     * @param settings
     */
    protected abstract void initSettings(GameSettings settings);

    /**
     * Override to use your custom intro video
     *
     * @return
     */
    protected Intro initIntroVideo() {
        return new FXGLIntro(getWidth(), getHeight());
    }

    /**
     * Override to use your custom main menu
     *
     * @return
     */
    protected Menu initMainMenu() {
        return new FXGLMainMenu(this);
    }

    /**
     * Override to use your custom game menu
     *
     * @return
     */
    protected Menu initGameMenu() {
        return new FXGLGameMenu(this);
    }

    /**
     * Initialize game assets, such as Texture, AudioClip, Music
     *
     * @throws Exception
     */
    protected abstract void initAssets() throws Exception;

    /**
     * Called when user selects "save" from menu
     *
     * Default implementation returns null
     *
     * @return data with required info about current state
     */
    public Serializable saveState() {
        log.warning("Called saveState(), but it wasn't overriden!");
        return null;
    }

    /**
     * Called when user selects "continue" or "load" from menu
     *
     * @param data
     */
    public void loadState(Serializable data) {
        log.warning("Called loadState(), but it wasn't overriden!");
    }

    /**
     * Initialize game objects
     */
    protected abstract void initGame();

    /**
     * Initiliaze collision handlers, physics properties
     */
    protected abstract void initPhysics();

    /**
     * Initiliaze UI objects
     */
    protected abstract void initUI();

    /**
     * Initiliaze input, i.e.
     * bind key presses / key typed, bind mouse
     */
    protected abstract void initInput();

    /**
     * Main loop update phase, most of game logic and clean up
     *
     * @param now
     */
    protected abstract void onUpdate();

    /**
     * Default implementation does nothing
     *
     * Override to add your own cleanup
     */
    protected void onExit() {

    }

    /**
     * This is called AFTER all init methods complete
     * and BEFORE the main loop starts
     *
     * It is safe to use any protected fields at this stage
     */
    protected void postInit() {

    }

    /**
     * Ensure managers are of legal state and ready
     */
    private void initManagers() {
        inputManager.init(sceneManager.getScene());
        qteManager.init();
        sceneManager.init();
        sceneManager.getScene().addEventHandler(KeyEvent.KEY_RELEASED, qteManager::keyReleasedHandler);

        // register managers that need to be updated
        managers.add(inputManager);
        managers.add(timerManager);
        managers.add(physicsManager);
        managers.add(particleManager);
        managers.add(sceneManager);
    }

    /**
     * Initialize user application
     */
    private void initApp() {
        try {
            initAssets();
            initGame();
            initPhysics();
            initUI();
            initInput();

            postInit();
        }
        catch (Exception e) {
            log.severe("Exception occurred during initialization: " + e.getMessage());

            Arrays.asList(e.getStackTrace())
                .stream()
                .map(StackTraceElement::toString)
                .filter(s -> !s.contains("Unknown Source") && !s.contains("Native Method"))
                .map(s -> "Cause: " + s)
                .forEachOrdered(log::severe);
            FXGLExceptionHandler.INSTANCE.handle(e);
            exit();
        }
    }

    public GameApplication() {
        log.finer("GameApplication()");
    }

    @Override
    public final void init() {
        log.finer("init()");
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        log.finer("start()");

        GameSettings localSettings = new GameSettings();
        initSettings(localSettings);
        settings = new ReadOnlyGameSettings(localSettings);

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

        FXGLLogger.init(logLevel);

        log.info("Application Mode: " + settings.getApplicationMode());
        log.info("Log Level: " + logLevel);


        initManagers();

        stageManager.init(primaryStage);
        stageManager.show();
        sceneManager.onStageShow();
    }

    @Override
    public final void stop() {
        log.finer("stop()");
    }

    /**
     * This is the internal FXGL update tick,
     * executed 60 times a second ~ every 0.166 (6) seconds
     *
     * @param internalTime - The timestamp of the current frame given in nanoseconds (from JavaFX)
     */
    private void processUpdate(long internalTime) {
        // this will set up current tick and current time
        // for the rest modules of the game to use
        timerManager.tickStart(internalTime);

        // update managers
        managers.forEach(manager -> manager.onUpdate(getNow()));

        // update app
        onUpdate();

        // this is only end for our processing tick for basic profiling
        // the actual JavaFX tick ends when our new tick begins. So
        // JavaFX event callbacks will properly fire within the same "our" tick.
        timerManager.tickEnd();
    }

    /**
     * Call this to manually start the game.
     * To be used ONLY in menus.
     */
    public final void startNewGame() {
        initApp();

        getTimerManager().resetTicks();

        getSceneManager().returnFromMainMenu();
        timer.start();
    }

    /**
     * Pauses the main loop execution
     */
    public final void pause() {
        timer.stop();
    }

    /**
     * Resumes the main loop execution
     */
    public final void resume() {
        timer.start();
    }

    /**
     * This method will be automatically called when main window is closed.
     *
     * You can call this method when you want to quit the application manually
     * from the game
     */
    public final void exit() {
        log.finer("Closing Normally");
        onExit();
        FXGLLogger.close();
        Platform.exit();
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
     *
     * Equivalent to new Rectangle2D(0, 0, getWidth(), getHeight()).
     *
     * @return screen bounds
     */
    public final Rectangle2D getScreenBounds() {
        return new Rectangle2D(0, 0, getWidth(), getHeight());
    }

    /**
     *
     * @return current tick
     */
    public final long getTick() {
        return getTimerManager().getTick();
    }

    /**
     *
     * @return current time since start of game in nanoseconds
     */
    public final long getNow() {
        return getTimerManager().getNow();
    }

    /**
     *
     * @return timer manager
     */
    public final TimerManager getTimerManager() {
        return timerManager;
    }

    /**
     *
     * @return scene manager
     */
    public final SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     *
     * @return physics manager
     */
    public final PhysicsManager getPhysicsManager() {
        return physicsManager;
    }

    /**
     *
     * @return input manager
     */
    public final InputManager getInputManager() {
        return inputManager;
    }

    /**
     *
     * @return asset manager
     */
    public final AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     *
     * @return read only copy of game settings
     */
    public final ReadOnlyGameSettings getSettings() {
        return settings;
    }
}
