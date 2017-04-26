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

package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.AStarNode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class MoveAction extends GoalAction {

    public MoveAction() {
        super("Move");
    }

    private AStarGrid grid;
    private GameEntity player;

    private List<AStarNode> path = new ArrayList<>();

    private PositionComponent position;

    private double speed;

    @Override
    public void start() {
        position = getObject().getPositionComponent();

        grid = ((PacmanApp) FXGL.getApp()).getGrid();
        player = (GameEntity) FXGL.getApp().getGameWorld().getEntitiesByType(PacmanType.PLAYER).get(0);

        int startX = (int)(position.getX() / PacmanApp.BLOCK_SIZE);
        int startY = (int)(position.getY() / PacmanApp.BLOCK_SIZE);

        int targetX = (int)((player.getPositionComponent().getX() + 20) / PacmanApp.BLOCK_SIZE);
        int targetY = (int)((player.getPositionComponent().getY() + 20) / PacmanApp.BLOCK_SIZE);

        path = grid.getPath(startX, startY,
                targetX, targetY);
    }

    @Override
    public boolean reachedGoal() {
        return path.isEmpty();
    }

    @Override
    public void action() {
        speed = 0.016 * 60 * 5;

        AStarNode next = path.get(0);

        int nextX = next.getX() * PacmanApp.BLOCK_SIZE;
        int nextY = next.getY() * PacmanApp.BLOCK_SIZE;

        double dx = nextX - position.getX();
        double dy = nextY - position.getY();

        if (Math.abs(dx) <= speed)
            position.setX(nextX);
        else
            position.translateX(speed * Math.signum(dx));

        if (Math.abs(dy) <= speed)
            position.setY(nextY);
        else
            position.translateY(speed * Math.signum(dy));

        if (position.getX() == nextX && position.getY() == nextY) {
            path.remove(0);
        }
    }
}
