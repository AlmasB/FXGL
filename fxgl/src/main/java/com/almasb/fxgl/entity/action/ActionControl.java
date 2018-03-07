/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

import com.almasb.fxgl.entity.Control;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ActionControl<T extends Entity> extends Control {

    private Deque<Action<T>> actions = new ArrayDeque<>();
    private ObservableList<Action<T>> actionsObservable = FXCollections.observableArrayList();

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

    public ObservableList<Action<T>> actionsProperty() {
        return FXCollections.unmodifiableObservableList(actionsObservable);
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
        actionsObservable.add(action);
    }

    /**
     * Remove current executing action.
     * If there are more actions pending, the first pending action becomes current.
     */
    public void removeCurrentAction() {
        if (currentAction != null) {
            actionsObservable.remove(currentAction);

            currentAction.setEntity(null);
            currentAction = null;
        }
    }

    /**
     * Remove last added action.
     */
    public void removeLastAction() {
        Action<T> a = actions.pollLast();
        if (a != null) {
            actionsObservable.remove(a);
        }
    }

    public void removeAction(Action action) {
        if (action == currentAction) {
            removeCurrentAction();
        } else {
            actions.remove(action);
            actionsObservable.remove(action);
        }
    }

    /**
     * Clears all running and pending actions.
     */
    public void clearActions() {
        removeCurrentAction();
        actions.clear();
        actionsObservable.clear();
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
