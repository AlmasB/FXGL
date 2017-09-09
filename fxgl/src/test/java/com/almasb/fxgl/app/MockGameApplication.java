/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.app;

import com.almasb.fxgl.service.impl.notification.FXGLNotificationService;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.MockUIFactory;
import com.almasb.fxgl.util.Credits;
import javafx.scene.input.KeyCode;

import java.util.Arrays;

/**
 * A test game app used to mock the user app.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MockGameApplication extends GameApplication {

    public static MockGameApplication INSTANCE;

    public MockGameApplication() {
        if (INSTANCE != null)
            throw new IllegalStateException("INSTANCE != null");

        INSTANCE = this;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(500);
        settings.setHeight(500);
        settings.setTitle("Test");
        settings.setVersion("0.99");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setFullScreen(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setMenuKey(KeyCode.ENTER);
        settings.setCredits(new Credits(Arrays.asList("TestCredit1", "TestCredit2")));
        settings.setApplicationMode(ApplicationMode.RELEASE);

        // mock
        settings.setNotificationService(new FXGLNotificationService());
        settings.setExceptionHandler(MockExceptionHandler.INSTANCE);
        settings.setUIFactory(MockUIFactory.INSTANCE);
    }

    @Override
    protected void initInput() {
    }

    @Override
    protected void initAssets() {
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
