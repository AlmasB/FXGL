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
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.scene.effect.Bloom;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleShowcaseSample2 extends GameApplication {
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
    private int interpolatorIndex = 0;
    private Text debug;

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

        onBtnDown(MouseButton.PRIMARY, () -> {
            emitter = ParticleEmitters.newExplosionEmitter(60);

            emitter.setMaxEmissions(3);
            emitter.setNumParticles(350);
            emitter.setEmissionRate(0.86);
            emitter.setSize(1, 24);
            emitter.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.02));
            emitter.setExpireFunction(i -> Duration.seconds(random(1.25, 3.5)));

            var i = Interpolators.values()[interpolatorIndex++];

            if (interpolatorIndex == Interpolators.values().length)
                interpolatorIndex = 0;

            debug.setText("Easing (out): " + i.toString());

            emitter.setInterpolator(i.EASE_OUT());
            emitter.setAccelerationFunction(() -> new Point2D(0, random(1, 3)));
            //emitter.setVelocityFunction(i -> Point2D.ZERO);
            //emitter.setVelocityFunction(i -> FXGLMath.randomPoint2D().multiply(random(1, 15)));

            var name = names[index++];

            System.out.println("Using " + name);


            if (index == names.length) {
                index = 0;
            }

            // FXGLMath.randomColor();
            var c = Color.YELLOW;
            // name before
            emitter.setSourceImage(texture("particles/" + "flare_01.png", 32, 32).multiplyColor(c));
            emitter.setAllowParticleRotation(true);

//            emitter.setControl(p -> {
//                var x = p.position.x;
//                var y = p.position.y;
//
//                var noiseValue = FXGLMath.noise2D(x * 0.002 * t, y * 0.002 * t);
//                var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);
//
//                angle %= 360.0;
//
//                var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 15));
//
//                var vx = p.velocity.x * 0.8f + v.x * 0.2f;
//                var vy = p.velocity.y * 0.8f + v.y * 0.2f;
//
//                p.velocity.x = vx;
//                p.velocity.y = vy;
//            });

            e = entityBuilder()
                    .at(getAppWidth() / 2, getAppHeight() / 2)
                    .with(new ParticleComponent(emitter))
                    .with(new ExpireCleanComponent(Duration.seconds(6)))
                    .buildAndAttach();

            //e.xProperty().bind(getInput().mouseXWorldProperty().subtract(10));
            //e.yProperty().bind(getInput().mouseYWorldProperty().subtract(10));

//            var tank = entityBuilder()
//                    .type(Type.PLAYER)
//                    .at(e.getPosition().subtract(16, 16))
//                    .viewWithBBox(texture("tank_player.png"))
//                    .with(new ExpireCleanComponent(Duration.seconds(3)))
//                    .scale(0, 0)
//                    .buildAndAttach();
//
//            tank.getViewComponent().setOpacity(0);
//
//            animationBuilder()
//                    .delay(Duration.seconds(0.5))
//                    .fadeIn(tank)
//                    .buildAndPlay();
//
//            animationBuilder()
//                    .delay(Duration.seconds(0.5))
//                    .scale(tank)
//                    .from(new Point2D(0, 0))
//                    .to(new Point2D(1, 1))
//                    .buildAndPlay();
        });

        onKeyDown(KeyCode.F, () -> {
            entityBuilder()
                    .type(Type.BULLET)
                    .at(getInput().getMouseXWorld(), getInput().getMouseYWorld())
                    .viewWithBBox("tank_bullet.png")
                    .with(new ProjectileComponent(new Point2D(1, 0), 870))
                    .collidable()
                    .buildAndAttach();
        });
    }

    double t = 0.0;

    private enum Type {
        BULLET, ENEMY, PLAYER
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);
        //getGameScene().setCursorInvisible();

        var shield = new Circle(32, 32, 45, null);
        shield.setStroke(Color.LIGHTBLUE);
        shield.setStrokeWidth(2.5);
        shield.setEffect(new Bloom());

//        entityBuilder()
//                .at(700, 300)
//                .type(Type.ENEMY)
//                .view("tank_enemy.png")
//                .viewWithBBox(shield)
//                .collidable()
//                .buildAndAttach();
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(Type.BULLET, Type.ENEMY, (bullet, enemy) -> {
            bullet.removeFromWorld();

            spawnParticles(enemy);
        });
    }

    private void spawnParticles(Entity enemy) {
        var emitter1 = ParticleEmitters.newExplosionEmitter(30);

        emitter1.setMaxEmissions(1);
        emitter1.setNumParticles(650);
        emitter1.setEmissionRate(0.86);
        emitter1.setSize(1, 14);
        emitter1.setScaleFunction(i -> FXGLMath.randomPoint2D().multiply(0.01));
        emitter1.setExpireFunction(i -> Duration.seconds(random(0.25, 3)));
        emitter1.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
        emitter1.setAccelerationFunction(() -> new Point2D(0, random(0, 1)));
        //emitter1.setVelocityFunction(i -> Point2D.ZERO);
        //emitter1.setVelocityFunction(i -> FXGLMath.randomPoint2D().multiply(random(5, 25)));

        var name = names[index++];

        System.out.println("Using " + name);


        if (index == names.length) {
            index = 0;
        }

//        var c = FXGLMath.randomColor();
        var c = Color.ORANGERED;
        emitter1.setSourceImage(texture("particles/" + "circle_05.png", 32, 32).multiplyColor(c));
        emitter1.setAllowParticleRotation(true);

        emitter1.setControl(p -> {
            var x = p.position.x;
            var y = p.position.y;

            var noiseValue = FXGLMath.noise2D(x * 0.002 * t, y * 0.002 * t);
            var angle = FXGLMath.toDegrees((noiseValue + 1) * Math.PI * 1.5);

            angle %= 360.0;

            var v = Vec2.fromAngle(angle).normalizeLocal().mulLocal(FXGLMath.random(1.0, 5));

            var vx = p.velocity.x * 0.8f + v.x * 0.2f;
            var vy = p.velocity.y * 0.8f + v.y * 0.2f;

            p.velocity.x = vx;
            p.velocity.y = vy;
        });

        entityBuilder()
                .at(enemy.getPosition().add(16, 16))
                .with(new ParticleComponent(emitter1))
                .with(new ExpireCleanComponent(Duration.seconds(6)))
                .buildAndAttach();
    }

    @Override
    protected void initUI() {
        debug = getUIFactoryService().newText("", Color.WHITE, 24.0);

        addUINode(debug, 100, 100);
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
