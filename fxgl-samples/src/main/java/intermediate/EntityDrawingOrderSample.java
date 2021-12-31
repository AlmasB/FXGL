/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use z index.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityDrawingOrderSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            getGameWorld().getEntities().get(1).setZIndex(
                    -getGameWorld().getEntities().get(1).getZIndex()
            );
        });
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40))
                .buildAndAttach();

        // we have added red box after black box but because of the z index we specified
        // the red box will be drawn below the black box
        entityBuilder()
                .at(120, 120)
                .view(new Rectangle(40, 40, Color.RED))
                .zIndex(-1)
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
