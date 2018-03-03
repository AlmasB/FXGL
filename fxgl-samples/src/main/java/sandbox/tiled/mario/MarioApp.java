/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled.mario;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.control.EffectControl;
import com.almasb.fxgl.entity.effect.WobbleEffect;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MarioApp extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(15 * 70);
        settings.setHeight(10 * 70);
    }

    private Entity player;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Left") {
            @Override
            protected void onAction() {
                player.getControl(PlayerControl.class).left();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Right") {
            @Override
            protected void onAction() {
                player.getControl(PlayerControl.class).right();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onAction() {
                player.getControl(PlayerControl.class).jump();
            }
        }, KeyCode.W);

        DSLKt.onKeyDown(KeyCode.F, "asd", () -> {
            player.getControl(EffectControl.class).startEffect(new WobbleEffect(Duration.seconds(3), 3, 7, Orientation.VERTICAL));
        });

        DSLKt.onKeyDown(KeyCode.G, "asd2", () -> {
            player.getControl(EffectControl.class).startEffect(new WobbleEffect(Duration.seconds(3), 2, 4, Orientation.HORIZONTAL));
        });
    }

    @Override
    protected void initGame() {
        getGameWorld().setLevelFromMap("sewers.tmx");

        player = getGameWorld().spawn("player", 50, 50);

        getGameScene().getViewport().setBounds(-1500, 0, 1500, getHeight());
        getGameScene().getViewport().bindToEntity(player, getWidth() / 2, getHeight() / 2);

        getGameWorld().spawn("enemy", 470, 50);
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                coin.getComponent(CollidableComponent.class).setValue(false);

                Animation<?> anim = Entities.animationBuilder()
                        .duration(Duration.seconds(0.5))
                        .interpolator(Interpolators.ELASTIC.EASE_IN())
                        .scale(coin)
                        .from(new Point2D(1, 1))
                        .to(new Point2D(0, 0))
                        .buildAndPlay();

                anim.setOnFinished(() -> coin.removeFromWorld());
            }
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(MarioType.PLAYER, MarioType.DOOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity door) {
                getDisplay().showMessageBox("Level Complete!", () -> {
                    System.out.println("Dialog closed!");
                });
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
