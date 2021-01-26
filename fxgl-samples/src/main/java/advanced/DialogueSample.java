/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use in-game dialogues.
 */
public class DialogueSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        // we schedule this to fire in 0.5 seconds since we can't change our scene while loading in background
        runOnce(() -> {
            // the file simple.json is loaded from /assets/dialogues/
            var dialogueGraph = getAssetLoader().loadDialogueGraph("simple.json");

            getCutsceneService().startDialogueScene(dialogueGraph);
        }, Duration.seconds(0.5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
