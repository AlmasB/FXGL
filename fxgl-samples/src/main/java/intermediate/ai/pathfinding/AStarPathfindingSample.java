/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package intermediate.ai.pathfinding;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static com.almasb.fxgl.dsl.FXGL.debug;
import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * Demo that uses A* search to find a path between 2 cells in a grid.
 * Right click to place a wall, left click to move.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarPathfindingSample extends GameApplication {

    private Entity agent;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setDeveloperMenuEnabled(true);
    }

    @Override
    protected void initGame() {
        var grid = new AStarGrid(1280 / 40, 720 / 40);

        agent = entityBuilder()
                .viewWithBBox(new Rectangle(40, 40, Color.BLUE))
                .with(new CellMoveComponent(40, 40, 150))
                .with(new AStarMoveComponent(grid))
                .zIndex(1)
                .anchorFromCenter()
                .buildAndAttach();

        agent.getComponent(CellMoveComponent.class).atDestinationProperty().addListener((o, old, isAtDestination) -> {
            if (isAtDestination) {
                debug("CellMoveComponent: reached destination");
            }
        });

        agent.getComponent(AStarMoveComponent.class).atDestinationProperty().addListener((o, old, isAtDestination) -> {
            if (isAtDestination) {
                debug("AStarMoveComponent: reached destination");
            }
        });

        for (int y = 0; y < 720 / 40; y++) {
            for (int x = 0; x < 1280 / 40; x++) {
                final var finalX = x;
                final var finalY = y;

                var view = new Rectangle(40, 40, Color.WHITE);
                view.setStroke(Color.color(0, 0, 0, 0.25));
                view.setStrokeType(StrokeType.INSIDE);

                var e = entityBuilder()
                        .at(x * 40, y * 40)
                        .view(view)
                        .buildAndAttach();

                e.getViewComponent().addOnClickHandler(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        agent.getComponent(AStarMoveComponent.class).moveToCell(finalX, finalY);

                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        grid.get(finalX, finalY).setState(CellState.NOT_WALKABLE);
                        view.setFill(Color.RED);
                    }
                });
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
