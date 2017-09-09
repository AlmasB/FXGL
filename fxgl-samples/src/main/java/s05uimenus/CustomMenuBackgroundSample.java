/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.scene.FXGLMenu;
import com.almasb.fxgl.scene.SceneFactory;
import com.almasb.fxgl.scene.menu.FXGLDefaultMenu;
import com.almasb.fxgl.scene.menu.MenuType;
import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.Node;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

/**
 * Shows how to use custom background in menus.
 */
public class CustomMenuBackgroundSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("BG Sample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(true);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);

        settings.setSceneFactory(new MySceneFactory());
    }

    public static class MySceneFactory extends SceneFactory {

        @NotNull
        @Override
        public FXGLMenu newMainMenu(@NotNull GameApplication app) {
            return new FXGLDefaultMenu(app, MenuType.MAIN_MENU) {
                @Override
                protected Node createBackground(double width, double height) {
                    return FXGL.getAssetLoader().loadTexture("custom_bg.png");
                }

                @Override
                protected Node createTitleView(String title) {
                    return new Text("");
                }
            };
        }

        // 4. override game menu

        @NotNull
        @Override
        public FXGLMenu newGameMenu(@NotNull GameApplication app) {
            return new FXGLDefaultMenu(app, MenuType.GAME_MENU) {
                @Override
                protected Node createBackground(double width, double height) {
                    return FXGL.getAssetLoader().loadTexture("custom_bg.png");
                }

                @Override
                protected Node createTitleView(String title) {
                    return new Text("");
                }
            };
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
