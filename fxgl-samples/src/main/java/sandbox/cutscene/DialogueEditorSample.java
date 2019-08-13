/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.cutscene;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import dev.dialogue.DialoguePane;

/**
 * WIP basics of a dialogue editor
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DialogueEditorSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setTitle("EditorSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().addUINodes(new DialoguePane());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
