/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.robots;

import com.almasb.fxgl.ai.AIControl;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.ExpireCleanControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.handler.CollectibleHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;


/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(770);
        settings.setTitle("MarioApp");
        settings.setVersion("0.2");




        settings.setSingleStep(false);
        settings.setApplicationMode(ApplicationMode.RELEASE);
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

        getInput().addAction(new UserAction("Drop rectangle") {
            @Override
            protected void onActionBegin() {
                Entities.builder()
                        .type(MarioType.OBSTACLE)
                        .at(getInput().getMousePositionWorld())
                        .viewFromNodeWithBBox(new Rectangle(40, 40))
                        .with(new CollidableComponent(true))
                        .with(new ExpireCleanControl(Duration.seconds(2)).animateOpacity())
                        .buildAndAttach();
            }
        }, MouseButton.PRIMARY);
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

        getGameWorld().spawn("player", 350, 0);

        Entity player = getGameWorld().getEntitiesByType(MarioType.PLAYER).get(0);
        playerControl = player.getControl(PlayerControl.class);

        player.addControl(new AIControl("robot.tree"));

        getGameScene().getViewport().setBounds(0, 0, 30*70, 11 * 70);
        getGameScene().getViewport().bindToEntity(player, 500, 0);

        //getGameWorld().spawn("robot", 1500, 24 * 70 - 1300);

        level++;
        if (level == 4)
            level = 1;
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.GHOST_PLATFORM) {
            @Override
            protected void onCollisionBegin(Entity a, Entity platform) {
                platform.getView().setVisible(true);
            }

            @Override
            protected void onCollisionEnd(Entity a, Entity platform) {
                platform.getView().setVisible(false);
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
        debug.setText("On Ground: " + playerControl.getEntity().getComponent(PhysicsComponent.class).isOnGround());
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
