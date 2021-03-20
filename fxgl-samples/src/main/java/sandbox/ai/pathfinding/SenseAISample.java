/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.ai.pathfinding;

import com.almasb.fxgl.ai.senseai.HearingSenseComponent;
import com.almasb.fxgl.ai.senseai.SenseAIState;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class SenseAISample extends GameApplication {

    private static final int CELL_WIDTH = 40;
    private static final int CELL_HEIGHT = 40;

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;

    private enum Type {
        WALL, NPC
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setHeight(GRID_HEIGHT * CELL_HEIGHT);
        settings.setWidthFromRatio(16/9.0);
        settings.setClickFeedbackEnabled(true);
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {
            byType(Type.NPC).forEach(e -> {
                e.getComponent(HearingSenseComponent.class).hearNoise(getInput().getMousePositionWorld(), 1.0);
            });
        });
    }

    private AStarGrid grid;

    @Override
    protected void initGame() {
        getGameScene().setBackgroundColor(Color.DARKGRAY);

        getGameWorld().addEntityFactory(new WallFactory());

        setLevelFromMap("tmx/random_astar.tmx");

        grid = AStarGrid.fromWorld(getGameWorld(), GRID_WIDTH, GRID_HEIGHT, CELL_WIDTH, CELL_HEIGHT, (type) -> {
            return type == Type.WALL ? CellState.NOT_WALKABLE : CellState.WALKABLE;
        });

        spawnNPC(1, 1);

        spawnNPC(7, 17);

        spawnNPC(1, 11);

        spawnNPC(18, 5);
    }

    private void spawnNPC(int x, int y) {
        var rect = new Rectangle(20, 20, FXGLMath.randomColor().brighter());
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStroke(Color.BLACK);

        var sense = new HearingSenseComponent(150);

        var text = getUIFactoryService().newText("", Color.WHITE, 16.0);
        text.textProperty().bind(sense.stateProperty().asString());

        var hearingRadiusCircle = new Circle(150);
        hearingRadiusCircle.setFill(null);
        hearingRadiusCircle.setStroke(Color.GREEN);

        var e = entityBuilder()
                .type(Type.NPC)
                .viewWithBBox(rect)
                .view(text)
                .view(hearingRadiusCircle)
                .anchorFromCenter()
                .with(new CellMoveComponent(CELL_WIDTH, CELL_HEIGHT, 150))
                .with(new AStarMoveComponent(grid))
                .with(sense)
                .with(new CustomAIComponent())
                .buildAndAttach();

        e.getComponent(AStarMoveComponent.class).stopMovementAt(x, y);
    }

    private static class CustomAIComponent extends Component {
        private CellMoveComponent cellMove;
        private AStarMoveComponent astarMove;
        private HearingSenseComponent sense;

        @Override
        public void onUpdate(double tpf) {
            if (sense.getState() == SenseAIState.CALM)
                return;

            if (!astarMove.isAtDestination())
                return;

            var lastPoint = sense.getLastHeardPoint();

            var cellX = (int) (lastPoint.getX() / CELL_WIDTH);
            var cellY = (int) (lastPoint.getY() / CELL_HEIGHT);

            var cell = astarMove.getGrid().get(cellX, cellY);

            var currentCell = astarMove.getCurrentCell().get();

            if (currentCell != cell) {
                astarMove.moveToCell(cell);
            } else {
                if (sense.getState() == SenseAIState.AGGRESSIVE) {
                    astarMove.getGrid().getNeighbors(cellX, cellY)
                            .stream()
                            .findAny()
                            .ifPresent(c -> astarMove.moveToCell(c));
                }
            }

            if (sense.getState() == SenseAIState.ALERT) {
                cellMove.setSpeed(120);
            } else if (sense.getState() == SenseAIState.AGGRESSIVE) {
                cellMove.setSpeed(450);
            }
        }
    }

    public static class WallFactory implements EntityFactory {

        @Spawns("wall")
        public Entity newWall(SpawnData data) {
            int width = data.get("width");
            int height = data.get("height");

            return entityBuilder(data)
                    .type(Type.WALL)
                    .viewWithBBox(new Rectangle(width, height, Color.RED))
                    .build();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
