/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.app.listener.StateListener;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.time.Timer;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A single state in which an application can be.
 * State change should only be requested within {@link #onUpdate(double)}
 * and not any other callbacks.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public abstract class State {

    private Input input = new Input();
    private Timer timer = new Timer();
    private CopyOnWriteArrayList<StateListener> listeners = new CopyOnWriteArrayList<>();

    public final Timer getTimer() {
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

    /**
     * Called after entering this state from prevState
     */
    protected void onEnter(State prevState) {

    }

    /**
     * Called before exit.
     */
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
        input.update(tpf);
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
