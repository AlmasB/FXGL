/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.app;

import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.saving.DataFile;
import com.almasb.sslogger.*;
import javafx.application.Application;
import javafx.stage.Stage;

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

            launch(instance, args);
        } catch (Exception e) {
            printErrorAndExit(e);
        }
    }

    public static void launch(Class<? extends GameApplication> appClass, String[] args) {
        try {
            var instance = ReflectionUtils.newInstance(appClass);

            launch(instance, args);
        } catch (Exception e) {
            printErrorAndExit(e);
        }
    }

    private static void launch(GameApplication app, String[] args) {
        // this will be set automatically by javafxports on mobile
        if (System.getProperty("javafx.platform") == null)
            System.setProperty("javafx.platform", "Desktop");

        var settings = app.takeUserSettings();

        app.initLogger(settings);

        FXGLApplication.launchFX(app, settings, args);
    }

    private static GameApplication newInstance() {
        var appClass = ReflectionUtils.getCallingClass(GameApplication.class, "launch");

        return ReflectionUtils.newInstance(appClass);
    }

    private static void printErrorAndExit(Exception e) {
        System.out.println("Error during launch:");
        e.printStackTrace();
        System.out.println("Application will now exit");
        System.exit(-1);
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

    public static final class FXGLApplication extends Application {

        private static GameApplication app;
        private static ReadOnlyGameSettings settings;

        /**
         * This is the main entry point as run by the JavaFX platform.
         */
        @Override
        public void start(Stage stage) {
            var engine = new Engine(app, settings, stage);

            FXGL.engine = engine;

            engine.startLoop();
        }

        static void launchFX(GameApplication app, ReadOnlyGameSettings settings, String[] args) {
            FXGLApplication.app = app;
            FXGLApplication.settings = settings;
            launch(args);
        }
    }
}
