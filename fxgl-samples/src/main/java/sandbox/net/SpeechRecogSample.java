/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.net;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.intelligence.speechrecog.SpeechRecognitionService;
import com.almasb.fxgl.ui.FontType;
import javafx.scene.control.TextArea;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use the speech recognition service.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class SpeechRecogSample extends GameApplication {

    private TextArea output;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.addEngineService(SpeechRecognitionService.class);
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    @Override
    protected void initGame() {
        getService(SpeechRecognitionService.class).readyProperty().addListener((o, old, isReady) -> {
            System.out.println("Updated readyProperty: " + isReady);
        });

        getService(SpeechRecognitionService.class).addInputHandler((input, confidence) -> {
            output.appendText(String.format("(confidence %.0f) %s\n", confidence*100, input));
        });

        getService(SpeechRecognitionService.class).start();
    }

    @Override
    protected void initUI() {
        output = new TextArea();
        output.setWrapText(true);
        output.setPrefSize(getAppWidth(), getAppHeight());
        output.setFont(getUIFactoryService().newFont(FontType.MONO, 18));

        addUINode(output);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
