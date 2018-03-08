/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package s07particles;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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

                // 2. create and configure emitter + control
                ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
                //emitter.setEndColor(Color.WHITE);
                //emitter.setExpireFunction((i, x, y) -> Duration.seconds(5));
                ParticleControl control = new ParticleControl(emitter);

                // we also want the entity to destroy itself when particle control is done
                control.setOnFinished(explosion::removeFromWorld);

                // 3. add control to entity
                explosion.addControl(control);

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

                // 2. create and configure emitter + control
                ParticleEmitter emitter = ParticleEmitters.newExplosionEmitter(400);
                emitter.setSourceImage(getAssetLoader().loadImage("brick.png"));
                ParticleControl control = new ParticleControl(emitter);

                // we also want the entity to destroy itself when particle control is done
                control.setOnFinished(explosion::removeFromWorld);

                // 3. add control to entity
                explosion.addControl(control);

                // 4. add entity to game world
                getGameWorld().addEntity(explosion);
            }
        }, MouseButton.SECONDARY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
