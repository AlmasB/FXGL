/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows one approach to making fireworks with particles.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FireworksSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        spawnMajor(new Point2D(random(0, 1200), 700));

        run(() -> {
            spawnMajor(new Point2D(random(0, 1200), 700));
        }, Duration.seconds(0.15));
    }

    private void spawnMajor(Point2D p) {
        var emitter = ParticleEmitters.newFireEmitter();
        emitter.setNumParticles(15);
        emitter.setEmissionRate(0.5);
        emitter.setSize(1, 24);
        emitter.setSpawnPointFunction(i -> new Point2D(random(-1, 1), random(-1, 1)));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 0.6)));
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSourceImage(texture("particles/" + "star_04.png", 32, 32).multiplyColor(Color.YELLOW));
        emitter.setAllowParticleRotation(true);

        var e = entityBuilder()
                .at(p)
                .view(new Circle(1, 1, 1, Color.WHITE))
                .with(new ProjectileComponent(new Point2D(0, -1), 750))
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

        runOnce(() -> {
            spawnMinor(e.getPosition());

            e.removeFromWorld();
        }, Duration.seconds(random(0.4, 0.7)));
    }

    private void spawnMinor(Point2D p) {
        var color = FXGLMath.randomColor().brighter().brighter();

        var emitter = ParticleEmitters.newExplosionEmitter(random(50, 150));
        emitter.setExpireFunction(i -> Duration.seconds(random(1.25, 2.5)));
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
        emitter.setAccelerationFunction(() -> new Point2D(random(1, 1.5), random(1, 35.5)));
        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSize(8, 32);
        emitter.setAllowParticleRotation(false);
        emitter.setSourceImage(texture("particles/" + "flare_01.png", 64, 64).multiplyColor(color));

        entityBuilder()
                .at(p)
                .with(new ParticleComponent(emitter))
                .with(new ExpireCleanComponent(Duration.seconds(3)).animateOpacity())
                .zIndex(100)
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
