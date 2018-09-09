/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.extra.entity.components.DraggableComponent;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class HitBoxScaleSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(100, 100)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.YELLOW))
                .with(new DraggableComponent())
                .buildAndAttach();

        Entities.builder()
                .at(500, 100)
                .viewFromNodeWithBBox(new Circle(23, Color.BLUE))
                .with(new DraggableComponent())
                .buildAndAttach();

        Entities.builder()
                .at(300, 100)
                .viewFromTextureWithBBox("brick.png")
                .with(new DraggableComponent())
                .buildAndAttach();
    }
}
