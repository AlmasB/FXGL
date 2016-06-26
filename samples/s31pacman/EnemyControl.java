/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

package s31pacman;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import javafx.geometry.Point2D;

import java.util.List;
import java.util.Random;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {

    public enum MoveDirection {
        UP, RIGHT, DOWN, LEFT;

        MoveDirection next() {
            int index = ordinal() + 1;

            if (index == values().length) {
                index = 0;
            }

            return values()[index];
        }

        MoveDirection prev() {
            int index = ordinal() - 1;

            if (index == -1) {
                index = values().length - 1;
            }

            return values()[index];
        }
    }

    private PositionComponent position;
    private BoundingBoxComponent bbox;

    private MoveDirection moveDir;

    @Override
    public void onAdded(Entity entity) {
        position = Entities.getPosition(entity);
        bbox = Entities.getBBox(entity);

        moveDir = MoveDirection.values()[new Random().nextInt(MoveDirection.values().length)];
        updateMoveDirection();
    }

    private void updateMoveDirection() {
        moveDir = Math.random() < 0.5 ? moveDir.next() : moveDir.prev();
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
        if (!getEntity().isActive())
            return;

        if (blocks == null) {
            blocks = FXGL.getApp().getGameWorld().getEntitiesByType(EntityType.BLOCK);
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
                updateMoveDirection();
                break;
            }
        }
    }
}
