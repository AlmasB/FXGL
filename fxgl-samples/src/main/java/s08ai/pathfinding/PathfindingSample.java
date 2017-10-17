/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package s08ai.pathfinding;

import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.AStarNode;
import com.almasb.fxgl.ai.pathfinding.NodeState;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entity;
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
public class PathfindingSample extends GameApplication {

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("PathfindingSample");
        settings.setVersion("0.1");
        settings.setFullScreen(false);
        settings.setIntroEnabled(false);
        settings.setMenuEnabled(false);
        settings.setProfilingEnabled(false);
        settings.setApplicationMode(ApplicationMode.DEVELOPER);
    }

    // 1. Define A* grid
    private AStarGrid grid;

    @Override
    protected void initGame() {
        // 2. init grid width x height
        grid = new AStarGrid(20, 15);

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 20; j++) {
                final int x = j;
                final int y = i;

                Entity tile = new Entity();
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
                            getGameWorld().getEntitiesAt(new Point2D(n.getX() * 40, n.getY() * 40))
                                    .forEach(Entity::removeFromWorld);
                        });
                    } else {
                        grid.setNodeState(x, y, NodeState.NOT_WALKABLE);
                        graphics.setFill(Color.RED);
                    }
                });

                tile.getViewComponent().setView(graphics);

                getGameWorld().addEntity(tile);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
