/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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
package sandbox;

import com.almasb.fxgl.algorithm.Grid;
import com.almasb.fxgl.algorithm.MapGenerator;
import com.almasb.fxgl.algorithm.TileType;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import kotlin.Pair;

import java.util.Arrays;

/**
 * Test for random map generation algorithm.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class MapTest extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Map");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setShowFPS(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {}

    @Override
    protected void initAssets() {}

    private static final int TILE_SIZE = 10;

    // 1. Define A* grid
    private Grid grid;

    @Override
    protected void initGame() {
        // 2. init grid width x height
        grid = new MapGenerator(1000).create(128, 72, Arrays.asList(
                new Pair<>(TileType.WATER, 79),
                new Pair<>(TileType.EARTH, 3000)));

        for (int i = 0; i < grid.getHeight(); i++) {
            for (int j = 0; j < grid.getWidth(); j++) {
                final int x = j;
                final int y = i;

                GameEntity tile = new GameEntity();
                tile.getPositionComponent().setValue(j*TILE_SIZE, i*TILE_SIZE);

                Rectangle graphics = new Rectangle(TILE_SIZE, TILE_SIZE,
                        grid.getTile(x, y).getType() == TileType.WATER ? Color.BLUE : Color.BROWN);
                tile.getMainViewComponent().setView(graphics);

                getGameWorld().addEntity(tile);
            }
        }
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
