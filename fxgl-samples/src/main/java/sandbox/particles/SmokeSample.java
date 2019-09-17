/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Using particles with source images and colorization.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SmokeSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    private ParticleEmitter emitter, e;
    private Entity entity;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Change Color") {
            @Override
            protected void onActionBegin() {
                Color randomColor = Color.color(FXGLMath.randomDouble(), FXGLMath.randomDouble(), FXGLMath.randomDouble());
                emitter.setBlendMode(BlendMode.SRC_OVER);
                e.setStartColor(randomColor);
                e.setEndColor(Color.color(FXGLMath.randomDouble(), FXGLMath.randomDouble(), FXGLMath.randomDouble()));
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.BLACK);

        e = ParticleEmitters.newSmokeEmitter();
        e.setBlendMode(BlendMode.SRC_OVER);
        e.setSize(15, 30);
        e.setNumParticles(10);
        e.setEmissionRate(0.25);
        e.setStartColor(Color.color(0.6, 0.55, 0.5, 0.47));
        e.setEndColor(Color.BLACK);
        e.setExpireFunction(i -> Duration.seconds(16));
        e.setVelocityFunction(i -> new Point2D(FXGLMath.randomDouble() - 0.5, 0));

//        Entities.builder()
//                .at(getWidth() / 2, getHeight() - 100)
//                .with(new ParticleComponent(e), new RandomMoveControl(2))
//                .buildAndAttach(getGameWorld());


        emitter = ParticleEmitters.newFireEmitter();
//        emitter.setSize(5, 15);
//        emitter.setVelocityFunction(i -> new Point2D(FXGLMath.random() - 0.5, -FXGLMath.random() * 3));
//        emitter.setAccelerationFunction(() -> new Point2D(0, 0.05));
//        emitter.setExpireFunction(i -> Duration.seconds(3));
//        emitter.setScaleFunction(i -> new Point2D(FXGLMath.random(0, 0.01), FXGLMath.random(-0.05, 0.05)));
//        emitter.setStartColor(Color.YELLOW);
//        emitter.setEndColor(Color.RED);
//        emitter.setBlendMode(BlendMode.SRC_OVER);
//        emitter.setSourceImage(texture("particleTexture2.png").toColor(Color.rgb(230, 75, 40)).getImage());

        entity = entityBuilder()
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

//        entity = Entities.builder()
//                .at(getWidth() / 2, getHeight() / 2)
//                .with(new ParticleComponent(emitter))
//                .buildAndAttach(getGameWorld());
//
//
//
//        Entities.builder()
//                .at(250, 250)
//                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
//                .with(new CircularMovementComponent(10, 25))
//                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void onUpdate(double tpf) {
        entity.setX(getInput().getMouseXWorld() - 25);
        entity.setY(getInput().getMouseYWorld() - 25);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
