/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxglgames.pacman.ai;

import com.almasb.fxgl.ai.SingleAction;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;
import com.almasb.fxglgames.pacman.control.MoveDirection;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RandomMoveAction extends SingleAction {

    public RandomMoveAction() {
        super("RandomMove");
    }

    @Override
    public void onUpdate(double tpf) {
        speed = tpf * 60;

        switch (moveDir) {
            case UP:
                up();
                break;

            case DOWN:
                down();
                break;

            case LEFT:
                left();
                break;

            case RIGHT:
                right();
                break;
        }

        if (position.getX() < 0) {
            position.setX(PacmanApp.BLOCK_SIZE * PacmanApp.MAP_SIZE - bbox.getWidth() - 5);
        }

        if (bbox.getMaxXWorld() > PacmanApp.BLOCK_SIZE * PacmanApp.MAP_SIZE) {
            position.setX(0);
        }
    }

    protected PositionComponent position;
    private BoundingBoxComponent bbox;

    private MoveDirection moveDir;

    public void setMoveDirection(MoveDirection moveDir) {
        this.moveDir = moveDir;
    }

    @Override
    public void start() {
        // fxglTODO: is there like init with object?
        if (position == null) {
            position = Entities.getPosition(getObject());
            bbox = Entities.getBBox(getObject());

            moveDir = MoveDirection.values()[new Random().nextInt(MoveDirection.values().length)];
        }
    }

    protected MoveDirection updateMoveDirection() {
        return MoveDirection.values()[new Random().nextInt(MoveDirection.values().length)];
    }

    private double speed = 0;

    public void up() {
        move(new Point2D(0, -5 * speed));
    }

    public void down() {
        move(new Point2D(0, 5 * speed));
    }

    public void left() {
        move(new Point2D(-5 * speed, 0));
    }

    public void right() {
        move(new Point2D(5 * speed, 0));
    }

    private List<Entity> blocks;

    private void move(Point2D vector) {
        if (!getObject().isActive())
            return;

        if (blocks == null) {
            blocks = FXGL.getApp().getGameWorld().getEntitiesByType(PacmanType.BLOCK);
        }

        long length = Math.round(vector.magnitude());

        Point2D unit = vector.normalize();

        for (int i = 0; i < length; i++) {
            position.translate(unit);

            boolean collision = blocks.stream()
                    .map(b -> Entities.getBBox(b))
                    .filter(box -> box.isCollidingWith(bbox))
                    .findAny()
                    .isPresent();

            if (collision) {
                position.translate(unit.multiply(-1));
                moveDir = updateMoveDirection();


                break;
            }
        }
    }
}
