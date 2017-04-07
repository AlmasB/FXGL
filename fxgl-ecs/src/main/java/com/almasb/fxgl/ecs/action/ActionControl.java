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

package com.almasb.fxgl.ecs.action;

import com.almasb.fxgl.ecs.AbstractControl;
import com.almasb.fxgl.ecs.Entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ActionControl<T extends Entity> extends AbstractControl {

    private Deque<Action<T>> actions = new ArrayDeque<>();
    private Action<T> currentAction = null;
    private T thisEntity = null;

    @SuppressWarnings("unchecked")
    @Override
    public void onAdded(Entity entity) {
        thisEntity = (T) entity;
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        updateActions(tpf);
    }

    @Override
    public void onRemoved(Entity entity) {
        clearActions();
        thisEntity = null;
    }

    @SuppressWarnings("unchecked")
    private void updateActions(double tpf) {
        if (currentAction != null) {

            if (currentAction.isComplete()) {
                removeCurrentAction();
            } else {
                currentAction.onUpdate(thisEntity, tpf);
            }

        } else {
            if (hasNextActions()) {
                currentAction = actions.pollFirst();
                currentAction.setEntity(thisEntity);
            }
        }
    }

    /**
     * @return true if there are more actions in the queue
     */
    public boolean hasNextActions() {
        return !actions.isEmpty();
    }

    /**
     * Add an action for this entity to execute.
     * If an entity is already executing an action,
     * this action will be queued.
     *
     * @param action next action to execute
     */
    @SuppressWarnings("unchecked")
    public void addAction(Action action) {
        actions.add(action);
    }

    /**
     * Remove current executing action.
     * If there are more actions pending, the first pending action becomes current.
     */
    public void removeCurrentAction() {
        if (currentAction != null) {
            currentAction.setEntity(null);
            currentAction = null;
        }
    }

    /**
     * Remove last added action.
     */
    public void removeLastAction() {
        actions.pollLast();
    }

    /**
     * Clears all running and pending actions.
     */
    public void clearActions() {
        removeCurrentAction();
        actions.clear();
    }

    public Optional<Action<T>> getCurrentAction() {
        return Optional.ofNullable(currentAction);
    }

    public Optional<Action<T>> getNextAction() {
        return Optional.ofNullable(actions.peekFirst());
    }

    public Optional<Action<T>> getLastAction() {
        return Optional.ofNullable(actions.peekLast());
    }
}
