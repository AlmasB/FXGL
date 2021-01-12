/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use FXGL within JavaFX.
 * Though using JavaFX within FXGL (i.e. extends GameApplication) is the recommended way.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class JavaFXAppWithFXGLSample extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        GameApplication.customLaunch(new GameApp(), stage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class GameApp extends GameApplication {
        @Override
        protected void initSettings(GameSettings settings) { }

        @Override
        protected void initGame() {
            entityBuilder()
                    .at(200, 200)
                    .view(new Circle(15, Color.BLUE))
                    .buildAndAttach();
        }

        @Override
        protected void initUI() {
            Button btn = getUIFactoryService().newButton("Hello!");
            
            btn.setOnAction(e -> {
                getGameWorld().getEntities().get(0).translate(5, 5);
            });

            addUINode(btn, 100, 100);
        }
    }
}
