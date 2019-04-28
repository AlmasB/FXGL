/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.input.KeyCode;

/**
 * Shows how to use sounds.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SoundSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        FXGL.onKeyDown(KeyCode.F, "Play audio", () -> {
            FXGL.play(FXGL.getSettings().getSoundMenuSelect());
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
