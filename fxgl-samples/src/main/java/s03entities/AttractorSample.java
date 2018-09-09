/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s03entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.extra.entity.components.AttractableComponent;
import com.almasb.fxgl.extra.entity.components.AttractorComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AttractorSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AttractorSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("spawn") {
            @Override
            protected void onActionBegin() {
                Entities.builder()
                        .at(getInput().getMousePositionWorld())
                        .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                        .with(new AttractorComponent(FXGLMath.random(30, 60), 350))
                        .buildAndAttach();
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .at(400, 100)
                .viewFromNode(new Rectangle(40, 40, Color.RED))
                .with(new AttractableComponent(25))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
