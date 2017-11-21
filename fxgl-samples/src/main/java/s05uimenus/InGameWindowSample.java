/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s05uimenus;

import com.almasb.fxgl.input.OnUserAction;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.ActionType;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.InGameWindow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

/**
 * Shows how to use in-game windows.
 * When app is running press O to open an in-game window.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InGameWindowSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("InGameWindowSample");
        settings.setVersion("0.1");





    }

    @Override
    protected void initInput() {
        Input input = getInput();
        input.addInputMapping(new InputMapping("Open", KeyCode.O));
    }

    @OnUserAction(name = "Open", type = ActionType.ON_ACTION_BEGIN)
    public void openWindow() {
        // 1. create in-game window
        InGameWindow window = new InGameWindow("Window Title");

        // 2. set properties
        window.setPrefSize(300, 200);
        window.setPosition(400, 300);
        window.setBackgroundColor(Color.BLACK);

        // 3. attach to game scene as UI node
        getGameScene().addUINode(window);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
