/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s07particles;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

/**
 * Example of using particles.
 * When left mouse button is clicked, an explosion will spawn at cursor position.
 */
public class ParticlesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ParticlesSample");
        settings.setVersion("0.1");
        settings.setProfilingEnabled(true);
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        input.addAction(new UserAction("Spawn Explosion") {
            @Override
            protected void onActionBegin() {
                // 1. create entity
                Entity explosion = new Entity();
                explosion.setPosition(input.getMousePositionWorld());

                // 2. create and configure emitter + component
                ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
                emitter.setBlendMode(BlendMode.SRC_OVER);
                //emitter.setEndColor(Color.WHITE);
                //emitter.setExpireFunction((i, x, y) -> Duration.seconds(5));
                ParticleComponent component = new ParticleComponent(emitter);

                // we also want the entity to destroy itself when particle component is done
                component.setOnFinished(explosion::removeFromWorld);

                // 3. add control to entity
                explosion.addComponent(component);

                // 4. add entity to game world
                getGameWorld().addEntity(explosion);
            }
        }, MouseButton.PRIMARY);

        input.addAction(new UserAction("Spawn Explosion 2") {
            @Override
            protected void onActionBegin() {
                // 1. create entity
                Entity explosion = new Entity();
                explosion.setPosition(input.getMousePositionWorld());

                // 2. create and configure emitter + component
                ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(400);
                emitter.setSourceImage(getAssetLoader().loadImage("brick.png"));
                ParticleComponent component = new ParticleComponent(emitter);

                // we also want the entity to destroy itself when particle component is done
                component.setOnFinished(explosion::removeFromWorld);

                // 3. add control to entity
                explosion.addComponent(component);

                // 4. add entity to game world
                getGameWorld().addEntity(explosion);
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
