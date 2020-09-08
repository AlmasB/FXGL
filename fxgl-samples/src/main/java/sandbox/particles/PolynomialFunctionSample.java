/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.particle.Particle;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PolynomialFunctionSample extends GameApplication {

    private static final double PIXELS_PER_UNIT = 80.0;

    private List<Point2D> points;
    private ParticleEmitter emitter;

    private Function<Double, Double> func = x -> x * x;

    private List<Point2D> spawnPoints;

    private Map<Particle, Point2D> dest;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            func = x -> FXGLMath.sin(x);

            points = IntStream.rangeClosed(0, getAppWidth())
                    .filter(i -> i % 2 == 0)
                    .map(x -> x - getAppWidth() / 2)
                    .mapToDouble(x -> x / PIXELS_PER_UNIT)
                    .mapToObj(x -> new Point2D(x * PIXELS_PER_UNIT + getAppWidth() / 2.0, getAppHeight() - (func.apply(x) * PIXELS_PER_UNIT + getAppHeight() / 2.0)))
                    .filter(p -> new Rectangle2D(0, 0, getAppWidth(), getAppHeight()).contains(p))
                    .collect(Collectors.toList());

            dest.clear();

            emitter.setSourceImage(texture("particles/circle_05.png", 32, 32).multiplyColor(Color.BLUE));
        });

        onKeyDown(KeyCode.G, () -> {
            func = x -> FXGLMath.sin(x / 4);

            points = IntStream.rangeClosed(0, getAppWidth())
                    .filter(i -> i % 2 == 0)
                    .map(x -> x - getAppWidth() / 2)
                    .mapToDouble(x -> x / PIXELS_PER_UNIT)
                    .mapToObj(x -> new Point2D(x * PIXELS_PER_UNIT + getAppWidth() / 2.0, getAppHeight() - (func.apply(x) * PIXELS_PER_UNIT + getAppHeight() / 2.0)))
                    .filter(p -> new Rectangle2D(0, 0, getAppWidth(), getAppHeight()).contains(p))
                    .collect(Collectors.toList());

            dest.clear();

            emitter.setSourceImage(texture("particles/circle_05.png", 32, 32).multiplyColor(Color.GREEN));
        });

        onKeyDown(KeyCode.H, () -> {
            func = x -> x * x * x;;

            points = IntStream.rangeClosed(0, getAppWidth())
                    .filter(i -> i % 2 == 0)
                    .map(x -> x - getAppWidth() / 2)
                    .mapToDouble(x -> x / PIXELS_PER_UNIT)
                    .mapToObj(x -> new Point2D(x * PIXELS_PER_UNIT + getAppWidth() / 2.0, getAppHeight() - (func.apply(x) * PIXELS_PER_UNIT + getAppHeight() / 2.0)))
                    .filter(p -> new Rectangle2D(0, 0, getAppWidth(), getAppHeight()).contains(p))
                    .collect(Collectors.toList());

            dest.clear();

            emitter.setSourceImage(texture("particles/circle_05.png", 32, 32).multiplyColor(Color.AQUAMARINE));
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        spawnPoints = new ArrayList<>();
        dest = new HashMap<>();

        for (int y = 0; y < getAppHeight(); y += 10) {
            spawnPoints.add(new Point2D(0, y));
            spawnPoints.add(new Point2D(getAppWidth(), y));
        }

        for (int x = 0; x < getAppWidth(); x += 10) {
            spawnPoints.add(new Point2D(x, 0));
            spawnPoints.add(new Point2D(x, getAppHeight()));
        }

        points = IntStream.rangeClosed(0, getAppWidth())
                .filter(i -> i % 2 == 0)
                .map(x -> x - getAppWidth() / 2)
                .mapToDouble(x -> x / PIXELS_PER_UNIT)
                .mapToObj(x -> new Point2D(x * PIXELS_PER_UNIT + getAppWidth() / 2.0, getAppHeight() - (func.apply(x) * PIXELS_PER_UNIT + getAppHeight() / 2.0)))
                .filter(p -> new Rectangle2D(0, 0, getAppWidth(), getAppHeight()).contains(p))
                .collect(Collectors.toList());

        emitter = ParticleEmitters.newExplosionEmitter(300);

        emitter.setBlendMode(BlendMode.ADD);

        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setNumParticles(750);
        emitter.setEmissionRate(0.02);
        emitter.setSize(1, 24);
        emitter.setSpawnPointFunction(i -> FXGLMath.random(spawnPoints).get());

        emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.01));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.55, 2.5)));
        emitter.setAccelerationFunction(() -> Point2D.ZERO);
        //emitter.setVelocityFunction(i -> Point2D.ZERO);
        emitter.setVelocityFunction(i -> FXGLMath.randomPoint2D().multiply(random(1, 45)));

        emitter.setSourceImage(texture("particles/circle_05.png", 32, 32).multiplyColor(Color.ORANGERED));
        emitter.setAllowParticleRotation(true);

        emitter.setControl(p -> {

            var x = p.position.x;
            var y = p.position.y;

            var point = dest.getOrDefault(p,
                    FXGLMath.random(points).get()
            );

            dest.put(p, point);

            var v = point.subtract(x, y).normalize().multiply(0.016 * 1500);

            p.velocity.x = p.velocity.x * 0.8f + (float) v.getX() * 0.2f;
            p.velocity.y = p.velocity.y * 0.8f + (float) v.getY() * 0.2f;
        });

        var e = entityBuilder()
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

//        e.xProperty().bind(getInput().mouseXWorldProperty().subtract(10));
//        e.yProperty().bind(getInput().mouseYWorldProperty().subtract(10));


        var lineX = new Line(0, getAppHeight() / 2.0, getAppWidth(), getAppHeight() / 2.0);
        lineX.setStrokeType(StrokeType.OUTSIDE);
        lineX.setStrokeWidth(3);
        lineX.setStroke(Color.WHITE);

        var lineY = new Line(getAppWidth() / 2.0, 0, getAppWidth() / 2.0, getAppHeight());
        lineY.setStrokeType(StrokeType.OUTSIDE);
        lineY.setStrokeWidth(3);
        lineY.setStroke(Color.WHITE);

//        addUINode(lineX);
//        addUINode(lineY);
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
