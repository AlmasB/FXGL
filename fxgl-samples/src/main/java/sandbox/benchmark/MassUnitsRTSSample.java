/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox.benchmark;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IDComponent;
import com.almasb.fxgl.pathfinding.CellMoveComponent;
import com.almasb.fxgl.pathfinding.CellState;
import com.almasb.fxgl.pathfinding.astar.AStarGrid;
import com.almasb.fxgl.pathfinding.astar.AStarMoveComponent;
import com.almasb.fxgl.pathfinding.astar.AStarPathfinder;
import com.almasb.fxgl.physics.BoundingShape;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import kotlin.Unit;
import kotlin.system.TimingKt;

import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MassUnitsRTSSample extends GameApplication {

    private static final int CELL_WIDTH = 20;
    private static final int CELL_HEIGHT = 20;

    private static final int GRID_WIDTH = 64;
    private static final int GRID_HEIGHT = 36;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(GRID_WIDTH * CELL_WIDTH);
        settings.setHeight(GRID_HEIGHT * CELL_HEIGHT);
        settings.setTitle("MassUnitsRTSSample");
        settings.setManualResizeEnabled(true);
    }

    // 1. Define A* grid
    private AStarGrid grid;

    private AStarPathfinder pathfinder;

    @Override
    protected void initInput() {
        onBtn(MouseButton.SECONDARY, "Obstacle", () -> {
            var x = (int) ((getInput().getMouseXWorld()) / CELL_WIDTH);
            var y = (int) ((getInput().getMouseYWorld()) / CELL_HEIGHT);

            grid.get(x, y).setState(CellState.NOT_WALKABLE);

            var e = getGameWorld().getEntityByID("" + x + "," + y, 0).get();

            var rect = (Rectangle) e.getViewComponent().getChildren().get(0);

            rect.setFill(Color.RED);
        });

//        onBtnDown(MouseButton.PRIMARY, "Freeze", () -> {
//
//            var nanos = TimingKt.measureNanoTime(() -> {
//                var units = getGameWorld().getEntitiesFiltered(e -> e.getPropertyOptional("type").isPresent());
//                units.forEach( unit -> unit.getComponent(AStarMoveComponent.class).stopMovement());
//
//                return Unit.INSTANCE;
//            });
//
//            //System.out.printf("Took: %.3f\n", nanos / 1000000000.0);
//        });
    }

    @Override
    protected void initGame() {
        // 2. init grid width x height
        grid = new AStarGrid(GRID_WIDTH, GRID_HEIGHT);
        pathfinder = new AStarPathfinder(grid);
        pathfinder.setCachingPaths(true);

        grid.forEach(c -> {

            var x = c.getX();
            var y = c.getY();

            Rectangle rect = new Rectangle(CELL_WIDTH - 2, CELL_HEIGHT - 2);
            rect.setFill(Color.WHITE);
            rect.setStroke(Color.BLACK);

            Entity tile = entityBuilder()
                    .at(x * CELL_WIDTH, y * CELL_HEIGHT)
                    .view(rect)
                    .with(new IDComponent("" + x + "," + y, 0))
                    .buildAndAttach();

            rect.setOnMouseClicked(event -> {

                //getExecutor().startAsync(() -> {


                    var nanos = TimingKt.measureNanoTime(() -> {

                        // 5.6 sec from bot-left to top-right


                        // if left click do search, else place a red obstacle
                        if (event.getButton() == MouseButton.PRIMARY) {
                            var units = getGameWorld().getEntitiesFiltered(e -> e.getPropertyOptional("type").isPresent());

                            var cells = grid.getWalkableCells()
                                    .stream()
                                    .sorted(Comparator.comparingInt(cell -> cell.distance(grid.get(x, y))))
                                    .limit(units.size())
                                    .collect(Collectors.toList());

                            Collections.shuffle(cells);

                            for (int i = 0; i < units.size(); i++) {
                                var unit = units.get(i);
                                var cell = cells.get(i);

                                unit.getComponent(AStarMoveComponent.class).moveToCell(cell);
                            }
                        }



                        return Unit.INSTANCE;
                    });

                    System.out.printf("Took: %.3f\n", nanos / 1000000000.0);


                //});
            });
        });

        for (int i = 0; i < 285; i++) {
            var color = FXGLMath.randomColor().darker().darker();

            var e = entityBuilder()
                    .view(new Circle(CELL_WIDTH / 4, color))
                    .bbox(BoundingShape.box(CELL_WIDTH / 4, CELL_WIDTH / 4))
                    .with(new CellMoveComponent(CELL_WIDTH, CELL_HEIGHT, 300))
                    .with(new AStarMoveComponent(pathfinder))
                    .with("type", "unit")
                    .buildAndAttach();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
