/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s01basics;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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
    }

    @Override
    protected void onUpdate(double tpf) {



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
