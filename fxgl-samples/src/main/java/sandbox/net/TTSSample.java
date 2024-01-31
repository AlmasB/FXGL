/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.net;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.intelligence.tts.TextToSpeechService;
import com.almasb.fxgl.speechrecog.SpeechRecognitionService;
import com.almasb.fxgl.ui.FontType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class TTSSample extends GameApplication {

    private TextArea output;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.addEngineService(SpeechRecognitionService.class);
        settings.addEngineService(TextToSpeechService.class);
    }

    @Override
    protected void initGame() {
        getService(SpeechRecognitionService.class).addInputHandler(input -> {
            if (input.isEmpty() || input.trim().isEmpty())
                return;

            getService(TextToSpeechService.class).speak(input);
        });

        //getService(SpeechRecognitionService.class).start();
        getService(TextToSpeechService.class).start();
    }

    @Override
    protected void initUI() {
        output = new TextArea();
        output.setWrapText(true);
        output.setPrefSize(getAppWidth(), getAppHeight() - 200);
        output.setFont(getUIFactoryService().newFont(FontType.MONO, 18));

        addUINode(output);

        var btn = new Button("Speak");
        btn.setOnAction(e -> {
            getService(TextToSpeechService.class).speak(output.getText());
        });

        addUINode(btn, 50, getAppHeight() - 150);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
