/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxglgames.flappy;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import javafx.scene.shape.Rectangle;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class WallBuildingControl extends AbstractControl {

    private PositionComponent position;

    private double lastWall = 1000;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (lastWall - position.getX() < FXGL.getApp().getWidth()) {
            buildWalls();
        }
    }

    private Rectangle wallView(double width, double height) {
        Rectangle wall = new Rectangle(width, height);
        wall.setArcWidth(25);
        wall.setArcHeight(25);
        wall.fillProperty().bind(FXGL.getApp().getGameState().objectProperty("stageColor"));
        return wall;
    }

    private void buildWalls() {
        double height = FXGL.getApp().getHeight();
        double distance = height / 2;

        for (int i = 1; i <= 10; i++) {
            double topHeight = Math.random() * (height - distance);

            Entities.builder()
                    .at(lastWall + i * 500, 0 - 25)
                    .type(EntityType.WALL)
                    .viewFromNodeWithBBox(wallView(50, topHeight))
                    .with(new CollidableComponent(true))
                    .buildAndAttach(FXGL.getApp().getGameWorld());

            Entities.builder()
                    .at(lastWall + i * 500, 0 + topHeight + distance + 25)
                    .type(EntityType.WALL)
                    .viewFromNodeWithBBox(wallView(50, height - distance - topHeight))
                    .with(new CollidableComponent(true))
                    .buildAndAttach(FXGL.getApp().getGameWorld());
        }

        lastWall += 10 * 500;
    }
}
