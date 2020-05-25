/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.circlegame;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static sandbox.circlegame.CircleNNType.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class CircleNNApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setDeveloperMenuEnabled(true);
    }

    private Entity player;
    private Text textPlace;
    private int place;

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

        for (int i = 0; i < 99; i++) {
            spawn("circle", getAppWidth() / 2, getAppHeight() / 2);
        }

        player = getGameWorld().getRandom(CIRCLE).get();
        //player.setType(PLAYER);
        player.removeComponent(RandomMoveComponent.class);
        player.removeComponent(BlockCollisionComponent.class);
        player.addComponent(new KeepOnScreenComponent());
        player.getComponent(CircleComponent.class).pause();

        place = 99;
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(BLOCK, BULLET, (block, bullet) -> {
            bullet.removeFromWorld();
        });

        onCollisionBegin(CIRCLE, BULLET, (circle, bullet) -> {
            if (bullet.getObject("owner") == circle)
                return;

            circle.call("takeHit");
            bullet.removeFromWorld();

            if (!circle.isActive()) {
                onCircleDied();
            }
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
