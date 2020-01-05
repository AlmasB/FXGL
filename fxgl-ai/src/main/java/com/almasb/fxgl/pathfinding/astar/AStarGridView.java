/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.pathfinding.astar;

import com.almasb.fxgl.pathfinding.CellState;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class AStarGridView extends Parent {

    public AStarGridView(AStarGrid grid, int cellWidth, int cellHeight) {
        var rectGroup = new Group();
        var linesGroup = new Group();
        var coordGroup = new Group();

        grid.forEach(cell -> {
            // rects
            var rect = new Rectangle(cellWidth, cellHeight, cell.getState() == CellState.NOT_WALKABLE ?
                    Color.color(0.8, 0.0, 0.0, 0.75) : Color.color(0.0, 0.8, 0.0, 0.75));

            rect.setTranslateX(cell.getX() * cellWidth);
            rect.setTranslateY(cell.getY() * cellHeight);

            rectGroup.getChildren().add(rect);

            // coords
            var midX = cell.getX() * cellWidth + cellWidth / 2;
            var midY = cell.getY() * cellHeight + cellHeight / 2;

            var text = new Text("" + cell.getX() + "," + cell.getY());
            text.setTranslateX(midX - text.getLayoutBounds().getWidth() / 2);
            text.setTranslateY(midY);

            coordGroup.getChildren().add(text);
        });

        for (int x = 0; x < grid.getWidth(); x++) {
            var line = new Line(x*cellWidth, 0, x*cellWidth, grid.getHeight() * cellHeight);

            linesGroup.getChildren().add(line);
        }

        for (int y = 0; y < grid.getWidth(); y++) {
            var line = new Line(0, y*cellHeight, grid.getWidth() * cellWidth, y*cellHeight);

            linesGroup.getChildren().add(line);
        }

        getChildren().addAll(rectGroup, linesGroup, coordGroup);
    }
}
