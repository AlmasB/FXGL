/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.FXGLButton;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GameApplication.customLaunch(new GameApp(), stage);
    }

    public static void main(String[] args) {
        System.setProperty("os.target", "ios");
        System.setProperty("os.name", "iOS");
        System.setProperty("glass.platform", "ios");
        System.setProperty("targetos.name", "iOS");

        launch(args);
    }

    public static class GameApp extends GameApplication {
        @Override
        protected void initSettings(GameSettings settings) {
        }

        @Override
        protected void initGame() {
            FXGL.entityBuilder()
                    .at(200, 200)
                    .view(new Circle(15, Color.BLUE))
                    .buildAndAttach();
        }

        @Override
        protected void initUI() {
            Button btn = new FXGLButton("HELLO");
            
            btn.setOnAction(e -> {
                FXGL.getGameWorld().getEntities().get(0).translate(5, 5);
            });

            FXGL.addUINode(btn, 100, 100);
        }
    }
}
