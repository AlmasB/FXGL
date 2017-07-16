/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.scifi;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.handler.CollectibleHandler;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.ui.LevelText;
import javafx.geometry.Orientation;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Arrays;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ScifiSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(768);
        settings.setTitle("ScifiSample");
        settings.setVersion("0.1");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private PlayerControl playerControl;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                playerControl.left();
            }

            @Override
            protected void onActionEnd() {
                playerControl.stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                playerControl.right();
            }

            @Override
            protected void onActionEnd() {
                playerControl.stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                playerControl.jump();
            }
        }, KeyCode.W);

        getInput().addAction(new UserAction("Animate Level Text") {
            @Override
            protected void onActionBegin() {
                levelText.animateIn();
            }

            @Override
            protected void onActionEnd() {
                levelText.animateOut();
            }
        }, KeyCode.ENTER);
    }

    @Override
    protected void initGame() {
        nextLevel();
    }

    private int level = 1;

    private RenderLayer BG = new RenderLayer() {
        @Override
        public String name() {
            return "bg";
        }

        @Override
        public int index() {
            return 900;
        }
    };

    private void nextLevel() {
        getGameWorld().setLevelFromMap("mario" + level + ".json");

        Entity player = getGameWorld().getEntitiesByType(ScifiType.PLAYER).get(0);
        playerControl = player.getControl(PlayerControl.class);

        getGameScene().getViewport().setBounds(0, 0, 3000, 768);
        getGameScene().getViewport().bindToEntity(player, 500, 0);

        // assets from https://raventale.itch.io/parallax-background
        getGameScene().addGameView(new ParallaxBackgroundView(Arrays.asList(
                new ParallaxTexture(getAssetLoader().loadTexture("bg_10.png", 1280, 768), 1.0),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_9.png", 1280, 768), 0.05),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_8.png", 1280, 768), 0.1),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_7.png", 1280, 768), 0.3),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_6.png", 1280, 768), 0.45),
                //new ParallaxTexture(getAssetLoader().loadTexture("bg_5.png", 1280, 768), 0.45),
                //new ParallaxTexture(getAssetLoader().loadTexture("bg_4.png", 1280, 768), 0.6),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_3.png", 1280, 768), 0.5),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_2.png", 1280, 768), 0.7),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_1.png", 1280, 768), 0.8),
                new ParallaxTexture(getAssetLoader().loadTexture("bg_0.png", 1280, 768), 0.9)
        ), Orientation.HORIZONTAL, BG));

        level++;
        if (level == 4)
            level = 1;
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ScifiType.PLAYER, ScifiType.PLATFORM) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxA.getName().equals("lower")) {
                    playerControl.canJump = true;
                }
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(ScifiType.PLAYER, ScifiType.PORTAL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                nextLevel();
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollectibleHandler(ScifiType.PLAYER, ScifiType.COIN, "drop.wav"));
    }

    private LevelText levelText;

    @Override
    protected void initUI() {
//        Text text = getUIFactory().newText("Level 1", Color.WHITESMOKE, 46.0);
//
//        DropShadow ds = new DropShadow(25, 0, 0, Color.BLACK);
//
//        text.setEffect(ds);
//
//        getUIFactory().centerText(text);

        levelText = new LevelText("Level 1");
        getGameScene().addUINode(levelText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
