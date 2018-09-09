/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.shape.Rectangle;

/**
 * This sample shows how to create custom components for entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityComponentSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("EntityComponentSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(400, 300)
                .viewFromNode(new Rectangle(40, 40))
                // 3. add a new instance of component to entity
                .with(new RotatingComponent())
                .buildAndAttach(getGameWorld());
    }

    // 1. create class that extends Component
    private class RotatingComponent extends Component {

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
