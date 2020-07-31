/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.serialization.Bundle;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.input.InputCapture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;
import static sandbox.circlegame.CircleNNType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CircleNNApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1750);
        settings.setHeightFromRatio(16/9.0);
        settings.setDeveloperMenuEnabled(true);
    }

    private Entity player;
    private Text textPlace;
    private int place;

    private InputCapture capture;

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> player.translateY(-5));
        onKey(KeyCode.S, () -> player.translateY(5));
        onKey(KeyCode.A, () -> player.translateX(-5));
        onKey(KeyCode.D, () -> player.translateX(5));

        onBtn(MouseButton.PRIMARY, () -> player.call("shoot", getInput().getMousePositionWorld().subtract(player.getCenter())));
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new CircleNNFactory());

        getGameScene().setBackgroundColor(Color.BLACK);

        spawn("block", 200, 200);
        spawn("block", getAppWidth() - 200 - 64, 200);
        spawn("block", 200, getAppHeight() - 200 - 64);
        spawn("block", getAppWidth() - 200 - 64, getAppHeight() - 200 - 64);

        var spawnPoints = List.of(
                new Point2D(100, 100),
                new Point2D(getAppWidth() - 100 - 64, 100),
                new Point2D(getAppWidth() / 2.0 - 32, 100),

                new Point2D(100, getAppHeight() / 2.0 - 32),
                new Point2D(getAppWidth() - 100 - 64, getAppHeight() / 2.0 - 32),
                new Point2D(getAppWidth() / 2.0 - 32, getAppHeight() / 2.0 - 32),

                new Point2D(100, getAppHeight() - 100 - 64),
                new Point2D(getAppWidth() - 100 - 64, getAppHeight() - 100 - 64),
                new Point2D(getAppWidth() / 2.0 - 32, getAppHeight() - 100 - 64)
        );

        spawn("circle", 500.0, 600.0);
        spawn("circle", 500.0, 600.0);

//        spawnPoints.forEach(point -> {
//            for (int i = 0; i < 11; i++) {
//                spawn("circle", point);
//            }
//        });

        player = getGameWorld().getRandom(CIRCLE).get();
        player.removeComponent(RandomMoveComponent.class);
        player.removeComponent(BlockCollisionComponent.class);
        player.addComponent(new KeepOnScreenComponent());
        player.addComponent(new PlayerComponent());

        getGameWorld().getEntitiesByType(CIRCLE)
                .stream()
                .filter(e -> e != player)
                .forEach(e -> {
                    e.getComponent(RandomMoveComponent.class).pause();
                    e.getComponent(BlockCollisionComponent.class).pause();

                    Bundle bundle = getFileSystemService().<Bundle>readDataTask("editor_json/input/input0.dat").run();

                    var input = new InputCapture();
                    input.read(bundle);

                    e.getComponent(CircleComponent.class).getInput().applyCapture(input);
                });

        place = getGameWorld().getEntitiesByType(CIRCLE).size();

        run(() -> {
            var powerupType = PowerupType.SHIELD;
            //var powerupType = FXGLMath.random(PowerupType.values()).get();

            spawn("powerup",
                    new SpawnData(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth(), getAppHeight())))
                            .put("powerupType", powerupType)
            );
        }, Duration.seconds(3));

        capture = getInput().startCapture();
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(BLOCK, BULLET, (block, bullet) -> {
            bullet.removeFromWorld();
        });

        onCollisionBegin(CIRCLE, BULLET, (circle, bullet) -> {
            if (bullet.getObject("owner") == circle)
                return;

            var point = circle.getCenter();

            int damage = bullet.getInt("damage");

            circle.getComponent(CircleComponent.class).takeHit(damage);

            if (!circle.isActive()) {
                Entity killer = bullet.getObject("owner");

                if (killer.isActive()) {
                    killer.getComponent(CircleComponent.class).onKill();
                }

                onCircleDied();
                spawn("explosion", point);
            }

            bullet.removeFromWorld();
        });

        onCollisionBegin(CIRCLE, POWERUP, (circle, powerup) -> {
            circle.getComponent(CircleComponent.class).applyPowerup(powerup.getObject("powerupType"));
            powerup.removeFromWorld();
        });
    }

    @Override
    protected void initUI() {
        textPlace = getUIFactoryService().newText("99", Color.WHITE, 18.0);

        addUINode(getUIFactoryService().newText("Place: ", Color.WHITE, 16.0), 25, 25);
        addUINode(textPlace, 75, 25);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (!player.isActive()) {
            showMessage("Your place: " + (place+1), () -> getGameController().startNewGame());
        }
    }

    public void onCircleDied() {
        getGameScene().getViewport().shakeTranslational(5);

        place--;
        textPlace.setText(place + "");

        animationBuilder()
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .duration(Duration.seconds(0.11))
                .autoReverse(true)
                .repeat(2)
                .scale(textPlace)
                .from(new Point2D(1, 1))
                .to(new Point2D(3, 3))
                .buildAndPlay();

        if (place == 1) {

            getInput().stopCapture();

            var bundle = new Bundle("input");
            capture.write(bundle);

            getFileSystemService().writeDataTask(bundle, "editor_json/input/input0.dat").run();

            showMessage("You Win!", () -> getGameController().startNewGame());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
