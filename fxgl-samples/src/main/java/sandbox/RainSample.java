/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.PauseMenu;
import com.almasb.fxgl.app.SceneFactory;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.scene.paint.Color;
import sandbox.customization.CustomPauseMenuApp;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Using particles with source images and colorization.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RainSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public PauseMenu newPauseMenu() {
                return new CustomPauseMenuApp.MyPauseMenu();
            }
        });
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .view("underwater3.png")
                .buildAndAttach();

        // example - multiply color with existing
        ParticleEmitter emitter = ParticleEmitters.newRainEmitter(getAppWidth() / 2);
        emitter.setSourceImage(texture("rain.png").multiplyColor(Color.BLUE));

        entityBuilder()
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

        // example - set color
        ParticleEmitter emitter2 = ParticleEmitters.newRainEmitter(getAppWidth() / 2);
        emitter2.setSourceImage(texture("rain.png").toColor(Color.BLUE));

        emitter2.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());

        entityBuilder()
                .at(getAppWidth() / 2, 0)
                .with(new ParticleComponent(emitter2))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
