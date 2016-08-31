/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import com.almasb.fxeventbus.EventBus;
import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.event.FXGLEvent;
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.gameplay.NotificationService;
import com.almasb.fxgl.gameplay.qte.QTE;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.logging.SystemLogger;
import com.almasb.fxgl.net.Net;
import com.almasb.fxgl.scene.Display;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.time.MasterTimer;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.Version;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

/**
 * General FXGL application that configures services for all parts to use.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class FXGLApplication extends Application {

    /**
     * We use system logger because logger service is not yet ready.
     */
    private static final Logger log = SystemLogger.INSTANCE;

    static {
        Version.print();
    }

    private List<FXGLListener> systemListeners = new ArrayList<>();

    /**
     * Add listener for core FXGL callbacks.
     *
     * @param listener the listener
     */
    public void addFXGLListener(FXGLListener listener) {
        systemListeners.add(listener);
    }

    /**
     * Remove previously added listener.
     *
     * @param listener the listener
     */
    public void removeFXGLListener(FXGLListener listener) {
        systemListeners.remove(listener);
    }

    void runTask(Class<? extends Runnable> type) {
        log.debug("Running task: " + type.getSimpleName());
        FXGL.getInstance(type).run();
    }

    @Override
    public final void init() throws Exception {
        log.debug("Initializing FXGL");
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.debug("Starting FXGL");

        long start = System.nanoTime();

        initSystemProperties();
        initUserProperties();
        initAppSettings();

        FXGL.configure(this, stage);

        log.debug("FXGL configuration complete");

        log.infof("FXGL configuration took:    %.3f sec", (System.nanoTime() - start) / 1000000000.0);

        runTask(UpdaterTask.class);
    }

    @Override
    public final void stop() {
        log.debug("Exiting FXGL");
    }

    /**
     * Pause the application.
     */
    protected void pause() {
        log.debug("Pausing main loop");
        systemListeners.forEach(FXGLListener::onPause);
        getEventBus().fireEvent(FXGLEvent.pause());
    }

    /**
     * Resume the application.
     */
    protected void resume() {
        log.debug("Resuming main loop");
        systemListeners.forEach(FXGLListener::onResume);
        getEventBus().fireEvent(FXGLEvent.resume());
    }

    /**
     * Reset the application.
     * After notifying all interested parties (where all should do a cleanup),
     * <code>System.gc()</code> will be called.
     */
    protected void reset() {
        log.debug("Resetting FXGL application");
        systemListeners.forEach(FXGLListener::onReset);
        getEventBus().fireEvent(FXGLEvent.reset());

        System.gc();
    }

    /**
     * Exit the application.
     * Safe to call this from a paused state.
     */
    protected void exit() {
        log.debug("Exiting Normally");
        systemListeners.forEach(FXGLListener::onExit);
        getEventBus().fireEvent(FXGLEvent.exit());

        FXGL.destroy();
        stop();
        log.close();

        Platform.exit();
        System.exit(0);
    }

    /**
     * Load FXGL system properties.
     */
    private void initSystemProperties() {
        log.debug("Initializing system properties");

        ResourceBundle props = ResourceBundle.getBundle("com.almasb.fxgl.app.system");
        props.keySet().forEach(key -> {
            Object value = props.getObject(key);
            FXGL.setProperty(key, value);

            log.debug(key + " = " + value);
        });
    }

    /**
     * Load user defined properties to override FXGL system properties.
     */
    private void initUserProperties() {
        log.debug("Initializing user properties");

        // services are not ready yet, so load manually
        try (InputStream is = getClass().getResource("/assets/properties/system.properties").openStream()) {
            ResourceBundle props = new PropertyResourceBundle(is);
            props.keySet().forEach(key -> {
                Object value = props.getObject(key);
                FXGL.setProperty(key, value);

                log.debug(key + " = " + value);
            });
        } catch (NullPointerException npe) {
            log.info("User properties not found. Using system");
        } catch (IOException e) {
            log.warning("Loading user properties failed: " + e);
        }
    }

    /**
     * Take app settings from user.
     */
    private void initAppSettings() {
        log.debug("Initializing app settings");

        GameSettings localSettings = new GameSettings();
        initSettings(localSettings);
        settings = localSettings.toReadOnly();

        log.debug("Logging settings\n" + settings.toString());
    }

    /**
     * Settings for this game instance. This is an internal copy
     * of the settings so that they will not be modified during game lifetime.
     */
    private ReadOnlyGameSettings settings;

    /**
     * @return read only copy of game settings
     */
    public final ReadOnlyGameSettings getSettings() {
        return settings;
    }

    /**
     * Initialize app settings.
     *
     * @param settings app settings
     */
    protected abstract void initSettings(GameSettings settings);

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
}
