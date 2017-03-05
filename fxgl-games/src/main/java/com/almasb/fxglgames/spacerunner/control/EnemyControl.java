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

package com.almasb.fxglgames.spacerunner.control;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.service.AudioPlayer;
import com.almasb.fxgl.service.ServiceType;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxglgames.spacerunner.SpaceRunnerFactory;
import com.almasb.fxglgames.spacerunner.SpaceRunnerType;
import javafx.util.Duration;

/**
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class EnemyControl extends AbstractControl {

    private LocalTimer attackTimer;
    private Duration nextAttack = Duration.seconds(2);

    private AudioPlayer audioPlayer;

    private PositionComponent position;

    @Override
    public void onAdded(Entity entity) {
        audioPlayer = FXGL.getService(ServiceType.AUDIO_PLAYER);

        attackTimer = FXGL.getService(ServiceType.LOCAL_TIMER);
        attackTimer.capture();

        position = Entities.getPosition(entity);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {

        if (attackTimer.elapsed(nextAttack)) {
            if (Math.random() < 0.8) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }

        position.translateX(tpf * 30);
    }

    private void shoot() {
        Entity bullet = FXGL.getInstance(SpaceRunnerFactory.class)
                .newBullet(position.getX(), position.getY() + 20, SpaceRunnerType.ENEMY);

        getEntity().getWorld().addEntity(bullet);

        //audioPlayer.playSound("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }
}

