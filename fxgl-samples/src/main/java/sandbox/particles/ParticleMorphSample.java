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
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Pixel;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ParticleMorphSample extends GameApplication {

    private static final String MESSAGE = "FXGL 2023";

    private List<Pixel> pixels;
    private List<Rectangle> rects;
    private List<Rectangle> rects2;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            for (int i = 0; i < rects.size(); i++) {
                Rectangle r = rects.get(i);

                animationBuilder()
                        .autoReverse(true)
                        .repeat(2)
                        .delay(Duration.seconds(i * 0.0001))
                        .duration(Duration.seconds(2.7))
                        .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                        .rotate(r)
                        .origin(new Point2D(150, 250))
                        .to(random(-360, 360))
                        .buildAndPlay();
            }
        });

        onKeyDown(KeyCode.G, () -> {
            for (int i = 0; i < rects.size(); i++) {
                Rectangle r = rects.get(i);

                animationBuilder()
                        .autoReverse(true)
                        .repeat(2)
                        .delay(Duration.seconds(i * 0.0001))
                        .duration(Duration.seconds(2.7))
                        .interpolator(Interpolators.ELASTIC.EASE_OUT())
                        .translate(r)
                        .to(new Point2D(r.getTranslateX() + random(-150, 150), r.getTranslateY() + 2))
                        .buildAndPlay();
            }
        });

        onKeyDown(KeyCode.H, () -> {
            for (int i = 0; i < rects.size(); i++) {
                Rectangle r = rects.get(i);

                animationBuilder()
                        .autoReverse(true)
                        .repeat(2)
                        .delay(Duration.seconds(i * 0.0001))
                        .duration(Duration.seconds(2.7))
                        .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                        .scale(r)
                        .to(new Point2D(2.2, 2.2))
                        .buildAndPlay();
            }
        });
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        pixels = new ArrayList<>();
        rects = new ArrayList<>();
        rects2 = new ArrayList<>();

        var c1 = FXGLMath.randomColorHSB(0.7, 0.2).darker().darker();
        var c2 = Color.GREEN.brighter().brighter();

        Text text = getUIFactoryService().newText(MESSAGE, Color.BLACK, 122);
        ImagesKt.toPixels(ImagesKt.toImage(text))
                .stream()
                .filter(p -> !p.getColor().equals(Color.TRANSPARENT))
                .forEach(p -> {
                    pixels.add(p);

                    var r = new Rectangle(1, 1, Color.BLACK);
                    r.setTranslateX(p.getX() + 350);
                    r.setTranslateY(p.getY() + 250);
                    rects.add(r);

                    addUINode(r);
                });


        rects2.addAll(rects);


        Collections.sort(rects2, (o1, o2) -> {
            return (int) (o2.getTranslateX() - o1.getTranslateX() + o2.getTranslateY() - o1.getTranslateY());
        });



        run(() -> {

            if (rects2.isEmpty())
                return;

            for (int i = 0; i < 80; i++) {
                if (rects2.isEmpty())
                    return;

                var r = rects2.remove(rects2.size() - 1);
                r.setFill(c1);

                animationBuilder()
                        .autoReverse(true)
                        .repeat(3)
                        .duration(Duration.seconds(0.7))
                        .interpolator(Interpolators.BOUNCE.EASE_OUT())
                        .animate(r.fillProperty())
                        .from(c1)
                        .to(c2)
                        .buildAndPlay();
            }

        }, Duration.seconds(0.016));

        //spawnMajor(new Point2D(random(0, 1200), 300));

//        run(() -> {
//            spawnMajor(new Point2D(random(0, 1200), 700));
//        }, Duration.seconds(0.15));
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
                //.with(new ProjectileComponent(new Point2D(0, -1), 750))
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

        animationBuilder()
                .repeatInfinitely()
                .duration(Duration.seconds(3))
                //.interpolator(Interpolators.BOUNCE.EASE_OUT())
                .translate(e)
                .alongPath(new Circle(300, 300, 130))
//                .alongPath(new CubicCurve(
//                        e.getX(), e.getY(),
//                        1000, 300,
//                        400, 150,
//                        e.getX(), e.getY()
//                ))
                .buildAndPlay();

        run(() -> {
            explode(e);
        }, Duration.seconds(random(0.1, 0.2)));
    }

    private void explode(Entity e) {
        for (int i = 0; i < 1; i++) {
            spawnMinor(e.getPosition());
        }

        //e.removeFromWorld();
        //e.addComponent(new ExpireCleanComponent(Duration.seconds(0.3)));
    }

    private void spawnMinor(Point2D p) {
        var emitter = ParticleEmitters.newFireEmitter();
        emitter.setNumParticles(15);
        emitter.setExpireFunction(i -> Duration.seconds(random(1.25, 2.5)));
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());
        emitter.setAccelerationFunction(() -> new Point2D(random(1, 1.5), random(1, 1.5)));

        var c = FXGLMath.randomColor().brighter().brighter();

        emitter.setBlendMode(BlendMode.ADD);
        emitter.setSourceImage(texture("particles/" + "flare_01.png", 64, 64).multiplyColor(c));

        var e = entityBuilder()
                .at(p)
                .with(new ParticleComponent(emitter))
                .with(new ExpireCleanComponent(Duration.seconds(1)).animateOpacity())
                .buildAndAttach();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
