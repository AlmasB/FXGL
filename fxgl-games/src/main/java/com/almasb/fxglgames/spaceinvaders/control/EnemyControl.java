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

package com.almasb.fxglgames.spaceinvaders.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameWorld;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.BoundingBoxComponent;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.spaceinvaders.event.GameEvent;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {

    private LocalTimer attackTimer;
    private LocalTimer moveTimer;
    private Duration nextAttack = Duration.seconds(2);
    private Duration nextMove = Duration.seconds(2);

    private boolean moveRight = true;

    private PositionComponent position;
    private BoundingBoxComponent bbox;

    @Override
    public void onAdded(Entity entity) {
        attackTimer = FXGL.newLocalTimer();
        attackTimer.capture();

        moveTimer = FXGL.newLocalTimer();
        moveTimer.capture();

        bbox = Entities.getBBox(entity);
        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (attackTimer.elapsed(nextAttack)) {
            if (FXGLMath.randomBoolean(0.3f)) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }

        if (moveTimer.elapsed(nextMove)) {
            moveRight = !moveRight;
            moveTimer.capture();
        }

        if (bbox.getMaxYWorld() >= FXGL.getApp().getHeight()) {
            FXGL.getEventBus().fireEvent(new GameEvent(GameEvent.ENEMY_REACHED_END));
            getEntity().removeFromWorld();
        }

        double speed = tpf * 90 * (moveRight ? 1 : -1);

        position.translateX(speed);
        position.translateY(tpf * 5);
    }

    private void shoot() {
        GameWorld world = (GameWorld) getEntity().getWorld();
        world.spawn("Bullet", new SpawnData(0, 0).put("owner", getEntity()));

        FXGL.getAudioPlayer().playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }
}
