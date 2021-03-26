/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ReusableEntitySample extends GameApplication {

    private Entity e1;
    private Entity e2;

    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.F, () -> {
            getGameWorld().removeEntity(e1);

            runOnce(() -> {
                getGameWorld().addEntity(e1);
            }, Duration.seconds(0.1));
        });

        onKeyDown(KeyCode.G, () -> {
            // should be fine, reusable
            getGameWorld().removeEntity(e2);

            runOnce(() -> {
                getGameWorld().addEntity(e2);
            }, Duration.seconds(0.1));
        });

        onKeyDown(KeyCode.T, () -> {
            getGameWorld().getEntitiesCopy().forEach(Entity::removeFromWorld);
        });

        onKeyDown(KeyCode.H, () -> {
            // should be fine, reusable
            spawn("e", getInput().getMousePositionWorld());
        });
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new ReusableFactory());

        e1 = entityBuilder()
                .at(100, 100)
                .view(new Rectangle(40, 40))
                .buildAndAttach();

        e2 = entityBuilder()
                .at(200, 100)
                .view(new Rectangle(40, 40))
                .buildAndAttach();

        e2.setReusable(true);
    }

    public static class ReusableFactory implements EntityFactory {
        @Spawns("e")
        public Entity spawnE(SpawnData data) {
            System.out.println("Calling factory");

            var e = entityBuilder(data)
                    .view(new Rectangle(20, 20, Color.BLUE))
                    .build();

            e.setReusable(true);

            return e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}