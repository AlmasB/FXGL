/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.control;

import com.almasb.fxgl.entity.AbstractControl;
import com.almasb.fxgl.entity.Entity;

/**
 * API NOT READY FOR USE
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class HorizontalMovementControl extends AbstractControl {

    @SuppressWarnings("unused")
    private double radius;
    private double speed;
    @SuppressWarnings("unused")
    private double t = 0.0;
    @SuppressWarnings("unused")
    private double x, y;

    public HorizontalMovementControl(double speed, double radius) {
        this.radius = radius;
        this.speed = speed;
    }

    @Override
    protected void initEntity(Entity entity) {
//        x = entity.getTranslateX();
//        y = entity.getTranslateY();
    }

    @Override
    public void onUpdate(Entity entity) {
        //entity.setTranslateX(entity.getTranslateX() + TimerManager.tpfSeconds() * speed);
    }
}
