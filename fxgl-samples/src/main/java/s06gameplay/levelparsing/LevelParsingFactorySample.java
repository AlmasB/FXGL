/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.levelparsing;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.settings.GameSettings;

/**
 * Shows how to load a level with pre-defined entity factory.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LevelParsingFactorySample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("LevelParsingFactorySample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initGame() {

        // 1. create a parser and set it up with a factory
        // where you specify entity producers for each character
        TextLevelParser parser = new TextLevelParser(new LevelParsingFactory());

        // 2. parse the level file and set it
        Level level = parser.parse("level0.txt");

        getGameWorld().setLevel(level);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
