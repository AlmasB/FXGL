/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.view.TrailParticleComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MotionBlurSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidthFromRatio(16/9.0);
    }

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGRAY.darker().darker());

        getGameWorld().addEntityFactory(new MotionBlurFactory());

        for (int y = 0; y < 6; y++) {
            var e = spawn("ball", 0, y * 100);

            var text = "Linear;";

            if (y == 1) {
                text = "Linear; with trail";
            } else if (y == 2) {
                text = "Exponential;";
            } else if (y == 3) {
                text = "Exponential; with trail";
            } else if (y == 4) {
                text = "Elastic;";
            } else if (y == 5) {
                text = "Elastic; with trail";
            }

            addUINode(getUIFactory().newText(text, Color.WHITE, 24.0), 50, 80 + y * 100);

            var line = new Line(0, 100 + y*100, getAppWidth(), 100 + y*100);
            line.setStrokeWidth(2.5);
            line.setStroke(Color.WHITE);

            addUINode(line);
        }
    }

    public static class MotionBlurFactory implements EntityFactory {

        private int index = 0;

        @Spawns("ball")
        public Entity newBall(SpawnData data) {

            var e = entityBuilder()
                    .from(data)
                    .viewWithBBox(texture("ball.png", 64, 64))
                    //.with(new ParticleComponent(emitter))
                    .build();

            if (index % 2 == 1) {
                e.addComponent(new TrailParticleComponent(texture("ball.png", 64, 64)));
            }

            var interpolator = Interpolators.LINEAR.EASE_OUT();

            if (index == 2 || index == 3) {
                interpolator = Interpolators.EXPONENTIAL.EASE_OUT();
            } else if (index == 4 || index == 5) {
                interpolator = Interpolators.ELASTIC.EASE_OUT();
            }

            animationBuilder()
                    .interpolator(interpolator)
                    .duration(Duration.seconds(1.5))
                    .autoReverse(true)
                    .repeatInfinitely()
                    .translate(e)
                    .from(new Point2D(0, e.getY()))
                    .to(new Point2D(getAppWidth() - 70, e.getY()))
                    .buildAndPlay();

            index++;

//            var emitter = ParticleEmitters.newFireEmitter();
//            emitter.setSourceImage(texture("ball.png", 64, 64));
//            emitter.setBlendMode(BlendMode.SRC_OVER);
//            emitter.setNumParticles(1);
//            emitter.setEmissionRate(1);
//            emitter.setSpawnPointFunction(i -> new Point2D(0, 0));
//            emitter.setScaleFunction(i -> new Point2D(-0.1, -0.1));
//            emitter.setExpireFunction(i -> Duration.millis(110));
//            //emitter.setMinSize(64);
            //emitter.setMaxSize(64);

            //emitter.setEntityScaleFunction(() -> new Point2D(0.1, 0.1));
            //emitter.setScaleOriginFunction(i -> new Point2D(0, 0));
            //e.getTransformComponent().setScaleOrigin(new Point2D(0, 0));

            //emitter.minSizeProperty().bind(e.getTransformComponent().scaleXProperty().multiply(60));
            //emitter.maxSizeProperty().bind(e.getTransformComponent().scaleXProperty().multiply(60));

            return e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
