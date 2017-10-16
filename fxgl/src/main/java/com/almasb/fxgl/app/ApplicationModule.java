/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.settings.ReadOnlyGameSettings;

/**
 * Module that binds services with their providers.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ApplicationModule {

    private GameApplication app;
    private ReadOnlyGameSettings settings;

    ApplicationModule(GameApplication app) {
        this.app = app;
        settings = app.getSettings();
    }
//
    public GameApplication getApp() {
        return app;
    }
//
//    @Override
//    protected final void configure() {
//        // application is the first thing to get ready
//        bindApp();
//
//        bindServices();
//    }
//
//    private void bindApp() {
//        bind(GameApplication.class).toInstance(app);
//        bind(ReadOnlyGameSettings.class).toInstance(settings);
//        bind(ApplicationMode.class).toInstance(settings.getApplicationMode());
//
//        bind(Integer.class).annotatedWith(Names.named("appWidth")).toInstance(app.getWidth());
//        bind(Integer.class).annotatedWith(Names.named("appHeight")).toInstance(app.getHeight());
//    }
//
//    protected void bindServices() {
//        // no-op
//    }
}
