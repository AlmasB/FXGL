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
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Pixel;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class FireworksSample2 extends GameApplication {

    private static final String MESSAGE = "F X G L";

    private String[] names = ("circle_01.png\n" +
            "circle_02.png\n" +
            "circle_03.png\n" +
            "circle_04.png\n" +
            "circle_05.png\n" +
            "dirt_01.png\n" +
            "dirt_02.png\n" +
            "dirt_03.png\n" +
            "fire_01.png\n" +
            "fire_02.png\n" +
            "flame_01.png\n" +
            "flame_02.png\n" +
            "flame_03.png\n" +
            "flame_04.png\n" +
            "flame_05.png\n" +
            "flame_06.png\n" +
            "flare_01.png\n" +
            "light_01.png\n" +
            "light_02.png\n" +
            "light_03.png\n" +
            "magic_01.png\n" +
            "magic_02.png\n" +
            "magic_03.png\n" +
            "magic_04.png\n" +
            "magic_05.png\n" +
            "muzzle_01.png\n" +
            "muzzle_02.png\n" +
            "muzzle_03.png\n" +
            "muzzle_04.png\n" +
            "muzzle_05.png\n" +
            "scorch_01.png\n" +
            "scorch_02.png\n" +
            "scorch_03.png\n" +
            "scratch_01.png\n" +
            "slash_01.png\n" +
            "slash_02.png\n" +
            "slash_03.png\n" +
            "slash_04.png\n" +
            "smoke_01.png\n" +
            "smoke_02.png\n" +
            "smoke_03.png\n" +
            "smoke_04.png\n" +
            "smoke_05.png\n" +
            "smoke_06.png\n" +
            "smoke_07.png\n" +
            "smoke_08.png\n" +
            "smoke_09.png\n" +
            "smoke_10.png\n" +
            "spark_01.png\n" +
            "spark_02.png\n" +
            "spark_03.png\n" +
            "spark_04.png\n" +
            "spark_05.png\n" +
            "spark_06.png\n" +
            "spark_07.png\n" +
            "star_01.png\n" +
            "star_02.png\n" +
            "star_03.png\n" +
            "star_04.png\n" +
            "star_05.png\n" +
            "star_06.png\n" +
            "star_07.png\n" +
            "star_08.png\n" +
            "star_09.png\n" +
            "symbol_01.png\n" +
            "symbol_02.png\n" +
            "trace_01.png\n" +
            "trace_02.png\n" +
            "trace_03.png\n" +
            "trace_04.png\n" +
            "trace_05.png\n" +
            "trace_06.png\n" +
            "trace_07.png\n" +
            "twirl_01.png\n" +
            "twirl_02.png\n" +
            "twirl_03.png\n" +
            "window_01.png\n" +
            "window_02.png\n" +
            "window_03.png\n" +
            "window_04.png").split("\n");

    private int index = 16;

    private List<Pixel> pixels;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.SECONDARY, () -> {
            index++;

            if (index == names.length)
                index = 0;
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        Text text = getUIFactoryService().newText(MESSAGE, Color.BLACK, 172);
        pixels = ImagesKt.toPixels(ImagesKt.toImage(text))
                .stream()
                .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .toList();

        spawnMajor(new Point2D(random(0, 1200), 700));

        run(() -> {
            spawnMajor(new Point2D(random(200, 1000), 700));
        }, Duration.seconds(0.95));
    }

    private void spawnMajor(Point2D p) {
        var emitter = ParticleEmitters.newFireEmitter();
        emitter.setNumParticles(35);
        emitter.setEmissionRate(0.5);
        emitter.setSize(1, 24);
        emitter.setSpawnPointFunction(i -> new Point2D(random(-1, 1), random(-1, 1)));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 0.6)));
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
        emitter.setAccelerationFunction(() -> new Point2D(0, random(1, 3)));

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
    }

    private void spawnMinor(Point2D p) {
        var emitter = ParticleEmitters.newExplosionEmitter(random(60, 150));
        emitter.setNumParticles(350);
        emitter.setExpireFunction(i -> Duration.seconds(random(3.25, 6.5)));
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
        emitter.setAllowParticleRotation(false);
        emitter.setSpawnPointFunction(i -> {
            var pixel = FXGLMath.random(pixels).get();

            return new Point2D(pixel.getX() - 190, pixel.getY() - 100).multiply(1.1);
        });

        emitter.setVelocityFunction(i -> new Point2D(Math.cos(i), Math.sin(i)).multiply(2.2));

//        emitter.setVelocityFunction(i -> {
//            var pixel = FXGLMath.random(pixels).get();
//
//            return new Point2D(pixel.getX() - 190, pixel.getY() - 100).multiply(0.3);
//        });
//        emitter.setVelocityFunction(i -> new Point2D(
//                sin(i) * (pow(E, cos(i)) - 2 * cos(4*i) - pow(sin(i/12.0), 5)),
//                -cos(i) * (pow(E, cos(i)) - 2 * cos(4*i) - pow(sin(i/12.0), 5))
//        ).multiply(10));
        emitter.setAccelerationFunction(() -> new Point2D(random(0, 0), random(0, 0)));

        var c = FXGLMath.randomColor().brighter().brighter();

        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(texture("particles/" + names[index], 64, 64).multiplyColor(c));

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
