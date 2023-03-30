/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
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

//        emitter.setMaxEmissions(1);
        emitter.setNumParticles(35);
        emitter.setEmissionRate(0.5);
        emitter.setSize(1, 24);
        //emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.002));
        emitter.setSpawnPointFunction(i -> new Point2D(random(-1, 1), random(-1, 1)));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 0.6)));
//
//        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
//        emitter.setAccelerationFunction(() -> new Point2D(0, random(1, 3)));

        var c = Color.YELLOW;

        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSourceImage(texture("particles/" + "star_04.png", 32, 32).multiplyColor(c));
        emitter.setAllowParticleRotation(true);

        var e = entityBuilder()
                .at(p)
                .view(new Circle(1, 1, 1, Color.WHITE))
                .with(new ProjectileComponent(new Point2D(0, -1), 750))
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

        runOnce(() -> {
            explode(e);
        }, Duration.seconds(random(0.4, 0.7)));
    }

    private void explode(Entity e) {
        for (int i = 0; i < 1; i++) {
            spawnMinor(e.getPosition());
        }

        e.removeFromWorld();
        //e.addComponent(new ExpireCleanComponent(Duration.seconds(0.3)));
    }

    private void spawnMinor(Point2D p) {
        var emitter = ParticleEmitters.newExplosionEmitter(random(50, 150));
        emitter.setExpireFunction(i -> Duration.seconds(random(1.25, 2.5)));
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
        emitter.setAccelerationFunction(() -> new Point2D(random(1, 1.5), random(1, 1.5)));

        var c = FXGLMath.randomColor().brighter().brighter();

        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(texture("particles/" + "flare_01.png", 64, 64).multiplyColor(c));

        var e = entityBuilder()
                .at(p)
                .with(new ParticleComponent(emitter))
                .with(new ExpireCleanComponent(Duration.seconds(3)).animateOpacity())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
