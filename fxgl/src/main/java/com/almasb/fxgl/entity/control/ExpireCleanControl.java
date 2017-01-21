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

package com.almasb.fxgl.entity.control;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.time.TimerAction;
import javafx.util.Duration;

/**
 * Removes an entity from the world after a certain duration.
 * Useful for special effects or temporary entities.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class ExpireCleanControl extends AbstractControl {

    private Duration expire;

    /**
     * The expire duration timer starts when the entity is attached to the world,
     * so it does not start immediately when this control is created.
     *
     * @param expire the duration after entity is removed from the world
     */
    public ExpireCleanControl(Duration expire) {
        this.expire = expire;
    }

    private TimerAction timerAction;

    @Override
    public void onAdded(Entity entity) {
        entity.activeProperty().addListener((observable, oldValue, isActive) -> {
            if (isActive) {
                timerAction = FXGL.getMasterTimer().runOnceAfter(entity::removeFromWorld, expire);
            } else {
                timerAction.expire();
            }
        });
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        // we use master timer to schedule removal
    }
}
