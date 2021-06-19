/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package advanced.particles;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getInput;

/**
 * Example of using particles.
 * When left mouse button is clicked, a fire effect will spawn at cursor position.
 * When right mouse button is clicked, an explosion will spawn at cursor position.
 */
public class ParticlesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {
            // 1. create and configure emitter + component
            ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
            emitter.setBlendMode(BlendMode.SRC_OVER);
            emitter.setMaxEmissions(100);

            ParticleComponent component = new ParticleComponent(emitter);

            // 2. add component to entity
            entityBuilder()
                    .at(getInput().getMousePositionWorld())
                    .with(component)
                    .buildAndAttach();
        });

        onBtnDown(MouseButton.SECONDARY, () -> {
            // 1. create and configure emitter + component
            ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(400);
            emitter.setSourceImage(image("brick.png"));

            ParticleComponent component = new ParticleComponent(emitter);

            // 2. add component to entity
            entityBuilder()
                    .at(getInput().getMousePositionWorld())
                    .with(component)
                    .buildAndAttach();
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
