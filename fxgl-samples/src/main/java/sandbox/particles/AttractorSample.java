/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.almasb.fxgl.dsl.FXGL.*;
import static java.lang.Math.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AttractorSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1366);
        settings.setHeight(768);
    }

    private double t = 0.0;


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

    private int index = 0;
    private ParticleEmitter emitter;

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.SECONDARY, () -> {
            var c = FXGLMath.randomColor();
            emitter.setStartColor(c);
            emitter.setEndColor(c.invert());

            var name = names[index++];

            System.out.println("Using " + name);

            emitter.setSourceImage(texture("particles/" + name, 32, 32).multiplyColor(c));

            if (index == names.length) {
                index = 0;
            }
        });
    }

    private List<Point2D> points;

    @Override
    protected void initGame() {
        points = new ArrayList<>();
        points.add(new Point2D(100, 100));
        points.add(new Point2D(getAppWidth() - 100, getAppHeight() - 100));

        getGameScene().setBackgroundColor(Color.BLACK);

        emitter = ParticleEmitters.newExplosionEmitter(300);

        emitter.setBlendMode(BlendMode.ADD);

        emitter.setMaxEmissions(1);
        emitter.setNumParticles(1440);
        emitter.setExpireFunction(i -> Duration.seconds(20000));
        emitter.setEmissionRate(0.52);
        emitter.setSize(4, 64);

        emitter.setScaleFunction(i -> new Point2D(0, 0));

        emitter.setAccelerationFunction(() -> Point2D.ZERO);
        emitter.setVelocityFunction(i -> Point2D.ZERO);
        //emitter.setVelocityFunction(i -> FXGLMath.randomPoint2D().multiply(random(1, 45)));

        emitter.setSpawnPointFunction(i -> new Point2D(
                random(-20, 20),
                random(-20, 20)
        ));

        //emitter.setStartColor(Color.color(0.8, 0.2, 0.1, 0.85));
        emitter.setSourceImage(texture("particles/circle_03.png", 64, 64).multiplyColor(Color.color(0.9, 0.1, 0.2, random(0.2, 0.9))));

        var e = entityBuilder()
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

        emitter.setControl(p -> {
            // using general model adapted from https://github.com/jodiDL/SimpleParticleSystem
            var x = p.position.x;
            var y = p.position.y;
            var noiseValue = FXGLMath.noise2D(x * 0.002 * t, y * 0.002 * t);

            var v = e.getPosition().subtract(p.position.x, p.position.y);

            var distanceSquared = v.magnitude() * v.magnitude();

            var power = 250.0 / max(10, min(distanceSquared, 1600)) * (noiseValue + 1) * random(1, 4);

            p.acceleration.set(v.normalize().multiply(power));

            if (p.position.x < 0) {
                p.position.x = 0;
                p.velocity.x = 1;
            }

            if (p.position.x > getAppWidth() - 10) {
                p.position.x = getAppWidth() - 10;
                p.velocity.x = -1;
            }

            if (p.position.y < 0) {
                p.position.y = 0;
                p.velocity.y = 1;
            }

            if (p.position.y > getAppHeight() - 10) {
                p.position.y = getAppHeight() - 10;
                p.velocity.y = -1;
            }
        });

        e.xProperty().bind(getInput().mouseXWorldProperty().subtract(10));
        e.yProperty().bind(getInput().mouseYWorldProperty().subtract(10));
    }

    @Override
    protected void onUpdate(double tpf) {
        t += tpf;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
