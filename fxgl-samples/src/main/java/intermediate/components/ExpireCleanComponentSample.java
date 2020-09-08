/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.components;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Shows how to use ExpireCleanComponent, which automatically removes an entity
 * from the world when the given duration expires.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ExpireCleanComponentSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        FXGL.entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40))
                .with(new ExpireCleanComponent(Duration.seconds(2)).animateOpacity())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
