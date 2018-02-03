/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;

/**
 * Shows how to enable intro/menus and menu items.
 */
public class MenuSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("MenuSample");
        settings.setVersion("0.1");

        // 1. set intro enabled to true
        settings.setIntroEnabled(true);

        // 2. set menu enabled to true
        settings.setMenuEnabled(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
