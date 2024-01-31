/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.net;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.speechrecog.SpeechRecognitionService;
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
    }

    @Override
    protected void initGame() {
        getService(SpeechRecognitionService.class).addInputHandler(input -> {
            getExecutor().startAsyncFX(() -> {
                output.appendText(input + "\n");
            });
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
