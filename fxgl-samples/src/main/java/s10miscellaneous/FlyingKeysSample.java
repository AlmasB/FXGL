/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s10miscellaneous;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FlyingKeysSample extends GameApplication {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("FlyingKeysSample");
        settings.setVersion("0.1");





    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Box") {
            @Override
            protected void onActionBegin() {
                Entity box = createPhysicsEntity();

                // 3. set hit box (-es) to specify bounding shape
                box.getBoundingBoxComponent()
                        .addHitBox(new HitBox("Left", BoundingShape.box(20, 30)));

                Button button = new Button(ALPHABET.charAt(FXGLMath.random(ALPHABET.length() - 1)) + "");
                button.setPrefWidth(20);
                button.setPrefHeight(30);
                button.setOnAction(e -> System.out.println(button.getText()));

                box.getViewComponent().setView(button);

                getGameWorld().addEntity(box);
            }
        }, MouseButton.SECONDARY);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntity(Entities.makeScreenBounds(50));
    }

    @Override
    protected void initPhysics() {

        getMasterTimer().runAtInterval(() -> {
            getPhysicsWorld().setGravity(FXGLMath.random(-10, 10), FXGLMath.random(-10, 10));
        }, Duration.seconds(2.5));
    }

    private Entity createPhysicsEntity() {
        // 1. create and configure physics component
        PhysicsComponent physics = new PhysicsComponent();

        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.setDensity(0.7f);
        fd.setRestitution(0.3f);
        physics.setFixtureDef(fd);

        return Entities.builder()
                .at(getWidth() / 2, getHeight() / 2)
                // 2. add physics component
                .with(physics)
                .build();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
