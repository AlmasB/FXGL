/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.particles;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ParticleScaleSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        ParticleEmitter emitter = ParticleEmitters.newFireEmitter();
        emitter.setSpawnPointFunction(i -> new Point2D(0, 0));
        emitter.setSourceImage(texture("brick.png"));
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setSize(12, 14);
        emitter.setScaleFunction(i -> new Point2D(0, 0));

        emitter.setEntityScaleFunction(() -> new Point2D(1.75, 1.75));
        emitter.setScaleOriginFunction(i -> new Point2D(12, 32));

        var e = entityBuilder()
                .at(250, 250)
                .viewWithBBox("brick.png")
                .scale(1.75, 1.75)
                .with(new ParticleComponent(emitter))
                .buildAndAttach();

        e.getTransformComponent().setScaleOrigin(new Point2D(12, 32));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
