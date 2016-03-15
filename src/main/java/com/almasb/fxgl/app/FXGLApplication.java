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
import com.almasb.fxgl.gameplay.AchievementManager;
import com.almasb.fxgl.gameplay.NotificationService;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.logging.FXGLLogger;
import com.almasb.fxgl.scene.Display;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.time.MasterTimer;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.FXGLCheckedExceptionHandler;
import com.almasb.fxgl.util.Version;
import com.google.inject.*;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * General FXGL application that configures services for all parts to use.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class FXGLApplication extends Application {

    static {
        FXGLLogger.init(Level.CONFIG);
        Version.print();
        setDefaultCheckedExceptionHandler(new FXGLCheckedExceptionHandler());
    }

    /**
     * The logger
     */
    protected static final Logger log = FXGLLogger.getLogger("FXGL.Application");

    private static ExceptionHandler defaultCheckedExceptionHandler;

    /**
     * @return handler for checked exceptions
     */
    public static ExceptionHandler getDefaultCheckedExceptionHandler() {
        return defaultCheckedExceptionHandler;
    }

    /**
     * Set handler for checked exceptions.
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
     * Dependency injector.
     */
    private static Injector injector;

    /**
     * Obtain an instance of a service.
     * It may be expensive to use this in a loop.
     * Store a reference to the instance instead.
     *
     * @param type service type
     * @param <T> type
     * @return service
     */
    public static final <T> T getService(ServiceType<T> type) {
        return injector.getInstance(type.service());
    }

    @SuppressWarnings("unchecked")
    private void configureServices(Stage stage) {
        injector = Guice.createInjector(new AbstractModule() {
            private Scene scene = new Scene(new Pane());

            @Override
            protected void configure() {
                bind(Double.class)
                        .annotatedWith(Names.named("appWidth"))
                        .toInstance((double)getSettings().getWidth());

                bind(Double.class)
                        .annotatedWith(Names.named("appHeight"))
                        .toInstance((double)getSettings().getHeight());

                bind(Integer.class)
                        .annotatedWith(Names.named("asset.cache.size"))
                        .toInstance(FXGL.getInt("asset.cache.size"));

                bind(ReadOnlyGameSettings.class).toInstance(getSettings());

                for (Field field : ServiceType.class.getDeclaredFields()) {
                    try {
                        ServiceType type = (ServiceType) field.get(null);
                        if (type.service().equals(type.serviceProvider()))
                            bind(type.serviceProvider());
                        else
                            bind(type.service()).to(type.serviceProvider());
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException("Failed to configure services: " + e.getMessage());
                    }
                }
            }

            @Provides
            Scene primaryScene() {
                return scene;
            }

            @Provides
            Stage primaryStage() {
                return stage;
            }
        });

        log.finer("Services configuration complete");

        injector.injectMembers(this);

        achievementManager = new AchievementManager();
    }

    @Override
    public final void init() throws Exception {
        log.finer("FXGL_init()");
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.finer("FXGL_start()");

        initSystemProperties();
        initUserProperties();

        initAppSettings();
        initLogger();

        configureServices(stage);
    }

    @Override
    public final void stop() throws Exception {
        log.finer("FXGL_stop()");
    }

    /**
     * Load FXGL system properties.
     */
    private void initSystemProperties() {
        log.finer("Initializing system properties");

        ResourceBundle props = ResourceBundle.getBundle("com.almasb.fxgl.app.system");
        props.keySet().forEach(key -> {
            Object value = props.getObject(key);
            FXGL.setProperty(key, value);

            log.finer(key + " = " + value);
        });
    }

    /**
     * Load user defined properties to override FXGL system properties.
     */
    private void initUserProperties() {
        log.finer("Initializing user properties");

        // services are not ready yet, so load manually
        try (InputStream is = getClass().getResource("/assets/properties/system.properties").openStream()) {
            ResourceBundle props = new PropertyResourceBundle(is);
            props.keySet().forEach(key -> {
                Object value = props.getObject(key);
                FXGL.setProperty(key, value);

                log.finer(key + " = " + value);
            });
        } catch (NullPointerException npe) {
            log.info("User properties not found. Using system");
        } catch (IOException e) {
            log.warning("Loading user properties failed: " + e.getMessage());
        }
    }

    /**
     * Take app settings from user.
     */
    private void initAppSettings() {
        GameSettings localSettings = new GameSettings();
        initSettings(localSettings);
        settings = localSettings.toReadOnly();

        FXGL.setSettings(settings);
    }

    /**
     * Init logging system based on app settings.
     */
    private void initLogger() {
        Level logLevel = Level.ALL;
        switch (getSettings().getApplicationMode()) {
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
        log.info("Application Mode: " + getSettings().getApplicationMode());
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

    @Inject
    private EventBus eventBus;

    /**
     * @return event bus
     */
    public final EventBus getEventBus() {
        return eventBus;
    }

    @Inject
    private Display display;

    /**
     * @return display service
     */
    public final Display getDisplay() {
        return display;
    }

    @Inject
    private Input input;

    /**
     * @return input service
     */
    public final Input getInput() {
        return input;
    }

    @Inject
    private AudioPlayer audioPlayer;

    /**
     * @return audio player
     */
    public final AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    @Inject
    private AssetLoader assetLoader;

    /**
     * @return asset loader
     */
    public final AssetLoader getAssetLoader() {
        return assetLoader;
    }

    @Inject
    private MasterTimer masterTimer;

    /**
     * @return master timer
     */
    public final MasterTimer getMasterTimer() {
        return masterTimer;
    }

    @Inject
    private NotificationService notificationService;

    /**
     * @return notification service
     */
    public final NotificationService getNotificationService() {
        return notificationService;
    }

    private AchievementManager achievementManager;

    /**
     * @return achievement manager
     */
    public final AchievementManager getAchievementManager() {
        return achievementManager;
    }
}
