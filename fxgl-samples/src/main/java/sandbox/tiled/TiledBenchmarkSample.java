/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.tiled;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * This is a sample for testing the performance of tiled map loading
 *
 * @author Adam Bocco (adam.bocco) (adam.bocco@gmail.com)
 */
public class TiledBenchmarkSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {

        settings.setWidth(500);
        settings.setHeight(500);
        settings.setTitle("TiledBenchmarkSample");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new TiledObjectFactory());

        var t1engine = getEngineTimer().getNow();
        var t1sys = System.nanoTime();

        setLevelFromMap("tmx/benchmarking/csv_1layer_noflip_500x500.tmx");
//        setLevelFromMap("tmx/benchmarking/gzip_5layers_noflip_500x500.tmx");
//        setLevelFromMap("tmx/benchmarking/gzip_10layers_noflip_500x500.tmx");
//        setLevelFromMap("tmx/benchmarking/csv_1layer_verticalflip_500x500.tmx");
//        setLevelFromMap("tmx/benchmarking/csv_1layer_horizontalflip_500x500.tmx");
//        setLevelFromMap("tmx/benchmarking/csv_1objectlayer_noflip_100x100.tmx");
//        setLevelFromMap("tmx/benchmarking/csv_1objectlayer_verticalflip_100x100.tmx");
//        setLevelFromMap("tmx/benchmarking/csv_1objectlayer_horizontalflip_100x100.tmx");

        var t2engine = getEngineTimer().getNow();
        var t2sys = System.nanoTime();

        System.out.println("<------ setLevelFromMap() performance ------>");

        System.out.println("System Time: " + (t2engine - t1engine));

        System.out.println("Engine Time: " + ((t2sys - t1sys)/1e9));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class TiledObjectFactory implements EntityFactory {

        @Spawns("")
        public Entity spawnTiledObject(SpawnData data) {
            return entityBuilder(data).build();
        }
    }
}
