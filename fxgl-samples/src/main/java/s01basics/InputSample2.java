/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.annotation.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;

/**
 * Shows how to use input service and bind actions to triggers.
 */
public class InputSample2 extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InputSample2");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        // 1. get input service
        Input input = getInput();

        // 2. add input mappings (action name -> trigger name)
        input.addInputMapping(new InputMapping("Print Line", KeyCode.F));
    }

    // 3. specify which method to call on each action

    @OnUserAction(name = "Print Line", type = ActionType.ON_ACTION_BEGIN)
    public void printLineBegin() {
        System.out.println("Action Begin");
    }

    @OnUserAction(name = "Print Line", type = ActionType.ON_ACTION)
    public void printLine() {
        System.out.println("On Action");
    }

    @OnUserAction(name = "Print Line", type = ActionType.ON_ACTION_END)
    public void printLineEnd() {
        System.out.println("Action End");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
