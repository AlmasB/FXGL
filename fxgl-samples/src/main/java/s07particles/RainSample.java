/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s07particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;

/**
 * Using particles with source images and colorization.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RainSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("RainSample");
        settings.setVersion("0.1");






    }

    @Override
    protected void initGame() {
        Entities.builder()
                .viewFromTexture("underwater3.png")
                .buildAndAttach(getGameWorld());

        // example - multiply color with existing
        ParticleEmitter emitter = ParticleEmitters.newRainEmitter((int)getWidth() / 2);
        emitter.setSourceImage(getAssetLoader().loadTexture("rain.png").multiplyColor(Color.RED).getImage());

        Entities.builder()
                .with(new ParticleComponent(emitter))
                .buildAndAttach(getGameWorld());

        // example - set color
        ParticleEmitter emitter2 = ParticleEmitters.newRainEmitter((int)getWidth() / 2);
        emitter2.setSourceImage(getAssetLoader().loadTexture("rain.png").toColor(Color.RED).getImage());

        emitter2.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());

        Entities.builder()
                .at(getWidth() / 2, 0)
                .with(new ParticleComponent(emitter2))
                .buildAndAttach(getGameWorld());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
