/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.entity.action;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.sslogger.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Allows queueing new actions, querying and cancelling existing actions.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class ActionComponent extends Component {

    private static final Logger log = Logger.get(ActionComponent.class);

    private final Action IDLE = new IdleAction();

    private CancelPolicy cancelPolicy = CancelPolicy.ONE;

    private ObservableList<Action> actions = FXCollections.observableArrayList();

    private Action currentAction = IDLE;

    @Override
    public void onUpdate(double tpf) {
        if (currentAction.isCancelled()) {
            switch (cancelPolicy) {
                case ONE:
                    currentAction.onCancelled();
                    removeCurrentActionAndSetNext();
                    break;

                case ALL:
                    cancelActions();
                    break;

                default:
                    log.warning("Unknown cancel policy: " + cancelPolicy);
                    currentAction.onCancelled();
                    removeCurrentActionAndSetNext();
                    break;
            }
        }

        if (currentAction.isComplete()) {
            currentAction.onCompleted();
            removeCurrentActionAndSetNext();
        } else {
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
        actions.forEach(Action::onCancelled);
        actions.clear();
        removeCurrentActionAndSetNext();
    }

    /**
     * Add an action for this entity to execute.
     * If an entity is already executing an action,
     * this action will be queued.
     *
     * @param action next action to execute
     */
    public void pushAction(Action action) {
        action.setEntity(entity);
        actions.add(action);
        action.onQueued();
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
        currentAction.onStarted();
    }

    /**
     * @return next action in the queue or Idle if queue is empty
     */
    public Action getNextAction() {
        return hasNextActions() ? actions.get(0) : IDLE;
    }
}
