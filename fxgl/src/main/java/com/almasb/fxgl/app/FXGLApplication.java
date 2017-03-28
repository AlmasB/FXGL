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

import com.almasb.fxgl.core.concurrent.Async;
import com.almasb.fxgl.core.logging.FXGLLogger;
import com.almasb.fxgl.core.logging.Logger;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.scene.PreloadingScene;
import com.almasb.fxgl.service.*;
import com.almasb.fxgl.service.listener.FXGLListener;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.util.Version;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

/**
 * General FXGL application that configures services, settings and properties
 * to be used by the framework.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class FXGLApplication extends Application {

    /**
     * Use system logger fallback until actual logger is ready.
     */
    protected static Logger log = FXGLLogger.getSystemLogger();

    private Stage primaryStage;

    /**
     * Used by mocking.
     *
     * @param stage mock stage
     */
    void injectStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * @return primary stage as set by JavaFX
     */
    public final Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Internal copy of settings, so that they are not modified during game.
     */
    private ReadOnlyGameSettings settings;

    /**
     * Used by mocking.
     *
     * @param settings mock settings
     */
    void injectSettings(ReadOnlyGameSettings settings) {
        this.settings = settings;
    }

    /**
     * @return read only copy of game settings
     */
    public final ReadOnlyGameSettings getSettings() {
        return settings;
    }

    private List<FXGLListener> systemListeners = new ArrayList<>();

    /**
     * Add listener for core FXGL callbacks.
     *
     * @param listener the listener
     */
    public final void addFXGLListener(FXGLListener listener) {
        systemListeners.add(listener);
    }

    /**
     * Remove previously added listener.
     *
     * @param listener the listener
     */
    public final void removeFXGLListener(FXGLListener listener) {
        systemListeners.remove(listener);
    }

    void runTask(Class<? extends Runnable> type) {
        log.debug("Running task: " + type.getSimpleName());
        FXGL.getInstance(type).run();
    }

    @Override
    public final void init() throws Exception {
        Version.print();
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showPreloadingStage();

        // this is the only JavaFX scene in use with a placeholder root
        // the root will be replaced with a relevant FXGL scene
        primaryStage.setScene(new Scene(new Pane()));

        startFXGL();
    }

    private void startFXGL() {
        new Thread(() -> {
            try {
                configureFXGL();

                runUpdaterAndWait();

                configureApp();
            } catch (Exception e) {
                log.fatal("Exception during system configuration:");
                log.fatal(FXGLLogger.errorTraceAsString(e));
                log.fatal("System will now exit");
                log.close();

                // we don't know what exactly has been initialized
                // so to avoid the process hanging just shut down the JVM
                System.exit(-1);
            }

            Platform.runLater(primaryStage::show);
        }, "FXGL Launcher Thread").start();
    }

    private void runUpdaterAndWait() throws Exception {
        // TODO: this _maybe_ could run with Async.startFX()
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            runTask(UpdaterTask.class);
            latch.countDown();
        });

        latch.await();
    }

    @Override
    public final void stop() {
        log.debug("Exiting FXGL");
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

    /**
     * After this call all FXGL.* calls are valid.
     */
    private void configureFXGL() {
        long start = System.nanoTime();

        initSystemProperties();
        initUserProperties();
        initAppSettings();
        asyncInitLogger();

        FXGL.configure(new ApplicationModule((GameApplication) this));

        log.debug("FXGL configuration complete");

        log.infof("FXGL configuration took:  %.3f sec", (System.nanoTime() - start) / 1000000000.0);
    }

    /**
     * Configure the actual application that uses FXGL.
     * Called after the FXGL has been configured.
     */
    abstract void configureApp();

    /**
     * Pause the application.
     */
    protected final void pause() {
        log.debug("Pausing main loop");
        systemListeners.forEach(FXGLListener::onPause);
        getEventBus().fireEvent(FXGLEvent.pause());
    }

    /**
     * Resume the application.
     */
    protected final void resume() {
        log.debug("Resuming main loop");
        systemListeners.forEach(FXGLListener::onResume);
        getEventBus().fireEvent(FXGLEvent.resume());
    }

    /**
     * Reset the application.
     * After notifying all interested parties (where all should do a cleanup),
     * <code>System.gc()</code> will be called.
     */
    protected final void reset() {
        log.debug("Resetting FXGL application");
        systemListeners.forEach(FXGLListener::onReset);
        getEventBus().fireEvent(FXGLEvent.reset());

        System.gc();
    }

    /**
     * Exit the application.
     * Safe to call this from a paused state.
     */
    protected final void exit() {
        log.debug("Exiting Normally");
        systemListeners.forEach(FXGLListener::onExit);
        getEventBus().fireEvent(FXGLEvent.exit());

        FXGL.destroy();
        stop();
        log.close();

        Platform.exit();
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

    private void asyncInitLogger() {
        Async.start(() -> {
            String resourceName = "log4j2-debug.xml";

            switch (getSettings().getApplicationMode()) {
                case DEBUG:
                    resourceName = "log4j2-debug.xml";
                    break;
                case DEVELOPER:
                    resourceName = "log4j2-devel.xml";
                    break;
                case RELEASE:
                    resourceName = "log4j2-release.xml";
                    break;
            }

            FXGLLogger.configure(FXGLApplication.class.getResource(resourceName).toExternalForm());

            log = FXGLLogger.get(FXGLApplication.class);
            log.debug("FXGLLogger configuration complete");
            log.debug("Logging game settings\n" + settings.toString());
        });
    }

    /**
     * Initialize app settings.
     *
     * @param settings app settings
     */
    protected abstract void initSettings(GameSettings settings);

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
        return getMasterTimer().getTick();
    }

    /**
     * @return current time since start of game in nanoseconds
     */
    public final long getNow() {
        return getMasterTimer().getNow();
    }

    /**
     * @return event bus
     */
    public final EventBus getEventBus() {
        return FXGL.getEventBus();
    }

    /**
     * @return display service
     */
    public final Display getDisplay() {
        return FXGL.getDisplay();
    }

    /**
     * @return input service
     */
    public final Input getInput() {
        return FXGL.getInput();
    }

    /**
     * @return audio player
     */
    public final AudioPlayer getAudioPlayer() {
        return FXGL.getAudioPlayer();
    }

    /**
     * @return asset loader
     */
    public final AssetLoader getAssetLoader() {
        return FXGL.getAssetLoader();
    }

    /**
     * @return master timer
     */
    public final MasterTimer getMasterTimer() {
        return FXGL.getMasterTimer();
    }

    /**
     * @return executor service
     */
    public final Executor getExecutor() {
        return FXGL.getExecutor();
    }

    /**
     * @return notification service
     */
    public final NotificationService getNotificationService() {
        return FXGL.getNotificationService();
    }

    /**
     * @return achievement manager
     */
    public final AchievementManager getAchievementManager() {
        return FXGL.getAchievementManager();
    }

    /**
     * @return QTE service
     */
    public final QTE getQTE() {
        return FXGL.getQTE();
    }

    /**
     * @return net service
     */
    public final Net getNet() {
        return FXGL.getNet();
    }

    /**
     * @return exception handler service
     */
    public final ExceptionHandler getExceptionHandler() {
        return FXGL.getExceptionHandler();
    }

    /**
     * @return UI factory service
     */
    public final UIFactory getUIFactory() {
        return FXGL.getUIFactory();
    }

    /**
     * @return quest manager
     */
    public final QuestService getQuestService() {
        return FXGL.getQuestManager();
    }
}
