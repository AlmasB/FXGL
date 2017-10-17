/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to init a basic game object and attach it to the world
 * using predefined Entity.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class InitSampleEasy extends GameApplication {

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
        settings.setTitle("InitSampleEasy");
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
        // 2. create entity and modify necessary components
        player = new Entity();
        player.getTypeComponent().setValue(Type.PLAYER);
        player.getPositionComponent().setValue(100, 100);
        player.getViewComponent().setView(new Rectangle(40, 40));

        // 3. add entity to game world
        getGameWorld().addEntity(player);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
