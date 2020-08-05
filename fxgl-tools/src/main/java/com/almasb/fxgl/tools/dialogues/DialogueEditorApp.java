/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.tools.dialogues;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;

import java.util.Map;

/**
 * A dialogue editor for FXGL.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class DialogueEditorApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1600);
        settings.setHeight(900);
        settings.setTitle("FXGL Dialogue Editor");
        settings.setVersion("1.0");
        settings.getCSSList().add("dialogue_editor.css");
        settings.setIntroEnabled(false);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        // TODO: have a const var name list
        vars.put("isSnapToGrid", true);
        vars.put("isColorMode", true);
    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().addUINodes(new MainUI());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
