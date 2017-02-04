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

package com.almasb.fxglgames.pacman.control;


import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.NodeState;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.entity.component.RotationComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;

import java.util.List;
import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class PlayerControl extends AbstractControl {
    private PositionComponent position;
    private BoundingBoxComponent bbox;
    private ViewComponent view;
    private RotationComponent rotation;

    private MoveDirection moveDir = MoveDirection.UP;

    public MoveDirection getMoveDirection() {
        return moveDir;
    }

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        bbox = Entities.getBBox(entity);
        view = Entities.getView(entity);
        rotation = Entities.getRotation(entity);
    }

    private double speed = 0;

    @Override
    public void onUpdate(Entity entity, double tpf) {
        speed = tpf * 60;

        if (position.getX() < 0) {
            position.setX(PacmanApp.BLOCK_SIZE * PacmanApp.MAP_SIZE - bbox.getWidth() - 5);
        }

        if (bbox.getMaxXWorld() > PacmanApp.BLOCK_SIZE * PacmanApp.MAP_SIZE) {
            position.setX(0);
        }
    }

    public void up() {
        moveDir = MoveDirection.UP;

        move(0, -5*speed);

        rotation.setValue(270);
        view.getView().setScaleX(1);
    }

    public void down() {
        moveDir = MoveDirection.DOWN;

        move(0, 5*speed);

        rotation.setValue(90);
        view.getView().setScaleX(1);
    }

    public void left() {
        moveDir = MoveDirection.LEFT;

        move(-5*speed, 0);

        view.getView().setScaleX(-1);
        rotation.setValue(0);
    }

    public void right() {
        moveDir = MoveDirection.RIGHT;

        move(5*speed, 0);

        view.getView().setScaleX(1);
        rotation.setValue(0);
    }

    public void teleport() {
        Random random = new Random();

        AStarGrid grid = ((PacmanApp) FXGL.getApp()).getGrid();

        int x, y;

        do {
            x = (random.nextInt(PacmanApp.MAP_SIZE - 2) + 1);
            y = (random.nextInt(PacmanApp.MAP_SIZE - 2) + 1);
        } while (grid.getNodeState(x, y) != NodeState.WALKABLE);

        position.setValue(x * PacmanApp.BLOCK_SIZE, y * PacmanApp.BLOCK_SIZE);
    }

    private List<Entity> blocks;

    private void move(double dx, double dy) {
        if (!getEntity().isActive())
            return;

        if (blocks == null) {
            blocks = FXGL.getApp().getGameWorld().getEntitiesByType(PacmanType.BLOCK);
        }

        double mag = Math.sqrt(dx * dx + dy * dy);
        long length = Math.round(mag);

        double unitX = dx / mag;
        double unitY = dy / mag;

        for (int i = 0; i < length; i++) {
            position.translate(unitX, unitY);

            boolean collision = false;

            for (int j = 0; j < blocks.size(); j++) {
                if (Entities.getBBox(blocks.get(j)).isCollidingWith(bbox)) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                position.translate(-unitX, -unitY);
                break;
            }
        }
    }
}
