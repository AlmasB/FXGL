/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.menu.MenuStyle;
import com.almasb.fxgl.settings.GameSettings;

/**
 * Shows how to set different menu styles.
 */
public class MenuStyleSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("MenuStyleSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);

        // 1. set menu enabled to true
        settings.setMenuEnabled(true);

        // 2. set menu style
        settings.setMenuStyle(MenuStyle.GTA5);

        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
