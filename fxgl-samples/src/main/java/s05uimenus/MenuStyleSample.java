/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;

//import com.almasb.fxgl.scene.menu.GTAVMenu;

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



        //settings.setSceneFactory(new SceneFactory());
    }

//    public static class MySceneFactory extends SceneFactory {
//
//        @NotNull
//        @Override
//        public FXGLMenu newMainMenu(@NotNull GameApplication app) {
//            return new GTAVMenu(app, MenuType.MAIN_MENU);
//        }
//
//        @NotNull
//        @Override
//        public FXGLMenu newGameMenu(@NotNull GameApplication app) {
//            return new GTAVMenu(app, MenuType.GAME_MENU);
//        }
//    }

    public static void main(String[] args) {
        launch(args);
    }
}
