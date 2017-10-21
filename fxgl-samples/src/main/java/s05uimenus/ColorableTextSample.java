/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.FXGLTextFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Shows how to use JavaFX UI within FXGL.
 */
public class ColorableTextSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ColorableTextSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initUI() {
        FXGLTextFlow uiText = new FXGLTextFlow();
        uiText.append("Press ").append(KeyCode.V, Color.ORANGE).append(" to use ").append("Enhanced Vision", Color.ORANGE);

        FXGLTextFlow t2 = new FXGLTextFlow();
        t2.append("Discovered: ").append("The New Temple", Color.DARKCYAN);

        FXGLTextFlow t3 = new FXGLTextFlow();
        t3.append("Hold: ").append(MouseButton.PRIMARY, Color.ORANGE).append(" and ").append(MouseButton.SECONDARY, Color.ORANGE).append(" to shoot");

        VBox vbox = new VBox(10);
        vbox.setTranslateX(100);
        vbox.setTranslateY(100);
        vbox.getChildren().addAll(
                uiText,
                t2,
                t3
        );

        // 5. add UI object to scene
        getGameScene().addUINode(vbox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
