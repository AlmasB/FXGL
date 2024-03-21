/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.KeyTrigger;
import com.almasb.fxgl.input.TriggerListener;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 */
public class InputModifierSample extends GameApplication {

    private Entity e;
    private double speedMult = 1.0;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        getInput().addTriggerListener(new TriggerListener() {
            @Override
            protected void onKeyBegin(KeyTrigger keyTrigger) {
                if (keyTrigger.getKey() == KeyCode.SHIFT) {
                    speedMult = 3.0;
                }
            }

            @Override
            protected void onKey(KeyTrigger keyTrigger) {
                switch (keyTrigger.getKey()) {
                    case W -> { e.translateY(-speedMult * 1); }
                    case S -> { e.translateY(speedMult * 1); }
                    case A -> { e.translateX(-speedMult * 1); }
                    case D -> { e.translateX(speedMult * 1); }
                }
            }

            @Override
            protected void onKeyEnd(KeyTrigger keyTrigger) {
                if (keyTrigger.getKey() == KeyCode.SHIFT) {
                    speedMult = 1.0;
                }
            }
        });
    }

    @Override
    protected void initGame() {
        e = entityBuilder()
                .at(150, 150)
                .view(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
