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
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleShowcaseSample3 extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidthFromRatio(16/9.0);
    }

    ParticleEmitter emitter;
    Entity e;

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

    int index = 0;

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

        onKeyDown(KeyCode.R, () -> {
            emitter.setAllowParticleRotation(!emitter.isAllowParticleRotation());
        });

        onKeyDown(KeyCode.F, () -> {
            toggleEmitter();
        });
    }

    private void toggleEmitter() {
        var particleComponent = e.getComponent(ParticleComponent.class);
        if (particleComponent.isEmitterPaused()) {
            particleComponent.resumeEmitter();
        } else {
            particleComponent.pauseEmitter();
        }
    }

    double t = 0.0;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        emitter = ParticleEmitters.newExplosionEmitter(300);

        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setNumParticles(90);
        emitter.setEmissionRate(0.25);
        emitter.setSize(1, 16);
        emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.01));
        emitter.setExpireFunction(i -> Duration.seconds(random(0.25, 0.5)));
        emitter.setAccelerationFunction(() -> Point2D.ZERO);
        emitter.setVelocityFunction(i -> FXGLMath.randomPoint2D().multiply(random(0.25, 5.0)));

        emitter.setSpawnPointFunction(i -> Vec2.fromAngle(i * 4).toPoint2D().multiply(90).add(70, 70));

        //emitter.setVelocityFunction(i -> FXGLMath.randomPoint2D().multiply(random(1, 45)));

        //emitter.setSourceImage(texture("particles/star_09.png", 32, 32));
        emitter.setAllowParticleRotation(true);

//        emitter.setControl(p -> {
//            var x = p.position.x;
//            var y = p.position.y;
//
//            var noiseValue = FXGLMath.noise2D(x * 0.002 * t, y * 0.002 * t);
//            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);
//
//            angle %= 360.0;
//
//            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 25));
//
//            var vx = p.velocity.x * 0.8f + v.x * 0.2f;
//            var vy = p.velocity.y * 0.8f + v.y * 0.2f;
//
//            p.velocity.x = vx;
//            p.velocity.y = vy;
//        });


        var circle = new Circle(80, Color.LIME);
        circle.setStrokeWidth(2.5);

        var stackPane = new StackPane(circle, getUIFactoryService().newText("Play", Color.WHITESMOKE, 58));

        e = entityBuilder()
                .at(150, 150)
                .view(stackPane)
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

        //e.xProperty().bind(getInput().mouseXWorldProperty().subtract(10));
        //e.yProperty().bind(getInput().mouseYWorldProperty().subtract(10));

        e.getViewComponent().getParent().hoverProperty().addListener((observableValue, old, isHover) -> {
            var particleComponent = e.getComponent(ParticleComponent.class);

            if (isHover) {
                particleComponent.resumeEmitter();
            } else {
                particleComponent.pauseEmitter();
            }
        });

        circle.strokeProperty().bind(
                Bindings.when(e.getViewComponent().getParent().hoverProperty())
                        .then(Color.LIGHTGREEN)
                        .otherwise(Color.GRAY)
        );

        circle.fillProperty().bind(
                Bindings.when(e.getViewComponent().getParent().hoverProperty())
                        .then(Color.GREEN)
                        .otherwise(Color.LIME)
        );
    }

    boolean up = true;

    @Override
    protected void onUpdate(double tpf) {
        if (up) {
            t += tpf;
        } else {
            t -= tpf;
        }

        if (t > 7) {
            up = false;
        }

        if (t < 1) {
            up = true;
        }

//        e.setX(e.getX() * 0.9 + getInput().getMouseXWorld() * 0.1);
//        e.setY(e.getY() * 0.9 + getInput().getMouseYWorld() * 0.1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
