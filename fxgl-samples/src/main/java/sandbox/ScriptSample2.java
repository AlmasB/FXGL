/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.devtools.DeveloperWASDControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.gameplay.rpg.quest.QuestPane;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Shows how to use scripted controls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScriptSample2 extends GameApplication {

    private enum EntityType {
        PC, NPC, COIN
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ScriptSample2");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Trigger Dialog") {
            @Override
            protected void onActionBegin() {
                triggerDialog();
            }
        }, KeyCode.ENTER);
    }

    @Override
    protected void initGame() {
        GameEntity e = Entities.builder()
                .type(EntityType.PC)
                .at(300, 300)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.BLUE))
                .with(new CollidableComponent(true))
                .with(new DeveloperWASDControl())
                .buildAndAttach(getGameWorld());

        Entities.builder()
                .type(EntityType.NPC)
                .at(400, 300)
                .viewFromNodeWithBBox(new Rectangle(40, 40, Color.RED))
                .with(new CollidableComponent(true))
                .buildAndAttach(getGameWorld());
    }

    private void triggerDialog() {
        getGameplay().getCutsceneManager().startRPGCutscene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
