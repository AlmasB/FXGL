/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.input.KeyCode;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EngineTicksSample extends GameApplication {

    private ScheduledExecutorService executor;
    private int ticks = 0;

    @Override
    protected void initSettings(GameSettings settings) {
        //settings.setTicksPerSecond(10);
        settings.setProfilingEnabled(true);
    }

    @Override
    protected void initInput() {
        FXGL.onKeyDown(KeyCode.F, () -> {
            executor.shutdown();
        });
    }

    @Override
    protected void initGame() {
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            System.out.println("Num ticks: " + ticks);
            System.out.println("tpf: " + FXGL.tpf());
            ticks = 0;
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onUpdate(double tpf) {
        ticks++;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
