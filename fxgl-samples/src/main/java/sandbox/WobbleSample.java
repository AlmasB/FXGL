/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.control.EffectControl;
import com.almasb.fxgl.extra.entity.effects.WobbleEffect;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class WobbleSample extends GameApplication {

    // 1. define types of entities in the game using Enum
    private enum Type {
        PLAYER
    }

    // make the field instance level
    // but do NOT init here for properly functioning save-load system
    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("WobbleSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initInput() {
        DSLKt.onKeyDown(KeyCode.F, "asd", () -> {
            player.getControl(EffectControl.class).startEffect(new WobbleEffect(Duration.seconds(3), 3, 7, Orientation.VERTICAL));
        });

        DSLKt.onKeyDown(KeyCode.G, "asd2", () -> {
            player.getControl(EffectControl.class).startEffect(new WobbleEffect(Duration.seconds(3), 2, 4, Orientation.HORIZONTAL));
        });
    }

    @Override
    protected void initGame() {
        // 2. create entity and attach to world using fluent API
        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                .viewFromTexture("brick.png")
                //.viewFromNode(new Rectangle(40, 40))
                .with(new EffectControl())
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
