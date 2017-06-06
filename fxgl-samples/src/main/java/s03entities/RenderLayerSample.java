/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.EntityView;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to use render layers.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RenderLayerSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RenderLayerSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .buildAndAttach(getGameWorld());

        EntityView view = new EntityView(new Rectangle(40, 40, Color.RED));

        // 1. predefine or create dynamically like below
        view.setRenderLayer(new RenderLayer() {
            @Override
            public String name() {
                // 2. specify the unique name for that layer
                return "LAYER_BELOW_PLAYER";
            }

            @Override
            public int index() {
                // 3. specify layer index, higher values will be drawn above lower values
                return 1000;
            }
        });

        // we have added box after player but because of the render layer we specified
        // the red box will be drawn below the player
        Entities.builder()
                .at(80, 80)
                .viewFromNode(view)
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
