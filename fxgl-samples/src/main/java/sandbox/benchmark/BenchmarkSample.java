/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.benchmark;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.component.ComponentHelper;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * A benchmark demo that uses core FXGL features and provides stats.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BenchmarkSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setProfilingEnabled(true);
    }

    private static final int NUM_OBJECTS = 2500;
    private static final boolean IS_JAVAFX = true;

    private int frames;
    private Component[] components;

    private Texture[] textures;

    @Override
    protected void initGame() {
        frames = 0;
        components = new Component[NUM_OBJECTS];
        textures = new Texture[NUM_OBJECTS];

        getGameWorld().addEntityFactory(new BenchmarkFactory());

        for (int i = 0; i < NUM_OBJECTS; i++) {
            if (IS_JAVAFX) {
                var t = texture("ball.png", 100, 100);
                textures[i] = t;

                addUINode(t, random(100, 500), random(100, 500));
            } else {
                var e = spawn("ball");
                e.setUpdateEnabled(false);

                //var c = new ProjectileComponent(new Point2D(random(0.0, 1.0), random(0.0, 1.0)), FXGLMath.map(i, 0, 2000, 10, 60));
                var c = new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), random(10, 400));
                components[i] = c;

                ComponentHelper.setEntity(c, e);
            }
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        frames++;

        // at 60fps this should finish in 100 sec
        if (frames == 6000) {
            getGameController().exit();
        }

        if (IS_JAVAFX) {
            for (var t : textures) {
                t.setTranslateX(t.getTranslateX() + 1);
            }
        } else {
            for (var c : components) {
                c.onUpdate(tpf);
            }
        }
    }

    public static class BenchmarkFactory implements EntityFactory {

        @Spawns("ball")
        public Entity newEntity(SpawnData data) {
            return entityBuilder()
                    .from(data)
                    .viewWithBBox(texture("ball.png", 100, 100))
                    //.with(new ProjectileComponent(new Point2D(1, 1), 5))
                    //.with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), random(10, 200)))
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
