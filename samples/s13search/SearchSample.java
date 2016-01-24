/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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
package s13search;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.search.AStarGrid;
import com.almasb.fxgl.search.AStarNode;
import com.almasb.fxgl.search.NodeState;
import com.almasb.fxgl.settings.GameSettings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Demo that uses A* search to find a path between 2 nodes in a grid.
 * Right click to place a wall, left click to move.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SearchSample extends GameApplication {

    private enum Type {
        TILE
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("SearchSample");
        settings.setVersion("0.1developer");
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

    // 1. Define A* grid
    private AStarGrid grid;

    @Override
    protected void initGame() {
        // 2. init grid 20x15
        grid = new AStarGrid(20, 15);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 20; j++) {
                final int x = j;
                final int y = i;

                GameEntity tile = new GameEntity();
                tile.getPositionComponent().setValue(j*40, i*40);

                Rectangle graphics = new Rectangle(38, 38);
                graphics.setFill(Color.WHITE);
                graphics.setStroke(Color.BLACK);

                // add on click listener
                graphics.setOnMouseClicked(e -> {
                    // if left click do search, else place a red obstacle
                    if (e.getButton() == MouseButton.PRIMARY) {
                        List<AStarNode> nodes = grid.getPath(0, 0, x, y);
                        nodes.forEach(n -> {
                            getGameWorld().getEntityAt(new Point2D(n.getX() * 40, n.getY() * 40))
                                    .ifPresent(Entity::removeFromWorld);
                        });
                    } else {
                        grid.setNodeState(x, y, NodeState.NOT_WALKABLE);
                        graphics.setFill(Color.RED);
                    }
                });

                tile.getMainViewComponent().setGraphics(graphics);

                getGameWorld().addEntity(tile);
            }
        }
    }

    @Override
    protected void initPhysics() {}

    @Override
    protected void initUI() {}

    @Override
    protected void onUpdate() {}

    public static void main(String[] args) {
        launch(args);
    }
}
