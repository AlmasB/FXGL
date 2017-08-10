/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.service.*;
import com.almasb.fxgl.service.impl.asset.FXGLAssetLoader;
import com.almasb.fxgl.event.EventBus;
import com.almasb.fxgl.service.impl.executor.FXGLExecutor;
import com.almasb.fxgl.service.impl.notification.FXGLNotificationService;
import com.almasb.fxgl.service.impl.pooler.FXGLPooler;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.MockUIFactory;
import com.google.inject.name.Names;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Module that binds services with their mock providers.
 * This is only needed for testing, as this allows us to test
 * each service in isolation (more or less).
 * Some services have actual "live" providers since they are required
 * to be functional by others, e.g. Pooler.
 * Almost all services are singleton only, so tests must reflect that.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MockApplicationModule extends ApplicationModule {

    private static MockApplicationModule instance;

    public static MockApplicationModule get() {
        if (instance == null) {
            Stage stage = mockStage();
            GameSettings settings = new GameSettings();

            GameApplication app = new MockGameApplication();
            app.initSettings(settings);

            app.injectStage(stage);
            app.injectSettings(settings.toReadOnly());

            instance = new MockApplicationModule(app);
        }

        return instance;
    }

    private MockApplicationModule(GameApplication app) {
        super(app);
    }

    private static Stage mockStage() {
        new Thread(() -> {
            Application.launch(MockApplication.class);
        }).start();

        try {
            MockApplication.Companion.getREADY().await();
        } catch (InterruptedException e) {
            System.out.println("Exception during mocking: " + e);
            e.printStackTrace();
            System.exit(-1);
        }

        return MockApplication.stage;
    }

    @Override
    protected void bindServices() {
        mockProperties();
        mockTimer();
        mockPooler();
        mockLoggerFactory();
        mockInput();
        mockExecutor();
        mockNotificationService();
        mockUIFactory();
        mockAssetLoader();
        mockPhysics();
    }

    /**
     * Supplies values for properties that would normally be loaded
     * from the system.properties file.
     */
    private void mockProperties() {
        FXGL.setProperty("dev.showbbox", false);
    }

    private void mockTimer() {
        //bind(MasterTimer.class).toInstance(MockMasterTimer.INSTANCE);
        //bind(StateTimer.class).to(StateTimerImpl.class);
        //bind(LocalTimer.class).to(FXGLLocalTimer.class);
    }

    private void mockPooler() {
        bind(Integer.class).annotatedWith(Names.named("pooling.initialSize")).toInstance(128);
        bind(Pooler.class).to(FXGLPooler.class);
    }

    private void mockLoggerFactory() {
        //bind(LoggerFactory.class).toInstance(MockLoggerFactory.INSTANCE);
    }

    private void mockInput() {
        //bind(Input.class).to(Input.class);
    }

    private void mockExecutor() {
        bind(Executor.class).to(FXGLExecutor.class);
    }

    private void mockNotificationService() {
        bind(NotificationService.class).to(FXGLNotificationService.class);
    }

    private void mockUIFactory() {
        bind(UIFactory.class).toInstance(MockUIFactory.INSTANCE);
    }

    private void mockAssetLoader() {
        bind(Integer.class).annotatedWith(Names.named("asset.cache.size")).toInstance(35);
        bind(AssetLoader.class).to(FXGLAssetLoader.class);
    }

    private void mockPhysics() {
        bind(Double.class).annotatedWith(Names.named("physics.ppm")).toInstance(50.0);
    }
}
