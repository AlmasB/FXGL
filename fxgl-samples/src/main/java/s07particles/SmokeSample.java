/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package s07particles;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.effect.ParticleControl;
import com.almasb.fxgl.effect.ParticleEmitter;
import com.almasb.fxgl.effect.ParticleEmitters;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.control.RandomMoveControl;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
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
public class SmokeSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SmokeSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setCloseConfirmation(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    private ParticleEmitter emitter, e;
    private GameEntity entity;

    @Override
    protected void initInput() {
        getInput().addAction(new UserAction("Change Color") {
            @Override
            protected void onActionBegin() {
                Color randomColor = Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random());
                e.setStartColor(randomColor);
                e.setEndColor(Color.color(FXGLMath.random(), FXGLMath.random(), FXGLMath.random()));
            }
        }, MouseButton.PRIMARY);
    }

    @Override
    protected void initGame() {
        Entities.builder()
                .viewFromNode(new Rectangle(getWidth(), getHeight()))
                .buildAndAttach(getGameWorld());

        e = ParticleEmitters.newSmokeEmitter();
        e.setBlendMode(BlendMode.SRC_OVER);
        e.setSize(15, 30);
        e.setNumParticles(10);
        e.setEmissionRate(0.25);
        e.setStartColor(Color.color(0.6, 0.55, 0.5, 0.47));
        e.setEndColor(Color.BLACK);
        e.setExpireFunction((i, x, y) -> Duration.seconds(16));
        e.setVelocityFunction((i, x, y) -> new Point2D(FXGLMath.random() - 0.5, 0));
        e.setGravityFunction(() -> new Point2D((FXGLMath.noise1D(7776 + getTick()) - 0.5) * 0.02, 0));
        //e.setSpawnPointFunction((i, x, y) -> new Point2D(x + FXGLMath.noise1D(333 + getTick()) * 150 - 75, y + FXGLMath.noise1D(getTick()) * 150 - 75));

        Entities.builder()
                .at(getWidth() / 2, getHeight() - 100)
                .with(new ParticleControl(e), new RandomMoveControl(2))
                .buildAndAttach(getGameWorld());


        emitter = ParticleEmitters.newFireEmitter();
        emitter.setSize(5, 15);
        emitter.setVelocityFunction((i, x, y) -> new Point2D(FXGLMath.random() - 0.5, -FXGLMath.random() * 3));
        emitter.setGravityFunction(() -> new Point2D(0, 0.05));
        emitter.setExpireFunction((i, x, y) -> Duration.seconds(3));
        emitter.setScaleFunction((i, x, y) -> new Point2D(FXGLMath.random(0, 0.01f), FXGLMath.random(-0.05f, 0.05f)));
        emitter.setStartColor(Color.YELLOW);
        emitter.setEndColor(Color.RED);
        emitter.setBlendMode(BlendMode.SRC_OVER);

        //emitter.setSourceImage(getAssetLoader().loadTexture("particleTexture2.png").toColor(Color.rgb(230, 75, 40)).getImage());

        entity = Entities.builder()
                .at(getWidth() / 2, getHeight() / 2)
                //.with(new ParticleControl(emitter))
                .buildAndAttach(getGameWorld());
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
