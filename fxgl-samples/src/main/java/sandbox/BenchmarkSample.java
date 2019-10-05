/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class BenchmarkSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, "spawn", () -> {
            spawn();
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("tpf", 0.0);
    }

    private void spawn() {
        var start = System.nanoTime();

        for (int i = 0; i < 10000; i++) {
            getGameWorld().create("e", new SpawnData(0, 0));

            //var e = new Entity();

//            entityBuilder()
//                    .at(i * 6, i * 4)
//                    .buildAndAttach();
        }

        var result = System.nanoTime() - start;
        System.out.printf("%.3f sec\n", result / 1000000000.0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new BenchmarkFactory());

        var text = getUIFactory().newText("", Color.BLUE, 24.0);
        text.textProperty().bind(getdp("tpf").asString("%.3f"));

        addUINode(text, 100, 100);
    }

    @Override
    protected void onUpdate(double tpf) {
        set("tpf", tpf);
    }

    public static class BenchmarkFactory implements EntityFactory {

        @Preload(10000)
        @Spawns("e")
        public Entity newEntity(SpawnData data) {
            return new Entity();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
