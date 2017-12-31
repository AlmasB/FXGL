/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s06gameplay.levelparsing;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.parser.text.TextLevelParser;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Shows how to load a level.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LevelParsingSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("LevelParsingSample");
        settings.setVersion("0.1");






    }

    private static final int BLOCK_SIZE = 200;

    @Override
    protected void initGame() {

        // 1. create a parser and set it up
        TextLevelParser parser = new TextLevelParser('0', BLOCK_SIZE, BLOCK_SIZE);

        // 3. for each '1' and '2' an entity will be produced based on next callbacks
        parser.addEntityProducer('1', data -> {
            return Entities.builder()
                    .at(data.getX(), data.getY())
                    .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.RED))
                    .build();
        });

        parser.addEntityProducer('2', data -> {
            return Entities.builder()
                    .at(data.getX(), data.getY())
                    .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.GREEN))
                    .build();
        });

        // 4. parse the level file and set it
        Level level = parser.parse("level0.txt");

        getGameWorld().setLevel(level);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
