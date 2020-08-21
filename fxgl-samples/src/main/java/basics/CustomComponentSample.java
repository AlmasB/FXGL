/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import javafx.scene.shape.Rectangle;

/**
 * This sample shows how to create custom components for entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class CustomComponentSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        FXGL.entityBuilder()
                .at(400, 300)
                .view(new Rectangle(40, 40))
                // 3. add a new instance of component to entity
                .with(new RotatingComponent())
                .buildAndAttach();
    }

    // 1. create class that extends Component
    // Note: ideally in a separate file. It's included in this file for clarity.
    private static class RotatingComponent extends Component {

        @Override
        public void onUpdate(double tpf) {
            // 2. specify behavior of the entity enforced by this component
            entity.rotateBy(tpf * 45);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
