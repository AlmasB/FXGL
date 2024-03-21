/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.RandomAStarMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarCell;
import com.almasb.fxgl.pathfinding.dungeon.DungeonGrid;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baim (https://github.com/AlmasB)
 */
public class DungeonGenSample extends GameApplication {
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1280);
        settings.setHeight(720);
    }

    @Override
    protected void initGame() {
        DungeonGrid dungeon = new DungeonGrid(130, 72);

        var scale = 10;

        var agent = entityBuilder()
                .viewWithBBox(new Rectangle(scale, scale, Color.BLUE))
                .with(new CellMoveComponent(scale, scale, 150))
                .with(new AStarMoveComponent<>(dungeon))
                .zIndex(1)
                .anchorFromCenter()
                .buildAndAttach();

        for (int y = 0; y < 72; y++) {
            for (int x = 0; x < 130; x++) {
                var finalX = x;
                var finalY = y;

                var tile = dungeon.get(x, y);

                var rect = new Rectangle(scale, scale, Color.WHITE);

                if (!tile.isWalkable()) {
                    rect.setFill(Color.GRAY);
                } else {
                    rect.setFill(Color.WHITE);
                    agent.getComponent(AStarMoveComponent.class).stopMovementAt(finalX, finalY);

                    rect.setOnMouseClicked(e -> {
                        agent.getComponent(AStarMoveComponent.class).moveToCell(finalX, finalY);
                    });

                    if (FXGLMath.randomBoolean(0.09)) {
                        spawnNPC(x, y, dungeon);
                    }
                }

                entityBuilder()
                        .at(x*scale, y*scale)
                        .view(rect)
                        .buildAndAttach();
            }
        }
    }

    private void spawnNPC(int x, int y, DungeonGrid grid) {
        var view = new Rectangle(10, 10, FXGLMath.randomColor().brighter().brighter());
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(2);

        var e = entityBuilder()
                .zIndex(2)
                .viewWithBBox(view)
                .anchorFromCenter()
                .with(new CellMoveComponent(10, 10, 150))
                .with(new AStarMoveComponent<>(grid))
                .with(new RandomAStarMoveComponent<AStarCell>(1, 7, Duration.seconds(1), Duration.seconds(3)))
                .buildAndAttach();

        e.getComponent(AStarMoveComponent.class).stopMovementAt(x, y);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
