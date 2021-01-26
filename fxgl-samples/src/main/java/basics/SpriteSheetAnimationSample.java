/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package basics;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class SpriteSheetAnimationSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new ZombieFactory());

        spawn("zombie");
    }

    public static class ZombieFactory implements EntityFactory {

        @Spawns("zombie")
        public Entity newZombie(SpawnData data) {
            var channel = new AnimationChannel(List.of(
                    image("anim/Attack (1).png"),
                    image("anim/Attack (2).png"),
                    image("anim/Attack (3).png"),
                    image("anim/Attack (4).png")
            ), Duration.seconds(0.5));

            return entityBuilder()
                    .view(new AnimatedTexture(channel).loop())
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
