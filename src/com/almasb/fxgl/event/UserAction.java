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
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.almasb.fxgl.event;

/**
 * Represents a user action which is typically triggered when a key
 * or a mouse event has occurred. User actions have names so that they
 * are easily identifiable. An action can be bound to a key or mouse event
 * using InputManager.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 */
public abstract class UserAction {

    private final String name;

    /**
     * Constructs new user action with given name. Name examples:
     * Walk_Forward, Shoot, Use, Aim, etc.
     *
     * @param name unique name that identifies this action
     */
    public UserAction(String name) {
        this.name = name;
    }

    /**
     *
     * @return action name
     */
    public final String getName() {
        return name;
    }

    /**
     * Called once in the same tick when triggered.
     */
    protected void onActionBegin() {}

    /**
     * Called as long as the trigger is being held (pressed).
     * Starts from the next tick from the one when was triggered
     */
    protected void onAction() {}

    /**
     * Called once in the same tick when trigger was released.
     */
    protected void onActionEnd() {}
}
