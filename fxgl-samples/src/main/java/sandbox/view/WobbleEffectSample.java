/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.view;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.effects.WobbleEffect;
import javafx.geometry.Orientation;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to init a basic game object and attach it to the world
 * using fluent API.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class WobbleEffectSample extends GameApplication {

    private static final String PROPERTY_NAME = "textureName";
    private static final String[] TEXTURE_NAMES = new String[]{
            "brick.png",
            "bird.png",
            "coin.png",
            "dude.png",
            "player2.png"
    };

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setHeightFromRatio(16 / 9.0);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> getGameWorld().getEntities()
                .forEach(e -> {
                    String textureName = e.getString(PROPERTY_NAME);
                    e.getComponent(EffectComponent.class).startEffect(new WobbleEffect(texture(textureName), Duration.seconds(3), 2, 4, Orientation.VERTICAL));
                }));

        onKeyDown(KeyCode.G, () -> getGameWorld().getEntities()
                .forEach(e -> {
                    String textureName = e.getString(PROPERTY_NAME);
                    e.getComponent(EffectComponent.class).startEffect(new WobbleEffect(texture(textureName), Duration.seconds(3), 1, 10, Orientation.HORIZONTAL));
                }));
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY);

        double i = 0;

        for (String textureName : TEXTURE_NAMES) {
            entityBuilder()
                    .at(50, 50 + i * 75)
                    .view(textureName)
                    .with(PROPERTY_NAME, textureName)
                    .with(new EffectComponent())
                    .buildAndAttach();

            i++;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
