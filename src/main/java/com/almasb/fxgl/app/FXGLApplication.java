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

import com.almasb.fxgl.asset.AssetLoader;
import com.almasb.fxgl.asset.SaveLoadManager;
import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.scene.Display;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.settings.ReadOnlyGameSettings;
import com.almasb.fxgl.time.MasterTimer;
import com.almasb.fxgl.util.ExceptionHandler;
import com.almasb.fxgl.util.FXGLCheckedExceptionHandler;
import com.almasb.fxgl.util.FXGLLogger;
import com.almasb.fxgl.util.Version;
import com.google.inject.*;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
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
     * Set handler for checked exceptions
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

    private static Injector injector;

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
    }

    @Override
    public final void init() throws Exception {
        log.finer("FXGL_init()");
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.finer("FXGL_start()");

        GameSettings localSettings = new GameSettings();
        initSettings(localSettings);
        settings = localSettings.toReadOnly();

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
        log.info("Application Mode: " + getSettings().getApplicationMode());

        configureServices(stage);
    }

    @Override
    public final void stop() throws Exception {
        log.finer("FXGL_stop()");
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
    private SaveLoadManager saveLoadManager;

    /**
     * @return save load manager
     */
    public final SaveLoadManager getSaveLoadManager() {
        return saveLoadManager;
    }

    @Inject
    private MasterTimer masterTimer;

    /**
     * @return master timer
     */
    public final MasterTimer getMasterTimer() {
        return masterTimer;
    }
}
