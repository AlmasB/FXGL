/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.app;

import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.core.logging.*;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.gameplay.GameState;
import com.almasb.fxgl.gameplay.Gameplay;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.net.Net;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.fxgl.scene.GameScene;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.time.Timer;
import com.almasb.fxgl.ui.Display;
import com.almasb.fxgl.ui.UIFactory;
import javafx.geometry.Rectangle2D;

import java.util.Map;

/**
 * To use FXGL, extend this class and implement necessary methods.
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
public abstract class GameApplication {

    private static final Logger log = Logger.get(GameApplication.class);

    public static void launch(String[] args) {
        try {
            var instance = newInstance();

            // this will be set automatically by javafxports on mobile
            if (System.getProperty("javafx.platform") == null)
                System.setProperty("javafx.platform", "Desktop");

            var settings = instance.takeUserSettings();

            instance.initLogger(settings);

            FXGLApplication.launchFX(instance, settings, args);
        } catch (Exception e) {
            System.out.println("Error during launch:");
            e.printStackTrace();
            System.out.println("Application will now exit");
            System.exit(-1);
        }
    }

    private static GameApplication newInstance() throws Exception {
        // instantiate GameApp using JFX strategy
        // Figure out the right class to call
        StackTraceElement[] cause = Thread.currentThread().getStackTrace();

        boolean foundThisMethod = false;
        String callingClassName = null;
        for (StackTraceElement se : cause) {
            // Skip entries until we get to the entry for this class
            String className = se.getClassName();
            String methodName = se.getMethodName();
            if (foundThisMethod) {
                callingClassName = className;
                break;
            } else if (GameApplication.class.getName().equals(className)
                    && "launch".equals(methodName)) {

                foundThisMethod = true;
            }
        }

        if (callingClassName == null) {
            throw new RuntimeException("Error: unable to determine GameApplication class");
        }

        Class theClass = Class.forName(callingClassName, false, Thread.currentThread().getContextClassLoader());
        if (!GameApplication.class.isAssignableFrom(theClass)) {
            throw new RuntimeException("Error: " + theClass + " is not a subclass of GameApplication");
        }

        Class<? extends GameApplication> appClass = theClass;

        return appClass.getDeclaredConstructor().newInstance();
    }

    private ReadOnlyGameSettings takeUserSettings() {
        var localSettings = new GameSettings();
        initSettings(localSettings);
        return localSettings.toReadOnly();
    }

    private void initLogger(ReadOnlyGameSettings settings) {
        Logger.configure(new LoggerConfig());
        // we write all logs to file but adjust console log level based on app mode
        if (FXGL.isDesktop()) {
            Logger.addOutput(new FileOutput("FXGL"), LoggerLevel.DEBUG);
        }
        Logger.addOutput(new ConsoleOutput(), settings.getApplicationMode().getLoggerLevel());

        log.debug("Logger initialized");
        log.debug("Logging settings\n" + settings);
    }

    /* CALLBACKS BEGIN */

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

    private DataFile loadDataFile = DataFile.getEMPTY();

    /**
     * (Re-)initializes the user application as new and starts the game.
     */
    protected final void startNewGame() {
        log.debug("Starting new game");
        loadDataFile = DataFile.getEMPTY();
        getStateMachine().startLoad();
    }

    /**
     * (Re-)initializes the user application from the given data file and starts the game.
     *
     * @param dataFile save data to load from
     */
    void startLoadedGame(DataFile dataFile) {
        log.debug("Starting loaded game");
        loadDataFile = dataFile;
        getStateMachine().startLoad();
    }

    /**
     * Callback to finalize init game.
     * The data file to load will be set before this call.
     */
    void internalInitGame() {
        if (loadDataFile == DataFile.getEMPTY()) {
            initGame();
        } else {
            loadState(loadDataFile);
        }
    }

    /**
     * Exit the application.
     */
    protected void exit() {
        FXGL.exit();
    }

    /* CALLBACKS END */

    /* CONVENIENCE GETTERS */

    /**
     * @return time per frame for current frame
     */
    public final double tpf() {
        return FXGL.tpf();
    }

    public final AppStateMachine getStateMachine() {
        return FXGL.getStateMachine();
    }

    public final GameState getGameState() {
        return FXGL.getGameState();
    }

    public final GameWorld getGameWorld() {
        return FXGL.getGameWorld();
    }

    public final PhysicsWorld getPhysicsWorld() {
        return FXGL.getPhysicsWorld();
    }

    public final GameScene getGameScene() {
        return FXGL.getGameScene();
    }

    /**
     * @return play state input
     */
    public final Input getInput() {
        return FXGL.getInput();
    }

    /**
     * @return play state timer
     */
    public final Timer getMasterTimer() {
        return FXGL.getMasterTimer();
    }

    public final Gameplay getGameplay() {
        return FXGL.getGameplay();
    }

    public final <T> T getGameConfig() {
        return FXGL.getGameConfig();
    }

    /**
     * @return read only copy of game settings
     */
    public final ReadOnlyGameSettings getSettings() {
        return FXGL.getSettings();
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

//    public final NotificationService getNotificationService() {
//        return FXGL.getNotificationService();
//    }

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
