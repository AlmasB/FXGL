/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.shape.Rectangle;

/**
 * This sample shows how to create custom controls for entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EntityControlSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("EntityControlSample");
        settings.setVersion("0.1");





    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(400, 300)
                .viewFromNode(new Rectangle(40, 40))
                // 3. add a new instance of control to entity
                .with(new RotatingControl())
                .buildAndAttach(getGameWorld());
    }

    // 1. create class that extends Control
    private class RotatingControl extends Component {

        @Override
        public void onUpdate(double tpf) {
            // 2. specify behavior of the entity enforced by this control
            entity.rotateBy(tpf * 45);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
