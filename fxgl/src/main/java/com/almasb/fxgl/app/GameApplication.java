/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.app;

import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.core.util.Platform;
import com.almasb.fxgl.dev.profiling.ProfilerService;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.generated.BuildProperties;
import com.almasb.fxgl.logging.*;

import java.util.Map;

/**
 * To use FXGL, extend this class and implement necessary methods.
 * The initialization process can be seen below (irrelevant phases are omitted):
 *
 * <ol>
 * <li>Instance fields of YOUR subclass of GameApplication</li>
 * <li>initSettings()</li>
 * <li>Services configuration (after this you can safely call any FXGL.* methods)</li>
 * Executed on JavaFX UI thread:
 * <li>initInput()</li>
 * <li>onPreInit()</li>
 * NOT executed on JavaFX UI thread:
 * <li>initGameVars()</li>
 * <li>initGame()</li>
 * <li>initPhysics()</li>
 * <li>initUI()</li>
 * Start of main game loop execution on JavaFX UI thread
 * </ol>
 *
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

    /**
     * The caller of GameApplication.launch() must be a method in the same class that extends GameApplication.
     */
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

    public static FXGLPane embeddedLaunch(GameApplication app) {
        try {
            var settings = app.takeUserSettings();

            app.initLogger(settings);

            return FXGLApplication.embeddedLaunchFX(app, settings);

        } catch (Exception e) {
            printErrorAndExit(e);
        }

        return null;
    }

    private static void launch(GameApplication app, String[] args) {
        var settings = app.takeUserSettings();

        app.initLogger(settings);

        // this is a workaround for the JavaFX bug on linux discussed at https://github.com/AlmasB/FXGL/issues/579
        if (settings.isLinux()) {
            System.setProperty("quantum.multithreaded", "false");
        }

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

        if (localSettings.isProfilingEnabled()) {
            localSettings.getEngineServices().add(ProfilerService.class);
        }

        var platform = Platform.get();

        // if user set platform as browser, we keep it that way
        if (localSettings.getRuntimeInfo().getPlatform().isBrowser()) {
            platform = Platform.BROWSER;
        }

        var runtimeInfo = new RuntimeInfo(platform, BuildProperties.VERSION, BuildProperties.BUILD);
        localSettings.setRuntimeInfo(runtimeInfo);
        localSettings.setNative(localSettings.isNative() || platform.isMobile());
        return localSettings.toReadOnly(getClass());
    }

    private void initLogger(ReadOnlyGameSettings settings) {
        // we write all logs to file but adjust console log level based on app mode
        if (settings.isFileSystemWriteAllowed() && settings.isDesktop() && !settings.isNative()) {
            Logger.addOutput(new FileOutput("FXGL"), LoggerLevel.DEBUG);
        }
        Logger.addOutput(new ConsoleOutput(), settings.getApplicationMode().getLoggerLevel());

        Logger.configure(new LoggerConfig());

        log.debug("Logging settings\n" + settings);
    }

    /**
     * Shuts down currently running embedded FXGL instance.
     * No-op if no FXGL instance is launched in embedded mode.
     * After this call, another {@link #embeddedLaunch(GameApplication)} can be started.
     * Note that after FXGL is no longer needed (no launch calls will be made),
     * FXGL.getGameController().exit() should be called.
     */
    public static void embeddedShutdown() {
        FXGL.extract$fxgl();

        Logger.removeAllOutputs();
    }

    /**
     * Initialize app settings.
     */
    protected abstract void initSettings(GameSettings settings);

    /**
     * Called once per application lifetime, just before initGame().
     */
    protected void onPreInit() {}

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
     * Called once per application lifetime, after all engine services exited
     * and before FXGL is about to shutdown.
     */
    protected void onExit() {}
}
