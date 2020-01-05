/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.animation.AnimatedPoint2D;
import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.ImagesKt;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to use input service and bind actions to triggers.
 */
public class AnimSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("AnimSample");
        settings.setVersion("0.1");
        settings.setApplicationMode(ApplicationMode.DEBUG);
    }

    private Animation<?> anim;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "f", () -> {
            anim.onUpdate(0.5);

            getGameWorld().getEntities().forEach(e -> {
                System.out.println(e.getPosition());
            });
        });
        onKeyDown(KeyCode.G, "g", () -> anim.stop());
        onKeyDown(KeyCode.Q, "q", () -> anim.pause());
        onKeyDown(KeyCode.E, "e", () -> anim.resume());
    }

    private LazyValue<Image> image = new LazyValue<>(() -> {
        var images = IntStream.rangeClosed(1, 8)
                .mapToObj(i -> image("anim/Attack (" + i + ").png"))
                .collect(Collectors.toList());

        return ImagesKt.merge(images);
    });

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new EFactory());

        Entity e = entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40, Color.BLUE))
                .buildAndAttach();

        anim = animationBuilder()
                .duration(Duration.seconds(2))
                .translate(e)
                .from(new Point2D(100, 100))
                .to(new Point2D(200, 100))
                .build();
        anim.start();

        // animation channel from multiple images

        var channel = new AnimationChannel(image.get(), Duration.seconds(1), 8);

        entityBuilder()
                .at(200, 50)
                .view(new AnimatedTexture(channel).loop())
                .buildAndAttach();

        // generic animation builder
        animationBuilder()
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .duration(Duration.seconds(2))
                .onFinished(() -> System.out.println("Done!"))
                .animate(new AnimatedPoint2D(new Point2D(50, 50), new Point2D(200, 200)))
                .onProgress(value -> System.out.println(value))
                .buildAndPlay();
    }

    public static class EFactory implements EntityFactory {

        @Spawns("block")
        public Entity newBlock(SpawnData data) {
            return entityBuilder()
                    .from(data)
                    .view(new Rectangle(40, 40, Color.BLUE))
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
