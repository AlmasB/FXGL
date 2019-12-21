/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.text.Text;

/**
 * Shows how to use JavaFX UI within FXGL.
 */
public class UISample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initUI() {
        // 1. create any JavaFX or FXGL UI object
        Text uiText = new Text("Hello World");

        // 2. add the UI object to game scene (easy way) at 100, 100
        FXGL.addUINode(uiText, 100, 100);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
