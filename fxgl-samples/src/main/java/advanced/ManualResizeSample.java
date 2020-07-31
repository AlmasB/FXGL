/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import dev.DeveloperWASDControl;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to enable manual window resize.
 */
public class ManualResizeSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .view(new Rectangle(100.0, 100.0, Color.GREEN))
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        addUINode(new Rectangle(getAppWidth(), getAppHeight(), new Color(.0, .0, 1.0, .5)));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
