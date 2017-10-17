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
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.gameplay.rpg.quest.QuestPane;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Map;

/**
 * Shows how to use scripted controls.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScriptSample extends GameApplication {

    private enum EntityType {
        PC, NPC, COIN
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ScriptSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("coins", 0);
        vars.put("alive", true);
        vars.put("name", "MainCharacter");
        vars.put("armor", 10.5);
    }

    @Override
    protected void initGame() {
        Entity e = Entities.builder()
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

        for (int i = 0; i < 3; i++) {
            Entities.builder()
                    .type(EntityType.COIN)
                    .at(FXGLMath.random(100, 450), 500)
                    .viewFromNodeWithBBox(new Circle(20, Color.GOLD))
                    .with(new CollidableComponent(true))
                    .buildAndAttach(getGameWorld());
        }

        getMasterTimer().runAtInterval(() -> {
            getAudioPlayer().playPositionalSound("drop.wav", new Point2D(400, 300), e.getCenter(), 600);
        }, Duration.seconds(2));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PC, EntityType.NPC) {
            @Override
            protected void onCollisionBegin(Entity pc, Entity npc) {
                getGameplay().getCutsceneManager().startCutscene("cutscene.js");
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PC, EntityType.COIN) {
            @Override
            protected void onCollisionBegin(Entity pc, Entity coin) {
                getGameState().increment("coins", +1);
                coin.removeFromWorld();
            }
        });
    }

    @Override
    protected void initUI() {
        getGameScene().addUINode(new QuestPane(300, 300));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
