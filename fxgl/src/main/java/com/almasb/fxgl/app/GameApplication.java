/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.app;

import com.almasb.fxgl.core.EngineService;
import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.core.concurrent.IOTask;
import com.almasb.fxgl.core.reflect.ReflectionUtils;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.core.util.Platform;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.generated.BuildProperties;
import com.almasb.fxgl.profile.DataFile;
import com.almasb.fxgl.profile.SaveLoadHandler;
import com.almasb.fxgl.ui.FontType;
import com.almasb.sslogger.*;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

import java.util.HashMap;
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

    public static void customLaunch(GameApplication app, Stage stage) {
        try {
            var settings = app.takeUserSettings();

            app.initLogger(settings);

            FXGLApplication.customLaunchFX(app, settings, stage);

        } catch (Exception e) {
            printErrorAndExit(e);
        }
    }

    private static void launch(GameApplication app, String[] args) {
        var settings = app.takeUserSettings();

        app.initLogger(settings);

        // this _should_ be a workaround for the JavaFX bug on linux discussed at https://github.com/AlmasB/FXGL/issues/579
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

        var platform = Platform.get();

        // if user set platform as browser, we keep it that way
        if (localSettings.getRuntimeInfo().getPlatform().isBrowser()) {
            platform = Platform.BROWSER;
        }

        var runtimeInfo = new RuntimeInfo(platform, BuildProperties.VERSION, BuildProperties.BUILD);
        localSettings.setRuntimeInfo(runtimeInfo);
        localSettings.setExperimentalNative(localSettings.isExperimentalNative() || platform.isMobile());
        return localSettings.toReadOnly();
    }

    private void initLogger(ReadOnlyGameSettings settings) {
        Logger.configure(new LoggerConfig());
        // we write all logs to file but adjust console log level based on app mode
        if (settings.isDesktop() && !settings.isExperimentalNative()) {
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

    public static final class FXGLApplication extends Application {

        public static GameApplication app;
        private static ReadOnlyGameSettings settings;

        private static Engine engine;
        private MainWindow mainWindow;

        /**
         * This is the main entry point as run by the JavaFX platform.
         */
        @Override
        public void start(Stage stage) {
            // any exception on the JavaFX thread will be caught and reported
            Thread.setDefaultUncaughtExceptionHandler((thread, e) -> handleFatalError(e));

            log.debug("Initializing FXGL");

            engine = new Engine(settings);

            // after this call, all FXGL.* calls (apart from those accessing services) are valid
            FXGL.inject$fxgl(engine, app, this);

            var startupScene = settings.getSceneFactory().newStartup();

            // get window up ASAP
            mainWindow = new MainWindow(stage, startupScene, settings);
            mainWindow.show();

            // TODO: possibly a better way exists of doing below
            engine.getEnvironmentVars$fxgl().put("settings", settings);
            engine.getEnvironmentVars$fxgl().put("mainWindow", mainWindow);

            // start initialization of services on a background thread
            // then start the loop on the JavaFX thread
            var task = IOTask.ofVoid(() -> {
                            engine.initServices();
                            postServicesInit();
                        })
                        .onSuccess(n -> engine.startLoop())
                        .onFailure(e -> handleFatalError(e))
                        .toJavaFXTask();

            Async.INSTANCE.execute(task);
        }

        private void postServicesInit() {
            initPauseResumeHandler();
            initSaveLoadHandler();
            initAndLoadLocalization();
            initAndRegisterFontFactories();

            // onGameUpdate is only updated in Game Scene
            FXGL.getGameScene().addListener(tpf -> engine.onGameUpdate(tpf));
        }

        private void initPauseResumeHandler() {
            if (!settings.isMobile()) {
                mainWindow.iconifiedProperty().addListener((obs, o, isMinimized) -> {
                    if (isMinimized) {
                        engine.pauseLoop();
                    } else {
                        engine.resumeLoop();
                    }
                });
            }
        }

        private void initSaveLoadHandler() {
            FXGL.getSaveLoadService().addHandler(new SaveLoadHandler() {
                @Override
                public void onSave(DataFile data) {
                    var bundle = new Bundle("FXGLServices");
                    engine.write(bundle);
                }

                @Override
                public void onLoad(DataFile data) {
                    var bundle = data.getBundle("FXGLServices");
                    engine.read(bundle);
                }
            });
        }

        private void initAndLoadLocalization() {
            log.debug("Loading localizations");

            settings.getSupportedLanguages().forEach(lang -> {
                var pMap = FXGL.getAssetLoader().loadPropertyMap("languages/" + lang.getName().toLowerCase() + ".lang");
                FXGL.getLocalizationService().addLanguageData(lang, pMap.toStringMap());
            });

            FXGL.getLocalizationService().selectedLanguageProperty().bind(settings.getLanguage());
        }

        private void initAndRegisterFontFactories() {
            log.debug("Registering font factories with UI factory");

            var uiFactory = FXGL.getUIFactoryService();

            uiFactory.registerFontFactory(FontType.UI, FXGL.getAssetLoader().loadFont(settings.getFontUI()));
            uiFactory.registerFontFactory(FontType.GAME, FXGL.getAssetLoader().loadFont(settings.getFontGame()));
            uiFactory.registerFontFactory(FontType.MONO, FXGL.getAssetLoader().loadFont(settings.getFontMono()));
            uiFactory.registerFontFactory(FontType.TEXT, FXGL.getAssetLoader().loadFont(settings.getFontText()));
        }

        private boolean isError = false;

        private void handleFatalError(Throwable error) {
            if (isError) {
                // just ignore to avoid spamming dialogs
                return;
            }

            isError = true;

            // stop main loop from running as we cannot continue
            engine.stopLoop();

            log.fatal("Uncaught Exception:", error);
            log.fatal("Application will now exit");

            if (mainWindow != null) {
                mainWindow.showFatalError(error, this::exitFXGL);
            } else {
                exitFXGL();
            }
        }

        public void exitFXGL() {
            log.debug("Exiting FXGL");

            if (engine != null && !isError)
                engine.stopLoopAndExitServices();

            log.debug("Shutting down background threads");
            Async.INSTANCE.shutdownNow();

            log.debug("Closing logger and exiting JavaFX");
            Logger.close();
            javafx.application.Platform.exit();
        }

        static void launchFX(GameApplication app, ReadOnlyGameSettings settings, String[] args) {
            FXGLApplication.app = app;
            FXGLApplication.settings = settings;
            launch(args);
        }

        static void customLaunchFX(GameApplication app, ReadOnlyGameSettings settings, Stage stage) {
            FXGLApplication.app = app;
            FXGLApplication.settings = settings;
            new FXGLApplication().start(stage);
        }
    }

    public static class GameApplicationService extends EngineService {

        private GameApplication app = FXGLApplication.app;

        @Override
        public void onMainLoopStarting() {
            // these things need to be called early before the main loop
            // so that menus can correctly display input controls, etc.
            app.initInput();
            app.onPreInit();
        }

        @Override
        public void onGameUpdate(double tpf) {
            app.onUpdate(tpf);
        }
    }

    /**
     *  * Clears previous game.
     *  * Initializes game, physics and UI.
     *  * This task is rerun every time the game application is restarted.
     */
    public static class InitAppTask extends Task<Void> {

        private GameApplication app = FXGLApplication.app;

        @Override
        protected Void call() throws Exception {
            var start = System.nanoTime();

            log.debug("Initializing game");
            updateMessage("Initializing game");

            initGame();
            app.initPhysics();
            app.initUI();

            FXGLApplication.engine.onGameReady(FXGL.getWorldProperties());

            log.infof("Game initialization took: %.3f sec", (System.nanoTime() - start) / 1000000000.0);

            return null;
        }

        private void initGame() {
            var vars = new HashMap<String, Object>();
            app.initGameVars(vars);

            vars.forEach((name, value) ->
                    FXGL.getWorldProperties().setValue(name, value)
            );

            app.initGame();
        }

        @Override
        protected void failed() {
            throw new RuntimeException("Initialization failed", getException());
        }
    }
}
