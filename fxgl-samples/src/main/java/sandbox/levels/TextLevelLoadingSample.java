/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.levels;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class TextLevelLoadingSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) { }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new MyEntityFactory());

        Level level = getAssetLoader().loadLevel("test_level.txt", new TextLevelLoader(30, 30, '0'));

        getGameWorld().setLevel(level);

        spawn("rect", 750, 550);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
