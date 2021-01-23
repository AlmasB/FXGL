/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.benchmark;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import javafx.geometry.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * A benchmark demo that uses core FXGL features and provides stats.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BenchmarkManyStaticEntitiesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setProfilingEnabled(true);
    }

    private static final int NUM_STATIC_OBJECTS = 5000;
    private static final int NUM_UPDATED_OBJECTS = 1000;

    private int frames;

    @Override
    protected void initGame() {
        frames = 0;

        getGameWorld().addEntityFactory(new BenchmarkFactory());

        for (int i = 0; i < NUM_STATIC_OBJECTS; i++) {
            spawn("wall", FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 100, getAppHeight() - 100)));
        }

        for (int i = 0; i < NUM_UPDATED_OBJECTS; i++) {
            spawn("wall", FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 100, getAppHeight() - 100)));
            spawn("ball", FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 64, getAppHeight() - 64)));
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        frames++;

        // at 60fps this should finish in 100 sec
        if (frames == 6000) {
            getGameController().exit();
        }
    }

    public static class BenchmarkFactory implements EntityFactory {

        @Spawns("wall")
        public Entity newWall(SpawnData data) {
            return entityBuilder(data)
                    .viewWithBBox(texture("brick.png"))
                    .with(new CellMoveComponent(10, 10, 100))
                    .with(new AStarMoveComponent(new AStarGrid(100, 100)))
                    .with(new OffscreenPauseComponent())
                    .with(new KeepOnScreenComponent())
                    .with(new HealthIntComponent(100))
                    .with(new ManaIntComponent(100))
                    .with(new DraggableComponent())
                    .with(new EffectComponent())
                    .with(new ActionComponent())
                    .neverUpdated()
                    .build();
        }

        @Spawns("ball")
        public Entity newBall(SpawnData data) {
            return entityBuilder(data)
                    .viewWithBBox(texture("ball.png", 100, 100))
                    .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth() - 64, getAppHeight() - 64), 100, 100))
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
