/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import kotlin.Unit;
import kotlin.system.TimingKt;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * A benchmark demo that uses core FXGL features and provides stats.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BenchmarkSpawnManyEntitiesSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setProfilingEnabled(true);
    }

    private static final int NUM_OBJECTS = 2500;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            var time = TimingKt.measureNanoTime(() -> {
                for (int i = 0; i < NUM_OBJECTS; i++) {
                    spawn("ball");
                }

                return Unit.INSTANCE;
            });

            System.out.println(time / 1000000000.0);

            run(() -> getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld), Duration.seconds(0.2));
        });
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BenchmarkFactory2());
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    public static class BenchmarkFactory2 implements EntityFactory {

        @Spawns("ball")
        public Entity newEntity(SpawnData data) {
            return entityBuilder(data)
                    .viewWithBBox(texture("ball.png", 100, 100))
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
