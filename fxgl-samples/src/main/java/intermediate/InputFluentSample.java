/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.onKeyBuilder;

/**
 * Shows how to use input DSL.
 */
public class InputFluentSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKeyBuilder(KeyCode.F)
                .onActionBegin(() -> System.out.println("Action Begin"))
                .onAction(() -> System.out.println("On Action"))
                .onActionEnd(() -> System.out.println("Action End"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
