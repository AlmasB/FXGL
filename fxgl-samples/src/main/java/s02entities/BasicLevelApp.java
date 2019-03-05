/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s02entities;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class BasicLevelApp extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {

    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new MyEntityFactory());

        Level level = FXGL.getAssetLoader().loadLevel("test_level.txt", new TextLevelLoader(30, 30, '0'));

        FXGL.getGameWorld().setLevel(level);

        FXGL.spawn("rect", 750, 550);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
