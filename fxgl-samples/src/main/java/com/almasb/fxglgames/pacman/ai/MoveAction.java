/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.GoalAction;
import com.almasb.fxgl.ai.pathfinding.AStarGrid;
import com.almasb.fxgl.ai.pathfinding.AStarNode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
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
    private Entity player;

    private List<AStarNode> path = new ArrayList<>();

    private PositionComponent position;

    private double speed;

    @Override
    public void start() {
        position = getObject().getPositionComponent();

        grid = ((PacmanApp) FXGL.getApp()).getGrid();
        player = (Entity) FXGL.getApp().getGameWorld().getEntitiesByType(PacmanType.PLAYER).get(0);

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
    public void onUpdate(double tpf) {
        speed = tpf * 60 * 5;

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
