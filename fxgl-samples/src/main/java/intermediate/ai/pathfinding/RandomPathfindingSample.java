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
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class RandomPathfindingSample extends GameApplication {

    private static final int CELL_WIDTH = 40;
    private static final int CELL_HEIGHT = 40;

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;

    private enum Type {
        WALL
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(GRID_WIDTH * CELL_WIDTH);
        settings.setHeight(GRID_HEIGHT * CELL_HEIGHT);
    }

    private AStarGrid grid;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.LIGHTGREY);
        getGameWorld().addEntityFactory(new WallFactory());

        setLevelFromMap("tmx/random_astar.tmx");

        grid = AStarGrid.fromWorld(getGameWorld(), GRID_WIDTH, GRID_HEIGHT, CELL_WIDTH, CELL_HEIGHT, (type) -> {
            return type == Type.WALL ? CellState.NOT_WALKABLE : CellState.WALKABLE;
        });

        spawnNPC(1, 1);
        spawnNPC(7, 17);
        spawnNPC(1, 11);
        spawnNPC(18, 5);
        spawnNPC(14, 5);
        spawnNPC(3, 15);
        spawnNPC(5, 10);
        spawnNPC(8, 16);
        spawnNPC(19, 19);
        spawnNPC(19, 1);
        spawnNPC(1, 19);
    }

    private void spawnNPC(int x, int y) {
        var view = new Rectangle(20, 20, FXGLMath.randomColor().brighter().brighter());
        view.setStroke(Color.BLACK);
        view.setStrokeWidth(2);

        var e = entityBuilder()
                .viewWithBBox(view)
                .anchorFromCenter()
                .with(new CellMoveComponent(CELL_WIDTH, CELL_HEIGHT, 150))
                .with(new AStarMoveComponent(grid))
                .with(new RandomAStarMoveComponent(1, 7, Duration.seconds(1), Duration.seconds(3)))
                .buildAndAttach();

        e.getComponent(AStarMoveComponent.class).stopMovementAt(x, y);
    }

    public static class WallFactory implements EntityFactory {

        @Spawns("wall")
        public Entity newWall(SpawnData data) {
            int width = data.get("width");
            int height = data.get("height");

            var view = new Rectangle(width, height, Color.RED);
            view.setStroke(Color.BLACK);
            view.setStrokeWidth(2);

            return entityBuilder(data)
                    .type(Type.WALL)
                    .viewWithBBox(view)
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
