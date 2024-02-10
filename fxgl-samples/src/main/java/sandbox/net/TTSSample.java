/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.net;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.intelligence.tts.TextToSpeechService;
import com.almasb.fxgl.ui.FontType;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

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
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.addEngineService(TextToSpeechService.class);
    }

    @Override
    protected void initGame() {
        getService(TextToSpeechService.class).start();
    }

    @Override
    protected void initUI() {
        output = new TextArea();
        output.setWrapText(true);
        output.setPrefSize(getAppWidth() - 200, getAppHeight() - 200);
        output.setFont(getUIFactoryService().newFont(FontType.MONO, 18));

        addUINode(output);

        var btn = new Button("Speak");
        btn.setFont(Font.font(16.0));
        btn.setOnAction(e -> {
            getService(TextToSpeechService.class).speak(output.getText());
        });

        addUINode(btn, 50, getAppHeight() - 100);

        getService(TextToSpeechService.class).readyProperty().addListener((o, old, isReady) -> {
            if (isReady) {
                System.out.println("TTS service is ready");

                var cb = getUIFactoryService().newChoiceBox(FXCollections.observableArrayList(getService(TextToSpeechService.class).getVoices()));
                cb.setPrefWidth(400);
                cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    getService(TextToSpeechService.class).setSelectedVoice(newValue);
                });

                if (!cb.getItems().isEmpty()) {
                    cb.getSelectionModel().selectFirst();
                }

                addUINode(cb, 50, getAppHeight() - 150);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
