/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s07particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static java.lang.Math.*;

/**
 * Using particles with source images and colorization.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ParticleTextureSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("ParticleTextureSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private ParticleEmitter emitter;
    private Entity entity;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Change Color") {
            @Override
            protected void onActionBegin() {
                //emitter.setBlendMode(emitter.getBlendMode() == BlendMode.SRC_OVER ? BlendMode.ADD : BlendMode.SRC_OVER);
                emitter.setSourceImage(getAssetLoader().loadTexture("particleTexture2.png")
                        .multiplyColor(Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random())).getImage());
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .viewFromNode(new Rectangle(getWidth(), getHeight()))
                .buildAndAttach(getGameWorld());

        emitter = ParticleEmitters.newExplosionEmitter(100);
        emitter.setSize(5, 20);
        emitter.setNumParticles(24);
        emitter.setMaxEmissions(Integer.MAX_VALUE);
        emitter.setEmissionRate(0.1);
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(FXGLMath.random(2, 2)));
        emitter.setVelocityFunction((i, x, y) -> Vec2.fromAngle(360 / 24 *i).toPoint2D().multiply(100));
        emitter.setAccelerationFunction(() -> new Point2D(0, 30));
        emitter.setSourceImage(getAssetLoader().loadTexture("particleTexture2.png").multiplyColor(Color.rgb(230, 75, 40)).getImage());
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());

        entity = Entities.builder()
                .at(getWidth() / 2, getHeight() / 2)
                .with(new ParticleControl(emitter))
                //.with(new ButterflyControl())
                .buildAndAttach();

//        Entities.builder()
//                .at(520, 180)
//                .viewFromNode(new Circle(5, Color.WHITE))
//                .buildAndAttach();
//
//        Entities.builder()
//                .at(520, 252)
//                .viewFromNode(new Circle(5, Color.WHITE))
//                .buildAndAttach();
    }

    private class ButterflyControl extends Control {

        private double t = 0;

        @Override
        public void onUpdate(Entity entity, double tpf) {
            Entities.getPosition(entity).setValue(curveFunction().add(getWidth() / 2, getHeight() / 2));

            t += tpf;
        }

        private Point2D curveFunction() {
            double x = sin(t) * (pow(E, cos(t)) - 2 * cos(4*t) - pow(sin(t/12), 5));
            double y = cos(t) * (pow(E, cos(t)) - 2 * cos(4*t) - pow(sin(t/12), 5));

            return new Point2D(x, -y).multiply(85);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
