/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s07particles;

import com.almasb.fxgl.app.DSLKt;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.particle.ParticleControl;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.extra.entity.controls.CircularMovementControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Using particles with source images and colorization.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SmokeSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SmokeSample");
        settings.setVersion("0.1");






    }

    private ParticleEmitter emitter, e;
    private Entity entity;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Change Color") {
            @Override
            protected void onActionBegin() {
                Color randomColor = Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random());
                emitter.setBlendMode(BlendMode.SRC_OVER);
                e.setStartColor(randomColor);
                e.setEndColor(Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random()));
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
        e.setVelocityFunction(i -> new Point2D(FXGLMath.random() - 0.5, 0));
        e.setAccelerationFunction(() -> new Point2D((FXGLMath.noise1D(7776 + getTick()) - 0.5) * 0.02, 0));
        //e.setSpawnPointFunction(i -> new Point2D(x + FXGLMath.noise1D(333 + getTick()) * 150 - 75, y + FXGLMath.noise1D(getTick()) * 150 - 75));

//        Entities.builder()
//                .at(getWidth() / 2, getHeight() - 100)
//                .with(new ParticleControl(e), new RandomMoveControl(2))
//                .buildAndAttach(getGameWorld());


        emitter = ParticleEmitters.newFireEmitter();
        emitter.setSize(5, 15);
        emitter.setVelocityFunction(i -> new Point2D(FXGLMath.random() - 0.5, -FXGLMath.random() * 3));
        emitter.setAccelerationFunction(() -> new Point2D(0, 0.05));
        emitter.setExpireFunction(i -> Duration.seconds(3));
        emitter.setScaleFunction(i -> new Point2D(FXGLMath.random(0, 0.01), FXGLMath.random(-0.05, 0.05)));
        emitter.setStartColor(Color.YELLOW);
        emitter.setEndColor(Color.RED);
        //emitter.setBlendMode(BlendMode.SRC_OVER);

        //emitter.setSourceImage(getAssetLoader().loadTexture("particleTexture2.png").toColor(Color.rgb(230, 75, 40)).getImage());

        entity = Entities.builder()
                .at(getWidth() / 2, getHeight() / 2)
                .with(new ParticleControl(emitter))
                .buildAndAttach(getGameWorld());



        Entities.builder()
                .at(250, 250)
                .viewFromNode(new Rectangle(40, 40, Color.BLUE))
                .with(new CircularMovementControl(10, 25))
                .buildAndAttach(getGameWorld());
    }

    private Text debug;

    @Override
    protected void initUI() {
        debug = getUIFactory().newText("");

        DSLKt.centerText(debug);

        getGameScene().addUINode(debug);
    }

    @Override
    protected void onUpdate(double tpf) {
        //debug.setText(tpf + " at " + getMasterTimer().getFPS());

        entity.setX(getInput().getMouseXWorld() - 25);
        entity.setY(getInput().getMouseYWorld() - 25);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
