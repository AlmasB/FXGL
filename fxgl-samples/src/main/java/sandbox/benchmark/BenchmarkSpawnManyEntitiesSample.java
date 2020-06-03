/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.benchmark;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.TimeComponent;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import kotlin.Unit;
import kotlin.system.TimingKt;

import java.util.Map;

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
        settings.setEntityPreloadEnabled(true);
    }

    private static final int NUM_OBJECTS = 5000;

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            var time = TimingKt.measureNanoTime(() -> {
                for (int i = 0; i < NUM_OBJECTS; i++) {
                    spawn("ball");
                }

                return Unit.INSTANCE;
            });

            System.out.println("Add: " + time / 1000000000.0);

            runOnce(() -> {
                var time2 = TimingKt.measureNanoTime(() -> {
                    getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);

                    return Unit.INSTANCE;
                });

                System.out.println("Remove: " + time2 / 1000000000.0);
            }, Duration.seconds(0.2));
        });
    }

    /*
Add: 1.1907733
Remove: 0.1876595
Add: 0.6326357
Remove: 0.1069702
Add: 0.3467802
Remove: 0.085884
Add: 0.3773479
Remove: 0.0679462
Add: 0.2587072
Remove: 0.0661315
     */

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("loading", 0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BenchmarkFactory2());
    }

    @Override
    protected void initUI() {
        var text = getUIFactoryService().newText("", Color.BLACK, 18.0);
        getip("loading").addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> text.setText("" + newValue));
        });

        addUINode(text, 200, 50);
    }

    public static class BenchmarkFactory2 implements EntityFactory {

        @Spawns("ball")
        public Entity newEntity(SpawnData data) {
            var e = entityBuilder(data)
                    .viewWithBBox(texture("ball.png", 100, 100))
                    .with(new ProjectileComponent(new Point2D(1, 0), 0))
                    .with(new TimeComponent())
                    .build();

            e.setUpdateEnabled(false);

            return e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
