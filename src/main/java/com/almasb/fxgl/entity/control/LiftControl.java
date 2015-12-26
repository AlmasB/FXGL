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

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.ServiceType;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.time.FXGLMasterTimer;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * Lift control.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public class LiftControl extends AbstractControl {

    private LocalTimer timer;
    private Duration duration;
    private double distance;
    private boolean goingUp;
    private double speed;

    /**
     * Constructs lift control (moving vertically).
     *
     * @param duration duration going (one way)
     * @param distance the distance entity travels (one way)
     * @param goingUp if true, entity starts going up, otherwise down
     */
    public LiftControl(Duration duration, double distance, boolean goingUp) {
        this.duration = duration;
        this.distance = distance;
        this.goingUp = goingUp;
    }

    @Override
    protected void initEntity(Entity entity) {
        timer = GameApplication.getService(ServiceType.LOCAL_TIMER);
        speed = FXGLMasterTimer.tpfSeconds() * distance / duration.toSeconds();
    }

    @Override
    public void onUpdate(Entity entity) {
        if (timer.elapsed(duration)) {
            goingUp = !goingUp;
            timer.capture();
        }

        entity.translate(0, goingUp ? -speed : speed);
    }
}
