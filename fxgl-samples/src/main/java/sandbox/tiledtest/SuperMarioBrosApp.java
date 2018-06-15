/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiledtest;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.extra.entity.components.ActivatorComponent;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.input.virtual.VirtualControllerOverlay;
import com.almasb.fxgl.input.virtual.VirtualControllerStyle;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.app.DSLKt.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SuperMarioBrosApp extends GameApplication {

    private Entity player;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1400);
        settings.setHeight(700);
    }

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getComponent(MarioComponent.class).left();
            }
        }, KeyCode.A, VirtualButton.X);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getComponent(MarioComponent.class).right();
            }
        }, KeyCode.D, VirtualButton.B);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                player.getComponent(MarioComponent.class).jump();
            }
        }, KeyCode.W, VirtualButton.Y);

        getInput().addAction(new UserAction("Activate") {
            @Override
            protected void onActionBegin() {
                spawn("bullet", player.getPosition());
//                getGameWorld().getCollidingEntities(player)
//                        .stream()
//                        .filter(e -> e.hasComponent(ActivatorComponent.class))
//                        .map(e -> e.getComponent(ActivatorComponent.class))
//                        .forEach(c -> c.activate(player));
            }
        }, KeyCode.F, VirtualButton.A);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.rgb(92, 148, 252));

        getGameWorld().addEntityFactory(new TiledFactory());
        getGameWorld().setLevelFromMap("marioHD.tmx");

        player = spawn("player", 15, 10);

        getGameScene().getViewport().setBounds(0, 0, 212*70, getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 2, getHeight() / 2);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 1750);

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EType.PLAYER, EType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity pc, Entity enemy) {
                if (pc.getY() < enemy.getY()) {
                    //System.out.println("KILL");

                    player.getComponent(MarioComponent.class).jump();
                    enemy.removeFromWorld();

                } else {
                    System.out.println("DEAD");
                }
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EType.PLAYER, EType.CRATE) {
            @Override
            protected void onCollisionBegin(Entity pc, Entity crate) {
                if (pc.getY() > crate.getBottomY()) {
                    crate.getComponent(CrateComponent.class).bump();
                }
            }
        });
    }

    @Override
    protected void initUI() {
        VirtualControllerOverlay vcOverlay = new VirtualControllerOverlay(VirtualControllerStyle.XBOX);
        vcOverlay.setTranslateY(500);

        getGameScene().addUINodes( vcOverlay);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
