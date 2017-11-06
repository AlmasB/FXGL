/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.handler.CollectibleHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(768);
        settings.setTitle("MarioApp");
        settings.setVersion("0.2");
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setSingleStep(false);
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

        getInput().addAction(new UserAction("Enter") {
            @Override
            protected void onActionBegin() {
                stepLoop();
            }
        }, KeyCode.L);
    }

    @Override
    protected void preInit() {
        getGameScene().setBackgroundColor(Color.rgb(92, 148, 252));
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

        getGameWorld().setLevelFromMap("mario" + 0 + ".json");

        getGameWorld().spawn("player", 350, 24 * 70 - 768);

        Entity player = getGameWorld().getEntitiesByType(MarioType.PLAYER).get(0);
        playerControl = player.getControl(PlayerControl.class);

        getGameScene().getViewport().setBounds(0, 0, 64*70, 24 * 70 - 70);
        getGameScene().getViewport().bindToEntity(player, 500, 0);

        getGameWorld().spawn("robot", 1500, 24 * 70 - 1300);

        level++;
        if (level == 4)
            level = 1;
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.PLATFORM) {
            @Override
            protected void onHitBoxTrigger(Entity a, Entity b, HitBox boxA, HitBox boxB) {
                if (boxA.getName().equals("lower")) {
                    playerControl.canJump = true;
                }
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.PORTAL) {
            @Override
            protected void onCollisionBegin(Entity a, Entity b) {
                nextLevel = true;
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollectibleHandler(MarioType.PLAYER, MarioType.COIN, "drop.wav"));
    }

    private Text debug;

    @Override
    protected void initUI() {
        debug = getUIFactory().newText("");
        debug.setTranslateX(500);
        debug.setTranslateY(200);

        getGameScene().addUINode(debug);
    }

    @Override
    protected void onUpdate(double tpf) {
        debug.setText("On Ground: " + playerControl.getEntity().getComponent(PhysicsComponent.class).isGrounded());
    }

    private boolean nextLevel = false;

    @Override
    protected void onPostUpdate(double tpf) {
        if (nextLevel) {
            nextLevel = false;
            nextLevel();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
