/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.component.IrremovableComponent;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ResetSample extends GameApplication {

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
        settings.setTitle("ResetSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {
        // 2. create entity and attach to world using fluent API
        player = Entities.builder()
                .type(Type.PLAYER)
                .at(100, 100)
                .viewFromNode(new Rectangle(40, 40))
                .with(new IrremovableComponent())
                .buildAndAttach(getGameWorld());

        getGameWorld().removeEntity(player);

        System.out.println(getGameWorld().getEntities().size());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
