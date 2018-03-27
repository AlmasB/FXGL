/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.app;

import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.MockUIFactory;
import com.almasb.fxgl.util.Credits;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.Arrays;

/**
 * A test game app used to mock the user app.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MockGameApplication extends GameApplication {

    private static MockGameApplication INSTANCE;

    public static MockGameApplication get() {
        if (INSTANCE == null) {
            mockStage();

            GameSettings settings = new GameSettings();

            MockGameApplication app = new MockGameApplication();
            app.initSettings(settings);

            app.injectSettings(settings.toReadOnly());

            INSTANCE = app;
        }

        return INSTANCE;
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
    protected void initSettings(GameSettings settings) {
        settings.setWidth(500);
        settings.setHeight(500);
        settings.setTitle("Test");
        settings.setVersion("0.99");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setFullScreenAllowed(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setMenuKey(KeyCode.ENTER);
        settings.setCredits(new Credits(Arrays.asList("TestCredit1", "TestCredit2")));
        settings.setApplicationMode(ApplicationMode.RELEASE);

        // mock
        settings.setExceptionHandler(MockExceptionHandler.INSTANCE);
        settings.setUIFactory(MockUIFactory.INSTANCE);
    }

    @Override
    protected void initInput() {
    }

    @Override
    protected void initGame() {
    }

    @Override
    protected void initPhysics() {
    }

    @Override
    protected void initUI() {
    }

    @Override
    protected void onUpdate(double tpf) {
    }
}
