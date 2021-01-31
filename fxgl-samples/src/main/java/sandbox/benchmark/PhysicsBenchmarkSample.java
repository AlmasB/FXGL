/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.benchmark;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * A benchmark demo that uses lightweight FXGL physics.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PhysicsBenchmarkSample extends GameApplication {

    private static final int NUM_OBJECTS = 140;

    private int frames;
    private int numCollisions = 0;

    private Text text;

    private enum Type {
        BALL
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setProfilingEnabled(true);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("collisions", 0);
    }

    @Override
    protected void initGame() {
        frames = 0;

        getGameWorld().addEntityFactory(new BenchmarkFactory());

        for (int i = 0; i < NUM_OBJECTS; i++) {
            spawn("ball", FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 64, getAppHeight() - 64)));
        }

        text = new Text("");
        text.setFont(Font.font(32));

        addUINode(text, 100, getAppHeight() - 40);
    }

    @Override
    protected void initPhysics() {
        onCollision(Type.BALL, Type.BALL, (b1, b2) -> {
            numCollisions++;
        });
    }

    @Override
    protected void onUpdate(double tpf) {
        text.setText("Collisions: " + numCollisions);
        numCollisions = 0;

        frames++;

        // at 60fps this should finish in 100 sec
        if (frames == 6000) {
            getGameController().exit();
        }
    }

    public static class BenchmarkFactory implements EntityFactory {

        @Spawns("ball")
        public Entity newEntity(SpawnData data) {
            return entityBuilder(data)
                    .type(Type.BALL)
                    .viewWithBBox("brick.png")
                    .collidable()
                    .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth() - 64, getAppHeight() - 64), random(10, 200)))
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
