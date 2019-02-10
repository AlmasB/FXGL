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
