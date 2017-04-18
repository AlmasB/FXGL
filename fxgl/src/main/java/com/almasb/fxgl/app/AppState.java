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

package com.almasb.fxgl.app;

import com.almasb.fxgl.scene.FXGLScene;
import com.almasb.fxgl.service.Input;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class AppState implements State {

    private FXGLScene scene;
    private StateTimer timer = new StateTimerImpl();

    public AppState(FXGLScene scene) {
        this.scene = scene;
    }

    public FXGLScene getScene() {
        return scene;
    }

    @Override
    public StateTimer getTimer() {
        return timer;
    }

    @Override
    public Input getInput() {
        return scene.getInput();
    }

    @Override
    public void onEnter(State prevState) {
        // no-op
    }

    @Override
    public void onExit() {
        // no-op
    }

    @Override
    public void onUpdate(double tpf) {
        // no-op
    }

    @Override
    public String toString() {
        return getName();
    }
}
