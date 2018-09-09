/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.util.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.ColoredTexture;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.app.DSLKt.texture;
import static com.almasb.fxgl.core.math.FXGLMath.random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticlesSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {
        Texture t = new ColoredTexture(800, 600, Color.BLACK);

        Entities.builder()
                .viewFromNode(t)
                .buildAndAttach();

        Text text = getUIFactory().newText("Particle Effects", Color.LIGHTBLUE, 36);

        Entities.builder()
                .at(200, 200)
                .viewFromNode(text)
                .buildAndAttach();

        for (int i = 0; i < 1; i++) {
            spawnSmoke(i * 160);
        }
    }

    private Entity spawnSmoke(int x) {
        Texture t = texture("particles/circle_03" + ".png", 128, 128);

        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(t.getImage());
        emitter.setSize(150, 220);
        emitter.setNumParticles(10);
        emitter.setEmissionRate(0.01);
        emitter.setVelocityFunction((i) -> new Point2D(random() * 2.5, -random() * random(80, 120)));
        emitter.setExpireFunction((i) -> Duration.seconds(random(4, 7)));
        emitter.setScaleFunction((i) -> new Point2D(0.15, 0.10));
        emitter.setSpawnPointFunction((i) -> new Point2D(random(0, 600), 0));

        return Entities.builder()
                .at(x, getHeight() + 110)
                //.viewFromNode(texture("smoke0" + FXGLMath.random(0, 2) + ".png", 64, 64))
                .with(new ParticleComponent(emitter))
                //.with(new RandomMoveComponent(random(1, 5), random(0, 200), random(50, 1200), getAppBounds()))
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
