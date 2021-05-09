/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.benchmark;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionDetectionStrategy;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.Random;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * A benchmark demo that uses lightweight FXGL physics.
 * no rotation = 843
 * rotation = 970
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PhysicsBenchmarkSample extends GameApplication {

    private static final int NUM_OBJECTS = 300;

    private static Random random;

    private int frames;
    private int numCollisions = 0;

    private Text text;

    private enum Type {
        BALL, NOTHING, TYPE_A, TYPE_B, TYPE_C, TYPE_D, TYPE_E, TYPE_F
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setProfilingEnabled(true);
        settings.setCollisionDetectionStrategy(CollisionDetectionStrategy.BRUTE_FORCE);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("collisions", 0);
    }

    @Override
    protected void initGame() {
        random = new Random(225L);
        frames = 0;

        getGameWorld().addEntityFactory(new BenchmarkFactory());

        for (int i = 0; i < NUM_OBJECTS; i++) {
            var x = random.nextDouble() * (1280 - 64.0);
            var y = random.nextDouble() * (720 - 64.0);
            spawn("ball", x, y);
        }

        // with rotation
        getGameWorld().getEntities().forEach(e -> {
            e.setRotation(random.nextDouble() * 180.0);
        });

        text = new Text("");
        text.setFont(Font.font(32));

        addUINode(text, 100, getAppHeight() - 40);
    }

    @Override
    protected void initPhysics() {
        onCollision(Type.BALL, Type.BALL, (b1, b2) -> {
            numCollisions++;
        });

        // some extra handlers
        for (int i = 0; i < Type.values().length; i++) {
            for (int j = i + 1; j < Type.values().length; j++) {
                onCollision(Type.values()[i], Type.values()[j], (b1, b2) -> {
                    numCollisions++;
                });
            }
        }
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
                    //.type(FXGLMath.randomBoolean() ? Type.BALL : Type.NOTHING)
                    .view("brick.png")
                    .bbox(new HitBox(BoundingShape.box(64, 64)))
                    .collidable()
                    //.with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth() - 64, getAppHeight() - 64), random(10, 200)).withoutRotation())
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
