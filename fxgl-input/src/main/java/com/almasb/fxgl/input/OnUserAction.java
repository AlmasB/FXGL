/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.input;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is called when user triggers an action.
 * Note: the method signature must be <code>public void anyName()</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface OnUserAction {

    /**
     * Returns name of the action.
     * The action must have been specified
     * during initInput() as {@link com.almasb.fxgl.input.InputMapping}.
     *
     * @return action name
     */
    String name();

    /**
     * Returns type of the action, i.e. when the method should be called.
     * Based on the type, the method is called when the action starts, continues or stops.
     *
     * @return action type
     * @defaultValue {@link ActionType#ON_ACTION}
     */
    ActionType type() default ActionType.ON_ACTION;
}