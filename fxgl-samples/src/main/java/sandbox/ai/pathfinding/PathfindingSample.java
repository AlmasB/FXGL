/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.ai.pathfinding;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarPathfinder;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Demo that uses A* search to find a path between 2 cells in a grid.
 * Right click to place a wall, left click to move.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class PathfindingSample extends GameApplication {

    private static final int CELL_WIDTH = 40;
    private static final int CELL_HEIGHT = 40;

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 15;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(GRID_WIDTH * CELL_WIDTH);
        settings.setHeight(GRID_HEIGHT * CELL_HEIGHT);
        settings.setTitle("PathfindingSample");
    }

    // 1. Define A* grid
    private AStarGrid grid;

    private AStarPathfinder pathfinder;

    @Override
    protected void initGame() {
        // 2. init grid width x height
        grid = new AStarGrid(GRID_WIDTH, GRID_HEIGHT);
        pathfinder = new AStarPathfinder(grid);

        grid.forEach(c -> {

            var x = c.getX();
            var y = c.getY();

            Rectangle rect = new Rectangle(CELL_WIDTH - 2, CELL_HEIGHT - 2);
            rect.setFill(Color.WHITE);
            rect.setStroke(Color.BLACK);

            Entity tile = entityBuilder()
                    .at(x * CELL_WIDTH, y * CELL_HEIGHT)
                    .view(rect)
                    .buildAndAttach();

            rect.setOnMouseClicked(e -> {
                // if left click do search, else place a red obstacle
                if (e.getButton() == MouseButton.PRIMARY) {
                    var cells = pathfinder.findPath(0, 0, x, y);
                    cells.forEach(cell -> {
                        getGameWorld().getEntitiesAt(new Point2D(cell.getX() * CELL_WIDTH, cell.getY() * CELL_HEIGHT))
                                .forEach(Entity::removeFromWorld);
                    });
                } else {
                    grid.get(x, y).setState(CellState.NOT_WALKABLE);
                    rect.setFill(Color.RED);
                }
            });
        });

        var e = entityBuilder()
                .viewWithBBox(new Rectangle(20, 20))
                .with(new CellMoveComponent(CELL_WIDTH, CELL_HEIGHT, 100))
                .buildAndAttach();

        e.getComponent(CellMoveComponent.class).setPositionToCell(1, 1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
