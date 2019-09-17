/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BmpSample extends GameApplication {

    private enum Type {
        PLAYER, CRYSTAL
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1066);
        settings.setTitle("Crystal Chase");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("entities", 0);
        vars.put("tpf", 0.0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new MyFactory());
        getGameScene().setBackgroundColor(Color.BLACK);

        var text = getUIFactory().newText("", Color.WHITE, 24.0);
        text.textProperty().bind(getip("entities").asString("Entities: %d"));

        addUINode(text, 25, 25);

        var text2 = getUIFactory().newText("", Color.WHITE, 24.0);
        text2.textProperty().bind(new SimpleDoubleProperty(1.0).divide(getdp("tpf")).asString("FPS: %.2f"));

        addUINode(text2, 25, 50);

        run(this::spawnCrystal, Duration.seconds(3));
    }

    private void spawnCrystal() {
        int numToSpawn = 100;

        for (int i = 0; i < numToSpawn; i++) {
            spawn("crystal");
        }

        inc("entities", +numToSpawn);
    }

    @Override
    protected void onUpdate(double tpf) {
        set("tpf", tpf);
    }

    public static class MyFactory implements EntityFactory {

        @Preload(100)
        @Spawns("crystal")
        public Entity newEntity(SpawnData data) {
            return entityBuilder().at(FXGLMath.randomPoint(new Rectangle2D(0, 0, getAppWidth() - 55, getAppHeight() - 55)))
                    .type(Type.CRYSTAL)
                    .viewWithBBox(texture("ball.png", 32, 32))
                    .with(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight()), 250))
                    //.view(texture("YellowCrystal.png").toAnimatedTexture(8, Duration.seconds(0.66)).loop())
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
