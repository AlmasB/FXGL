/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.app;

import com.almasb.fxgl.app.listener.ExitListener;
import com.almasb.fxgl.app.listener.StateListener;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.core.logging.*;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.devtools.profiling.Profiler;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.gameplay.*;
import com.almasb.fxgl.gameplay.notification.NotificationEvent;
import com.almasb.fxgl.gameplay.notification.NotificationService;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.net.Net;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.fxgl.saving.LoadEvent;
import com.almasb.fxgl.saving.SaveEvent;
import com.almasb.fxgl.scene.FXGLScene;
import com.almasb.fxgl.scene.GameScene;
import com.almasb.fxgl.scene.PreloadingScene;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.scene.menu.MenuEventListener;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.time.FPSCounter;
import com.almasb.fxgl.time.Timer;
import com.almasb.fxgl.ui.Display;
import com.almasb.fxgl.ui.ErrorDialog;
import com.almasb.fxgl.ui.UIFactory;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.*;

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
 * Note: do NOT make any FXGL calls within your APP constructor or to initialize
 * APP fields during declaration, make these calls in initGame().
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class GameApplication extends Application {

    private static final Logger log = Logger.get(GameApplication.class);

    private MainWindow mainWindow;
    private ReadOnlyGameSettings settings;
    private AppStateMachine stateMachine;

    MainWindow getMainWindow() {
        return mainWindow;
    }

    /**
     * This is the main entry point as run by the JavaFX platform.
     */
    @Override
    public final void start(Stage stage) {
        try {
            initAppSettings();
            initLogger();

            initMainWindow(stage);

            showPreloadingStage();

            log.debug("Starting FXGL");

            new Thread(new FXGLStartTask(), "FXGL Launcher Thread").start();

        } catch (Exception e) {
            handleFatalErrorBeforeLaunch(e);
        }
    }

    private class FXGLStartTask extends Task<Void> {

        @Override
        protected Void call() {

            // after this call all FXGL.* calls are valid.
            FXGL.configure(GameApplication.this);

            log.debug("FXGL started");
            log.debug("Logging game settings\n" + settings.toString());

            initFatalExceptionHandler();

            log.debug("Configuring GameApplication");

            long start = System.nanoTime();

            initStateMachine();
            attachEventHandlers();

            log.infof("Game configuration took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);

            return null;
        }

        @Override
        protected void succeeded() {
            mainWindow.initAndShow();

            // these things need to be called early before the main loop
            // so that menus can correctly display input controls, etc.
            // this is called once per application lifetime
            runPreInit();

            // attempt to clean any garbage we generated before main loop
            System.gc();

            startMainLoop();
        }

        @Override
        protected void failed() {
            handleFatalErrorBeforeLaunch(getException());
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

    private void initLogger() {
        Logger.configure(new LoggerConfig());
        // we write all logs to file but adjust console log level based on app mode
        Logger.addOutput(new FileOutput("FXGL"), LoggerLevel.DEBUG);
        Logger.addOutput(new ConsoleOutput(), settings.getApplicationMode().getLoggerLevel());

        log.debug("Logger initialized");
    }

    private void initMainWindow(Stage stage) {
        mainWindow = new MainWindow(stage, settings);
        stage.iconifiedProperty().addListener((o, wasMinimized, isMinimized) -> {
            if (isMinimized) {
                pauseMainLoop();
            } else {
                resumeMainLoop();
            }
        });
    }

    /**
     * Shows preloading stage with scene while FXGL is being configured.
     */
    private void showPreloadingStage() {
        Stage preloadingStage = new Stage(StageStyle.UNDECORATED);
        preloadingStage.initOwner(mainWindow.getStage());
        preloadingStage.setScene(new PreloadingScene());
        preloadingStage.show();

        // when main stage has opened
        mainWindow.setOnShown(() -> {
            // close our preloader
            preloadingStage.close();
            // clean the reference to lambda + preloader
            mainWindow.setOnShown(null);
        });
    }

    private void handleFatalErrorBeforeLaunch(Throwable error) {
        if (Logger.isConfigured()) {
            log.fatal("Exception during FXGL configuration:");
            log.fatal(Logger.errorTraceAsString(error));
            log.fatal("FXGL will now exit");

            Logger.close();
        } else {
            System.out.println("Exception during FXGL configuration:");
            error.printStackTrace();
            System.out.println("FXGL will now exit");
        }

        // we can't assume we are running on JavaFX Application thread
        Async.startFX(() -> {
            // block with error dialog so that user can read the error
            new ErrorDialog(error).showAndWait();
        }).await();

        // we don't know what exactly has been initialized
        // so to avoid the process hanging just shut down the JVM
        System.exit(-1);
    }

    private void initFatalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, error) -> handleFatalErrorAfterLaunch(error));
    }

    private boolean handledOnce = false;

    private void handleFatalErrorAfterLaunch(Throwable error) {
        if (handledOnce) {
            // just ignore to avoid spamming dialogs
            return;
        }

        handledOnce = true;

        log.fatal("Uncaught Exception:");
        log.fatal(Logger.errorTraceAsString(error));
        log.fatal("Application will now exit");

        // stop main loop from running as we cannot continue
        stopMainLoop();

        // assume we are running on JavaFX Application thread
        // block with error dialog so that user can read the error
        new ErrorDialog(error).showAndWait();

        // exit normally
        exit();
    }

    private void initStateMachine() {
        log.debug("Initializing state machine and application states");

        SceneFactory sceneFactory = FXGL.getSettings().getSceneFactory();

        // STARTUP is default
        AppState initial = new StartupState(this);

        AppState loading = new LoadingState(this, sceneFactory);
        AppState play = new PlayState(sceneFactory);

        // reasonable hack to trigger dialog state init before intro and menus
        DialogSubState.INSTANCE.getView();

        /**
         * Replaced Null with Empty
         * Edited by: Hanzallah Azim Burney
         */
        AppState intro = this.getSettings().isIntroEnabled() ? new IntroState(this, sceneFactory) : AppState.EMPTY;
        AppState mainMenu = this.getSettings().isMenuEnabled() ? new MainMenuState(sceneFactory) : AppState.EMPTY;
        AppState gameMenu = this.getSettings().isMenuEnabled() ? new GameMenuState(sceneFactory) : AppState.EMPTY;

        stateMachine = new AppStateMachine(loading, play, DialogSubState.INSTANCE, intro, mainMenu, gameMenu, initial);

        stateMachine.addListener(new StateChangeListener() {
            @Override
            public void beforeEnter(State state) {
                if (state instanceof AppState) {
                    setScene(((AppState)state).getScene());
                } else if (state instanceof SubState) {
                    getScene().getRoot().getChildren().add(((SubState)state).getView());
                }
            }

            @Override
            public void exited(State state) {
                if (state instanceof SubState) {
                    getScene().getRoot().getChildren().remove(((SubState)state).getView());
                }
            }
        });

        playState = (PlayState) stateMachine.getPlayState();
    }

    private void attachEventHandlers() {
        getEventBus().addEventHandler(NotificationEvent.ANY, e -> getAudioPlayer().onNotificationEvent(e));
        getEventBus().addEventHandler(AchievementEvent.ANY, e -> getNotificationService().onAchievementEvent(e));

        getEventBus().addEventHandler(SaveEvent.ANY, e -> {
            getAudioPlayer().save(e.getProfile());
            getInput().save(e.getProfile());
            getGameplay().save(e.getProfile());
        });

        getEventBus().addEventHandler(LoadEvent.ANY, e -> {
            getAudioPlayer().load(e.getProfile());
            getInput().load(e.getProfile());
            getGameplay().load(e.getProfile());
        });

        getEventBus().scanForHandlers(this);
    }

    private void runPreInit() {
        log.debug("Running preInit()");

        if (getSettings().isProfilingEnabled()) {
            profiler = new Profiler();
        }

        initAchievements();

        // 1. register system actions
        SystemActions.INSTANCE.bind(getInput());

        // 2. register user actions
        initInput();

        // 3. scan for annotated methods and register them too
        getInput().scanForUserActions(this);

        generateDefaultProfile();

        preInit();
    }

    /**
     * Finds all @SetAchievementStore classes and registers achievements.
     */
    private void initAchievements() {
        AnnotationParser parser = new AnnotationParser(this.getClass());
        parser.parse(SetAchievementStore.class);
        parser.getClasses(SetAchievementStore.class).forEach(storeClass -> {
            AchievementStore storeObject = (AchievementStore) ReflectionUtils.newInstance(storeClass);
            storeObject.initAchievements(getGameplay().getAchievementManager());
        });
    }

    private void generateDefaultProfile() {
        if (getSettings().isMenuEnabled()) {
            menuHandler.generateDefaultProfile();
        }
    }

    private AnimationTimer mainLoop;

    private void startMainLoop() {
        log.debug("Starting main loop");

        mainLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                tpf = tpfCompute(now);

                // if we are not in play state run as normal
                if (!(getStateMachine().isInPlay() && getSettings().isSingleStep())) {
                    stepLoop();
                }
            }
        };
        mainLoop.start();
    }

    private void resumeMainLoop() {
        if (mainLoop != null) {
            mainLoop.start();
        }
    }

    private void pauseMainLoop() {
        if (mainLoop != null) {
            mainLoop.stop();
            fpsCounter.reset();
        }
    }

    /**
     * Only called in exceptional cases, e.g. uncaught (unchecked) exception.
     */
    private void stopMainLoop() {
        if (mainLoop != null) {
            log.debug("Stopping main loop");
            mainLoop.stop();
        }
    }

    protected void stepLoop() {
        long frameStart = System.nanoTime();

        tick.set(tick.get() + 1);
        stateMachine.onUpdate(tpf);

        if (getSettings().isProfilingEnabled()) {
            long frameTook = System.nanoTime() - frameStart;

            profiler.update(fps.get(), frameTook);
            profiler.render(getGameScene().getProfilerText());
        }
    }

    private ReadOnlyLongWrapper tick = new ReadOnlyLongWrapper();
    private ReadOnlyIntegerWrapper fps = new ReadOnlyIntegerWrapper();

    private double tpf;

    private FPSCounter fpsCounter = new FPSCounter();

    private double tpfCompute(long now) {
        fps.set(fpsCounter.update(now));

        // assume that fps is at least 5 to avoid subtle bugs
        // disregard minor fluctuations > 55 for smoother experience
        if (fps.get() < 5 || fps.get() > 55)
            fps.set(60);

        return 1.0 / fps.get();
    }

    private Profiler profiler;

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
        log.debug("Exiting game application");
        exitListeners.forEach(ExitListener::onExit);

        log.debug("Shutting down background threads");
        getExecutor().shutdownNow();

        if (getSettings().isProfilingEnabled()) {
            profiler.print();
        }

        log.debug("Exiting FXGL");
        FXGL.destroy();

        log.debug("Closing logger and exiting JavaFX");
        Logger.close();

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

    FXGLScene getScene() {
        return mainWindow.getCurrentScene();
    }

    void setScene(FXGLScene scene) {
        mainWindow.setScene(scene);
    }

    boolean saveScreenshot() {
        return mainWindow.saveScreenshot();
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
    @Deprecated
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

    public final <T> T getGameConfig() {
        return FXGL.getGameConfig();
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
