/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package s21levelparsing;

import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.parser.TextLevelParser;
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
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(true);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    private static final int BLOCK_SIZE = 200;

    @Override
    protected void initGame() {

        // 1. create a parser and set it up
        TextLevelParser parser = new TextLevelParser();

        // 2. empty chars are ignored
        parser.setEmptyChar('0');

        // 3. for each '1' and '2' an entity will be produced based on next callbacks
        parser.addEntityProducer('1', (x, y) -> {
            return Entities.builder()
                    .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                    .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.RED))
                    .build();
        });

        parser.addEntityProducer('2', (x, y) -> {
            return Entities.builder()
                    .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                    .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.GREEN))
                    .build();
        });

        // 4. parse the level file and set it
        Level level = parser.parse("level0.txt");

        getGameWorld().setLevel(level);
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate(double tpf) {}

    public static void main(String[] args) {
        launch(args);
    }
}
