/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input;

/**
 * Represents a user action which is typically triggered when a key
 * or a mouse event has occurred. User actions have names so that they
 * are easily identifiable. An action can be bound to a key or mouse event
 * using Input.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public abstract class UserAction {

    private final String name;

    /**
     * Constructs new user action with given name. Name examples:
     * Walk Forward, Shoot, Use, Aim, etc.
     *
     * @param name unique name that identifies this action
     */
    public UserAction(String name) {
        this.name = name;
    }

    /**
     * @return action name
     */
    public final String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserAction && name.equals(((UserAction) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Called once in the same tick when triggered.
     */
    protected void onActionBegin() {
        // no default implementation
    }

    /**
     * Called as long as the trigger is being held (pressed).
     * Starts from the next tick from the one when was triggered
     */
    protected void onAction() {
        // no default implementation
    }

    /**
     * Called once in the same tick when trigger was released.
     */
    protected void onActionEnd() {
        // no default implementation
    }

    public final void fireActionBegin() {
        onActionBegin();
    }

    public final void fireAction() {
        onAction();
    }

    public final void fireActionEnd() {
        onActionEnd();
    }
}