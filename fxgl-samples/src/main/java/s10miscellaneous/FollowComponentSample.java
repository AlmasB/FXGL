/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.devtools.DeveloperWASDControl;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.extra.entity.components.FollowComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FollowComponentSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    private Entity player;

    @Override
    protected void initGame() {
        // TODO: add developer "rectangle" with this config
        player = Entities.builder()
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .with(new DeveloperWASDControl())
                .buildAndAttach();

        Entity npc = Entities.builder()
                .viewFromNode(new Circle(20, Color.DARKGREEN))
                .with(new FollowComponent(player))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
