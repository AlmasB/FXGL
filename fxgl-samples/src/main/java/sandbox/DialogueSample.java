/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class DialogueSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        runOnce(() -> {
            var dialogueGraph = getAssetLoader().loadDialogueGraph("simple.json");

            getCutsceneService().startDialogueScene(dialogueGraph);
        }, Duration.seconds(0.5));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
