/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use single step updates for debugging.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SingleStepSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setSingleStep(true);
    }

    @Override
    protected void initInput() {

        // when ENTER is pressed, the game scene will compute one frame
        onKeyDown(KeyCode.ENTER, "update", () -> {
            getGameScene().step(0.016);
        });
    }

    // this update callback is not affected
    @Override
    protected void onUpdate(double tpf) { }

    public static void main(String[] args) {
        launch(args);
    }
}
