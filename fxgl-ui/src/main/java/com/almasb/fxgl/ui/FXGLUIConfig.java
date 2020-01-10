/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

import com.almasb.fxgl.localization.LocalizationService;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class FXGLUIConfig {

    private static UIFactoryService uiFactory;
    private static LocalizationService localizationService;

    public static void setUIFactory(UIFactoryService uiFactory) {
        FXGLUIConfig.uiFactory = uiFactory;
    }

    public static UIFactoryService getUIFactory() {
        return uiFactory;
    }

    public static LocalizationService getLocalizationService() {
        return localizationService;
    }

    public static void setLocalizationService(LocalizationService localizationService) {
        FXGLUIConfig.localizationService = localizationService;
    }
}
