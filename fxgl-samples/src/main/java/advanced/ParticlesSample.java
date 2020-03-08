/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package advanced;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

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
        Input input = getInput();

        input.addAction(new UserAction("Spawn Fire") {
            @Override
            protected void onActionBegin() {
                // 1. create and configure emitter + component
                ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
                emitter.setBlendMode(BlendMode.SRC_OVER);
                emitter.setMaxEmissions(100);

                ParticleComponent component = new ParticleComponent(emitter);

                // 2. add component to entity
                entityBuilder()
                        .at(input.getMousePositionWorld())
                        .with(component)
                        .buildAndAttach();
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Spawn Explosion") {
            @Override
            protected void onActionBegin() {
                // 1. create and configure emitter + component
                ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(400);
                emitter.setSourceImage(getAssetLoader().loadImage("brick.png"));

                ParticleComponent component = new ParticleComponent(emitter);

                // 2. add component to entity
                entityBuilder()
                        .at(input.getMousePositionWorld())
                        .with(component)
                        .buildAndAttach();
            }
        }, MouseButton.SECONDARY);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
