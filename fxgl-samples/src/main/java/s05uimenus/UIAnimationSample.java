/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Shows how to use JavaFX UI within FXGL.
 */
public class UIAnimationSample extends GameApplication {

    // 1. declare JavaFX or FXGL UI object
    private Text uiText;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("UIAnimationSample");
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
        // 2. initialize the object
        uiText = getUIFactory().newText("Hello FXGL", Color.BLACK, 24.0);

        // 3. position the object
        uiText.setTranslateX(350);
        uiText.setTranslateY(300);

        // 5. add UI object to scene
        getGameScene().addUINode(uiText);

        uiText.setOpacity(0);

        getUIFactory().fadeOut(uiText, Duration.seconds(0), Duration.seconds(2)).startInPlayState();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
