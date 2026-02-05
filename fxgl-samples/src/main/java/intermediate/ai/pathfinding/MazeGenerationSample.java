/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.ai.pathfinding;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomAStarMoveComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.pathfinding.TraversableGrid;
import com.almasb.fxgl.pathfinding.maze.MazeCell;
import com.almasb.fxgl.pathfinding.maze.MazeGrid;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Shows how to construct and traverse mazes.
 *
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class MazeGenerationSample extends GameApplication {

    private static final int CELL_SIZE = 40;
    private static final int ENTITY_SPEED = 150;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        var maze = new MazeGrid(getAppWidth() / CELL_SIZE, getAppHeight() / CELL_SIZE);

        var player = entityBuilder()
                .bbox(new HitBox(BoundingShape.box(CELL_SIZE, CELL_SIZE)))
                .view(new Circle(CELL_SIZE / 2.0, CELL_SIZE / 2.0, CELL_SIZE / 2.0, Color.BLUE))
                .with(new CellMoveComponent(CELL_SIZE, CELL_SIZE, 150))
                .with(new AStarMoveComponent<>(maze))
                .zIndex(1)
                .anchorFromCenter()
                .buildAndAttach();

        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                var finalX = x;
                var finalY = y;

                var tile = maze.get(x, y);

                // build a rectangle for each tile, so player can easily click on it
                // this can also be done differently by checking user's mouse click position
                // in case you don't want to spawn this many entities
                var rect = new Rectangle(CELL_SIZE, CELL_SIZE, Color.WHITE);
                rect.setOnMouseClicked(e -> {
                    player.getComponent(AStarMoveComponent.class).moveToCell(finalX, finalY);
                });

                entityBuilder()
                        .at(x*CELL_SIZE, y*CELL_SIZE)
                        .view(rect)
                        .buildAndAttach();

                // draw a line if there is a vertical wall
                if (tile.hasLeftWall()) {
                    addLine(x, y, x, y+1);
                }

                // draw a line if there is a horizontal wall
                if (tile.hasTopWall()) {
                    addLine(x, y, x+1, y);
                }

                // randomly place an NPC
                if (FXGLMath.randomBoolean(0.09)) {
                    spawnNPC(x, y, maze);
                }
            }
        }
    }

    private void addLine(int startX, int startY, int endX, int endY) {
        var line = new Line(startX*CELL_SIZE, startY*CELL_SIZE, endX*CELL_SIZE, endY*CELL_SIZE);
        line.setStrokeWidth(2);
        line.setStroke(Color.DARKGRAY);

        addUINode(line);
    }

    private void spawnNPC(int x, int y, TraversableGrid<MazeCell> grid) {
        var view = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2, FXGLMath.randomColor().desaturate().darker());
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(2);
        view.setStrokeType(StrokeType.INSIDE);

        var e = entityBuilder()
                .zIndex(2)
                .viewWithBBox(view)
                .anchorFromCenter()
                .with(new CellMoveComponent(CELL_SIZE, CELL_SIZE, ENTITY_SPEED))
                .with(new AStarMoveComponent<>(grid))
                .with(new RandomAStarMoveComponent<>(1, 7, Duration.seconds(1), Duration.seconds(3)))
                .buildAndAttach();

        e.getComponent(AStarMoveComponent.class).stopMovementAt(x, y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
