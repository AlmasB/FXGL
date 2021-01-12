/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.StartupScene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Shows how to use custom StartupScene to replace the FXGL default.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CustomStartupSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public StartupScene newStartup(int width, int height) {
                return new MyStartupScene(width, height);
            }
        });
    }

    public static class MyStartupScene extends StartupScene {

        // Note: in startup scene no services are ready, so don't call FXGL.*
        public MyStartupScene(int appWidth, int appHeight) {
            super(appWidth, appHeight);

            Rectangle bg = new Rectangle(appWidth, appHeight);

            Text textCompanyName = new Text("Company Name");
            textCompanyName.setFill(Color.WHITE);
            textCompanyName.setFont(Font.font(64));

            getContentRoot().getChildren().addAll(new StackPane(bg, textCompanyName));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
