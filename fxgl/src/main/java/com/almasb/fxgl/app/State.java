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

import com.almasb.fxgl.service.Input;
import com.almasb.fxgl.service.impl.input.FXGLInput;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A single state in which an application can be.
 * State change should only be requested within {@link #onUpdate(double)}
 * and not any other callbacks.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class State {

    private Input input = new FXGLInput();
    private StateTimer timer = new StateTimer();
    private CopyOnWriteArrayList<StateListener> listeners = new CopyOnWriteArrayList<>();

    public final StateTimer getTimer() {
        return timer;
    }

    public final Input getInput() {
        return input;
    }

    public final void addStateListener(StateListener listener) {
        listeners.add(listener);
    }

    public final void removeStateListener(StateListener listener) {
        listeners.remove(listener);
    }

    protected void onEnter(State prevState) {

    }

    protected void onExit() {

    }

    protected void onUpdate(double tpf) {

    }

    void enter(State prevState) {
        onEnter(prevState);

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onEnter(prevState);
        }
    }

    void update(double tpf) {
        input.onUpdate(tpf);
        timer.update(tpf);
        onUpdate(tpf);

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onUpdate(tpf);
        }
    }

    void exit() {
        onExit();
        input.clearAll();

        for (int i = 0; i < listeners.size(); i++) {
            listeners.get(i).onExit();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
