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

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxglgames.pacman.PacmanApp;
import com.almasb.fxglgames.pacman.PacmanType;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {

    protected PositionComponent position;
    private BoundingBoxComponent bbox;

    private MoveDirection moveDir;

    private Random random = new Random();

    public void setMoveDirection(MoveDirection moveDir) {
        this.moveDir = moveDir;
    }

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        bbox = Entities.getBBox(entity);

        moveDir = MoveDirection.values()[random.nextInt(MoveDirection.values().length)];
    }

    protected MoveDirection updateMoveDirection() {
        return MoveDirection.values()[random.nextInt(MoveDirection.values().length)];
    }

    private double speed = 0;

    @Override
    public void onUpdate(Entity entity, double tpf) {
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

    public void up() {
        move(0, -5 * speed);
    }

    public void down() {
        move(0, 5 * speed);
    }

    public void left() {
        move(-5 * speed, 0);
    }

    public void right() {
        move(5 * speed, 0);
    }

    private List<Entity> blocks;

    private void move(Point2D vector) {
        if (!getEntity().isActive())
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

    private Vec2 velocity = new Vec2();

    private void move(double dx, double dy) {
        if (!getEntity().isActive())
            return;

        if (blocks == null) {
            blocks = FXGL.getApp().getGameWorld().getEntitiesByType(PacmanType.BLOCK);
        }

        velocity.set((float) dx, (float) dy);

        int length = FXGLMath.roundPositive(velocity.length());

        velocity.normalizeLocal();

        for (int i = 0; i < length; i++) {
            position.translate(velocity.x, velocity.y);

            boolean collision = false;

            for (int j = 0; j < blocks.size(); j++) {
                if (Entities.getBBox(blocks.get(j)).isCollidingWith(bbox)) {
                    collision = true;
                    break;
                }
            }

            if (collision) {
                position.translate(-velocity.x, -velocity.y);
                moveDir = updateMoveDirection();

                break;
            }
        }

//        double mag = Math.sqrt(dx * dx + dy * dy);
//        long length = Math.round(mag);
//
//        double unitX = dx / mag;
//        double unitY = dy / mag;
//
//        for (int i = 0; i < length; i++) {
//            position.translate(unitX, unitY);
//
//            boolean collision = false;
//
//            for (int j = 0; j < blocks.size(); j++) {
//                if (Entities.getBBox(blocks.get(j)).isCollidingWith(bbox)) {
//                    collision = true;
//                    break;
//                }
//            }
//
//            if (collision) {
//                position.translate(-unitX, -unitY);
//                moveDir = updateMoveDirection();
//
//                break;
//            }
//        }
    }
}
