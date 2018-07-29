/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.extra.scene.menu.GTAVMenu;
import com.almasb.fxgl.extra.scene.menu.Warcraft3Menu;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.settings.GameSettings;

/**
 * Shows how to set different menu styles.
 */
public class MenuStyleSample extends GameApplication {

    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("MenuStyleSample");
        settings.setVersion("0.1");
        settings.setMenuEnabled(true);
        settings.setSceneFactory(new MySceneFactory());
    }

    public static class MySceneFactory extends SceneFactory {

        @Override
        public FXGLMenu newMainMenu(GameApplication app) {
            return new Warcraft3Menu(app, MenuType.MAIN_MENU);
        }

        @Override
        public FXGLMenu newGameMenu(GameApplication app) {
            return new GTAVMenu(app, MenuType.GAME_MENU);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
