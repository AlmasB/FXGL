/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.cutscene.dialogue.DialogueContext;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.net.MalformedURLException;
import java.nio.file.Paths;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use in-game dialogues.
 */
public class DialogueSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        PropertyMap map = new PropertyMap();

        // we schedule this to fire in 0.5 seconds since we can't change our scene while loading in background
        onKeyDown(KeyCode.F, () -> {
            // the file simple.json is loaded from /assets/dialogues/
            try {
                var url = Paths.get("function_test.json").toUri().toURL();

                var dialogueGraph = getAssetLoader().loadDialogueGraph(url);

                getCutsceneService().startDialogueScene(dialogueGraph, () -> map);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }


        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
