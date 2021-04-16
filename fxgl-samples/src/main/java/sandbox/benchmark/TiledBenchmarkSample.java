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
import com.almasb.fxgl.entity.level.tiled.TMXLevelLoader;
import javafx.scene.input.KeyCode;
import kotlin.Unit;
import kotlin.system.TimingKt;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class TiledBenchmarkSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            getExecutor().startAsync(() -> {
                for (int i = 0; i < 6; i++) {
                    var time = TimingKt.measureNanoTime(() -> {
                        var level = getAssetLoader().loadLevel("tmx/zeph/test_map.tmx", new TMXLevelLoader());

                        return Unit.INSTANCE;
                    });

                    System.out.printf("sec: %.2f\n", time / 1_000_000_000.0);
                }
            });
        });
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new TiledBenchmarkFactory());
    }

    public static class TiledBenchmarkFactory implements EntityFactory {

        @Spawns("nav,portal")
        public Entity newObject(SpawnData data) {
            return entityBuilder(data)
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
