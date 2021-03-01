/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package advanced.physics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class FlyingKeysPhysicsSample extends GameApplication {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private static final int BUTTON_WIDTH = 40;
    private static final int BUTTON_HEIGHT = 50;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setRandomSeed(4321);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        entityBuilder().buildScreenBoundsAndAttach(50);

        for (int i = 0; i < ALPHABET.length(); i++) {
            addButtonEntity(5 + i * (BUTTON_WIDTH + 6), 250, ALPHABET.charAt(i));
        }

        run(() -> {
            getPhysicsWorld().setGravity(FXGLMath.random(-10, 10) * 55, FXGLMath.random(-10, 10) * 55);
        }, Duration.seconds(2.1));
    }

    private void addButtonEntity(double x, double y, char c) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        physics.setFixtureDef(new FixtureDef().density(0.7f).restitution(0.3f));

        Button button = new Button(c + "");
        button.setFont(Font.font(18));
        button.setPrefWidth(BUTTON_WIDTH);
        button.setPrefHeight(BUTTON_HEIGHT);

        entityBuilder()
                .at(x, y)
                .bbox(BoundingShape.box(BUTTON_WIDTH, BUTTON_HEIGHT))
                .view(button)
                .with(physics)
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
