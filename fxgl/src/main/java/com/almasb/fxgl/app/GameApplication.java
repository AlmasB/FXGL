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

import com.almasb.fxgl.app.listener.ExitListener;
import com.almasb.fxgl.app.listener.StateListener;
import com.almasb.fxgl.app.listener.UpdateListener;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.core.logging.FXGLLogger;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.devtools.profiling.Profiler;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.gameplay.*;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.io.FXGLIO;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.fxgl.saving.LoadEvent;
import com.almasb.fxgl.saving.SaveEvent;
import com.almasb.fxgl.scene.GameScene;
import com.almasb.fxgl.scene.PreloadingScene;
import com.almasb.fxgl.scene.menu.MenuEventListener;
import com.almasb.fxgl.service.*;
import com.almasb.fxgl.service.impl.display.DisplayEvent;
import com.almasb.fxgl.time.FPSCounter;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.time.Timer;
import com.almasb.fxgl.util.Version;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * To use FXGL extend this class and implement necessary methods.
 * The initialization process can be seen below (irrelevant phases are omitted):
 * <p>
 * <ol>
 * <li>Instance fields of YOUR subclass of GameApplication</li>
 * <li>initSettings()</li>
 * <li>Services configuration (after this you can safely call any FXGL.* methods)</li>
 * <p>Executed on JavaFX UI thread:</p>
 * <li>initAchievements()</li>
 * <li>initInput()</li>
 * <li>preInit()</li>
 * <p>NOT executed on JavaFX UI thread:</p>
 * <li>initAssets()</li>
 * <li>initGameVars()</li>
 * <li>initGame() OR loadState()</li>
 * <li>initPhysics()</li>
 * <li>initUI()</li>
 * <p>Start of main game loop execution on JavaFX UI thread</p>
 * </ol>
 * <p>
 * Unless explicitly stated, methods are not thread-safe and must be
 * executed on the JavaFX Application (UI) Thread.
 * By default all callbacks are executed on the JavaFX Application (UI) Thread.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class GameApplication extends Application {

    /**
     * Use system logger until actual logger is ready.
     */
    private static Logger log = FXGLLogger.getSystemLogger();

    private Stage primaryStage;
    private ReadOnlyGameSettings settings;
    private AppStateMachine stateMachine;

    /**
     * This is the main entry point as run by the JavaFX platform.
     */
    @Override
    public final void start(Stage stage) {
        primaryStage = stage;

        Version.print();
        showPreloadingStage();
        startFXGL();
    }

    /**
     * Shows preloading stage with scene while FXGL is being configured.
     */
    private void showPreloadingStage() {
        Stage preloadingStage = new Stage(StageStyle.UNDECORATED);
        preloadingStage.initOwner(primaryStage);
        preloadingStage.setScene(new PreloadingScene());
        preloadingStage.show();

        // when main stage is ready to show
        primaryStage.setOnShowing(e -> {
            // close our preloader
            preloadingStage.close();
            // clean the reference to lambda + preloader
            primaryStage.setOnShowing(null);
        });
    }

    private void startFXGL() {
        new Thread(() -> {
            try {
                configureFXGL();

                runUpdaterAndWait();

                configureApp();

                launchGame();
            } catch (Exception e) {
                log.fatal("Exception during system configuration:");
                log.fatal(FXGLLogger.errorTraceAsString(e));
                log.fatal("System will now exit");
                log.close();

                // we don't know what exactly has been initialized
                // so to avoid the process hanging just shut down the JVM
                System.exit(-1);
            }
        }, "FXGL Launcher Thread").start();
    }

    /**
     * After this call all FXGL.* calls are valid.
     */
    private void configureFXGL() {
        long start = System.nanoTime();

        initSystemProperties();
        initUserProperties();
        initAppSettings();

        FXGL.configure(new ApplicationModule(this));

        // actual logger is ready
        log = FXGLLogger.get(GameApplication.class);
        log.debug("FXGL configuration complete");

        log.infof("FXGL configuration took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);

        log.debug("Logging game settings\n" + settings.toString());
    }

    /**
     * Load FXGL system properties.
     */
    private void initSystemProperties() {
        ResourceBundle props = ResourceBundle.getBundle("com.almasb.fxgl.app.system");
        props.keySet().forEach(key -> {
            Object value = props.getObject(key);
            FXGL.setProperty(key, value);
        });
    }

    /**
     * Load user defined properties to override FXGL system properties.
     */
    private void initUserProperties() {
        // services are not ready yet, so load manually
        try (InputStream is = getClass().getResource("/assets/properties/system.properties").openStream()) {
            ResourceBundle props = new PropertyResourceBundle(is);
            props.keySet().forEach(key -> {
                Object value = props.getObject(key);
                FXGL.setProperty(key, value);
            });
        } catch (NullPointerException npe) {
            // User properties not found. Using system
        } catch (IOException e) {
            log.warning("Loading user properties failed: " + e);
        }
    }

    /**
     * Take app settings from user.
     */
    private void initAppSettings() {
        GameSettings localSettings = new GameSettings();
        initSettings(localSettings);
        settings = localSettings.toReadOnly();
    }

    private void runUpdaterAndWait() {
        Async.startFX(() -> {
            new UpdaterTask().run();
        }).await();
    }

    private void configureApp() {
        log.debug("Configuring GameApplication");

        long start = System.nanoTime();

        initStateMachine();
        registerServicesForUpdate();


        log.infof("Game configuration took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);
    }

    private void initStateMachine() {
        stateMachine = new AppStateMachine(this);
        playState = (PlayState) stateMachine.getPlayState();
    }

    private void registerServicesForUpdate() {
        addUpdateListener(getAudioPlayer());

        getEventBus().addEventHandler(NotificationEvent.ANY, e -> getAudioPlayer().onNotificationEvent(e));
        getEventBus().addEventHandler(AchievementEvent.ANY, e -> getNotificationService().onAchievementEvent(e));

        getEventBus().addEventHandler(SaveEvent.ANY, e -> {
            getAudioPlayer().save(e.getProfile());
            getDisplay().save(e.getProfile());
            getInput().save(e.getProfile());
            getAchievementManager().save(e.getProfile());
            getGameplay().getQuestManager().save(e.getProfile());
        });

        getEventBus().addEventHandler(LoadEvent.ANY, e -> {
            getAudioPlayer().load(e.getProfile());
            getDisplay().load(e.getProfile());
            getInput().load(e.getProfile());
            getAchievementManager().load(e.getProfile());
            getGameplay().getQuestManager().load(e.getProfile());
        });

        getEventBus().addEventHandler(DisplayEvent.CLOSE_REQUEST, e -> exit());

        getEventBus().scanForHandlers(this);
    }

    private void generateDefaultProfile() {
        if (getSettings().isMenuEnabled()) {
            menuHandler.generateDefaultProfile();
        }
    }

    private void launchGame() {
        Async.startFX(() -> {
            FXGL.getDisplay().initAndShow();

            // these things need to be called early before the main loop
            // so that menus can correctly display input controls, etc.
            // this is called once per application lifetime
            runPreInit();

            startMainLoop();
        });
    }

    private void runPreInit() {
        log.debug("Running preInit()");

        if (getSettings().isProfilingEnabled()) {
            profiler = new Profiler();
        }

        FXGLIO.INSTANCE.setDefaultExceptionHandler(getExceptionHandler());
        FXGLIO.INSTANCE.setDefaultExecutor(getExecutor());

        initAchievements();

        // 1. register system actions
        SystemActions.INSTANCE.bind(getInput());

        // 2. register user actions
        initInput();

        // 3. scan for annotated methods and register them too
        getInput().scanForUserActions(this);

        generateDefaultProfile();

        preInit();

        // attempt to clean any garbage we generated before main loop
        System.gc();
    }

    private AnimationTimer mainLoop;

    private void startMainLoop() {
        log.debug("Starting main loop");

        mainLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long frameStart = System.nanoTime();

                tpf = tickStart(now);

                tick(tpf);

                tickEnd(System.nanoTime() - frameStart);
            }
        };
        mainLoop.start();
    }

    /**
     * Only called in exceptional cases, e.g. uncaught (unchecked) exception.
     */
    void stopMainLoop() {
        log.debug("Stopping main loop");

        if (mainLoop != null)
            mainLoop.stop();
    }

    private ReadOnlyLongWrapper tick = new ReadOnlyLongWrapper();
    private ReadOnlyIntegerWrapper fps = new ReadOnlyIntegerWrapper();

    private double tpf;

    private FPSCounter fpsCounter = new FPSCounter();

    private double tickStart(long now) {
        tick.set(tick.get() + 1);

        fps.set(fpsCounter.update(now));

        // assume that fps is at least 5 to avoid subtle bugs
        // disregard minor fluctuations > 55 for smoother experience
        if (fps.get() < 5 || fps.get() > 55)
            fps.set(60);

        return 1.0 / fps.get();
    }

    private void tick(double tpf) {
        stateMachine.onUpdate(tpf);

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onUpdate(tpf);
        }

        if (stateMachine.isInPlay()) {
            onUpdate(tpf);
            onPostUpdate(tpf);
        } else {
            onPausedUpdate(tpf);
        }
    }

    private Profiler profiler;

    private void tickEnd(long frameTook) {
        if (getSettings().isProfilingEnabled()) {
            profiler.update(fps.get(), frameTook);
            profiler.render(getGameScene().getGraphicsContext());
        }
    }

    /**
     * (Re-)initializes the user application as new and starts the game.
     */
    protected void startNewGame() {
        log.debug("Starting new game");
        stateMachine.startLoad(DataFile.getEMPTY());
    }

    /**
     * (Re-)initializes the user application from the given data file and starts the game.
     *
     * @param dataFile save data to load from
     */
    void startLoadedGame(DataFile dataFile) {
        log.debug("Starting loaded game");
        stateMachine.startLoad(dataFile);
    }

    /**
     * Exit the application.
     */
    protected final void exit() {
        log.debug("Exiting FXGL application");
        exitListeners.forEach(ExitListener::onExit);

        if (getSettings().isProfilingEnabled()) {
            profiler.print();
        }

        FXGL.destroy();
        log.debug("Closing FXGL logger and exiting JavaFX");
        log.close();

        Platform.exit();
    }

    /**
     * Handler for menu events.
     */
    private MenuEventHandler menuHandler;

    /**
     * @return menu event handler associated with this game
     * @throws IllegalStateException if menus are not enabled
     */
    public final MenuEventListener getMenuListener() {
        if (!getSettings().isMenuEnabled())
            throw new IllegalStateException("Menus are not enabled");

        if (menuHandler == null)
            menuHandler = new MenuEventHandler(this);
        return menuHandler;
    }

    private CopyOnWriteArrayList<UpdateListener> listeners = new CopyOnWriteArrayList<>();

    public final void addUpdateListener(UpdateListener listener) {
        listeners.add(listener);
    }

    public final void removeUpdateListener(UpdateListener listener) {
        listeners.remove(listener);
    }

    private List<ExitListener> exitListeners = new ArrayList<>();

    public final void addExitListener(ExitListener listener) {
        exitListeners.add(listener);
    }

    public final void removeExitListener(ExitListener listener) {
        exitListeners.remove(listener);
    }

    private PlayState playState;

    public final void addPlayStateListener(StateListener listener) {
        playState.addStateListener(listener);
    }

    public final void removePlayStateListener(StateListener listener) {
        playState.removeStateListener(listener);
    }

    /* CALLBACKS BEGIN */

    @Override
    public final void init() {}

    @Override
    public final void stop() {}

    /**
     * Initialize app settings.
     *
     * @param settings app settings
     */
    protected abstract void initSettings(GameSettings settings);

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
    protected void initAchievements() {}

    /**
     * Initialize input, i.e. bind key presses, bind mouse buttons.
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
    protected void initInput() {}

    /**
     * This is called after core services are initialized
     * but before any game init.
     * Called only once per application lifetime.
     */
    protected void preInit() {}

    /**
     * Initialize game assets, such as Texture, Sound, Music, etc.
     */
    protected void initAssets() {}

    /**
     * Can be overridden to provide global variables.
     *
     * @param vars map containing CVars (global variables)
     */
    protected void initGameVars(Map<String, Object> vars) {}

    /**
     * Initialize game objects.
     */
    protected void initGame() {}

    /**
     * Initialize collision handlers, physics properties.
     */
    protected void initPhysics() {}

    /**
     * Initialize UI objects.
     */
    protected void initUI() {}

    /**
     * Called every frame _only_ in Play state.
     *
     * @param tpf time per frame
     */
    protected void onUpdate(double tpf) {}

    /**
     * Called after main loop tick has been completed in Play state.
     * It can be used to de-register callbacks / listeners
     * and call various methods that otherwise might interfere
     * with main loop.
     *
     * @param tpf time per frame (same as main update tpf)
     */
    protected void onPostUpdate(double tpf) {}

    /**
     * Called every frame in any non-Play state.
     *
     * @param tpf time per frame
     */
    protected void onPausedUpdate(double tpf) {}

    /**
     * Called when MenuEvent.SAVE occurs.
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
     *
     * @param dataFile previously saved data
     * @throws UnsupportedOperationException if was not overridden
     */
    protected void loadState(DataFile dataFile) {
        log.warning("Called loadState(), but it wasn't overridden!");
        throw new UnsupportedOperationException("Default implementation is not available");
    }

    /* CALLBACKS END */

    /* MOCKING */

    /**
     * Used by mocking.
     *
     * @param stage mock stage
     */
    void injectStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Used by mocking.
     *
     * @param settings mock settings
     */
    void injectSettings(ReadOnlyGameSettings settings) {
        this.settings = settings;
    }

    /* GETTERS */

    /**
     * @return time per frame for current frame
     */
    public final double tpf() {
        return tpf;
    }

    public final AppStateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * @return primary stage as set by JavaFX
     */
    final Stage getPrimaryStage() {
        return primaryStage;
    }

    public final GameState getGameState() {
        return playState.getGameState();
    }

    public final GameWorld getGameWorld() {
        return playState.getGameWorld();
    }

    public final PhysicsWorld getPhysicsWorld() {
        return playState.getPhysicsWorld();
    }

    public final GameScene getGameScene() {
        return playState.getGameScene();
    }

    public final Gameplay getGameplay() {
        return FXGL.getGameplay();
    }

    /**
     * @return play state input
     */
    public final Input getInput() {
        return playState.getInput();
    }

    /**
     * @return play state timer
     */
    public final Timer getMasterTimer() {
        return playState.getTimer();
    }

    /**
     * @return read only copy of game settings
     */
    public final ReadOnlyGameSettings getSettings() {
        return settings;
    }

    /**
     * @return target width as set by GameSettings
     */
    public final int getWidth() {
        return getSettings().getWidth();
    }

    /**
     * @return target height as set by GameSettings
     */
    public final int getHeight() {
        return getSettings().getHeight();
    }

    /**
     * @return app bounds as set by GameSettings
     * @apiNote equivalent to new Rectangle2D(0, 0, getWidth(), getHeight())
     */
    public final Rectangle2D getAppBounds() {
        return new Rectangle2D(0, 0, getWidth(), getHeight());
    }

    /**
     * @return current tick (frame)
     */
    public final long getTick() {
        return tick.get();
    }

    public final EventBus getEventBus() {
        return FXGL.getEventBus();
    }

    public final Display getDisplay() {
        return FXGL.getDisplay();
    }

    public final AudioPlayer getAudioPlayer() {
        return FXGL.getAudioPlayer();
    }

    public final AssetLoader getAssetLoader() {
        return FXGL.getAssetLoader();
    }

    public final Executor getExecutor() {
        return FXGL.getExecutor();
    }

    public final NotificationService getNotificationService() {
        return FXGL.getNotificationService();
    }

    public final AchievementManager getAchievementManager() {
        return FXGL.getAchievementManager();
    }

    public final Net getNet() {
        return FXGL.getNet();
    }

    public final ExceptionHandler getExceptionHandler() {
        return FXGL.getExceptionHandler();
    }

    public final UIFactory getUIFactory() {
        return FXGL.getUIFactory();
    }
}
