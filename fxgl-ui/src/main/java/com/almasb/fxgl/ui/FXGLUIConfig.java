/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.ui;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class FXGLUIConfig {

    private static UIFactory uiFactory;

    public static void setUIFactory(UIFactory uiFactory) {
        FXGLUIConfig.uiFactory = uiFactory;
    }

    public static UIFactory getUIFactory() {
        return uiFactory;
    }
}
