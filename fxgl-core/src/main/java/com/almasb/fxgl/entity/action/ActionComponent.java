/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

import com.almasb.fxgl.entity.component.Component;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Allows queueing new actions, querying and cancelling existing actions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ActionComponent extends Component {

    private final Action IDLE = new IdleAction();

    private CancelPolicy cancelPolicy = CancelPolicy.ONE;

    /**
     * The head (if exists) of the queue is the current action.
     */
    private ObservableList<Action> actions = FXCollections.observableArrayList();

    private Action currentAction = IDLE;

    @Override
    public void onUpdate(double tpf) {
        if (currentAction != IDLE && currentAction.isCancelled()) {
            switch (cancelPolicy) {
                case ONE:
                    removeCurrentActionAndSetNext();
                    break;

                case ALL:
                    cancelActions();
                    break;
            }
        }

        if (currentAction.isComplete()) {
            currentAction.onCompleted();
            removeCurrentActionAndSetNext();
        }

        if (!currentAction.isCancelled()) {
            currentAction.onUpdate(tpf);
        }
    }

    @Override
    public void onRemoved() {
        cancelActions();
    }

    public CancelPolicy getCancelPolicy() {
        return cancelPolicy;
    }

    public void setCancelPolicy(CancelPolicy cancelPolicy) {
        this.cancelPolicy = cancelPolicy;
    }

    public ObservableList<Action> actionsProperty() {
        return FXCollections.unmodifiableObservableList(actions);
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    /**
     * @return true when not performing any actions
     */
    public boolean isIdle() {
        return currentAction == IDLE;
    }

    /**
     * @return true if there are more actions (apart from Idle) in the queue
     */
    public boolean hasNextActions() {
        return !actions.isEmpty();
    }

    /**
     * Clears all running and pending actions.
     */
    public void cancelActions() {
        actions.forEach(Action::cancel);
        actions.clear();
        removeCurrentActionAndSetNext();
    }

    /**
     * Add an action to the queue for this entity to execute.
     * The action is notified that it was put in a queue.
     *
     * @param action next action to execute
     */
    public void addAction(Action action) {
        action.setEntity(entity);
        actions.add(action);
        action.onQueued();
    }

    /**
     * Removing an action also cancels it.
     *
     * @param action the action to remove
     */
    public void removeAction(Action action) {
        action.cancel();
        actions.remove(action);
    }

    /**
     * Remove current executing action.
     * If there are more actions pending, the first pending action becomes current.
     */
    private void removeCurrentActionAndSetNext() {
        if (!isIdle() && !actions.isEmpty()) {
            actions.remove(0);
        }

        currentAction = getNextAction();

        // check if the action is cancelled before it started
        if (!currentAction.isCancelled())
            currentAction.onStarted();
    }

    /**
     * @return next action in the queue or Idle if queue is empty
     */
    public Action getNextAction() {
        return hasNextActions() ? actions.get(0) : IDLE;
    }

    @Override
    public boolean isComponentInjectionRequired() {
        return false;
    }
}
