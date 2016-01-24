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

package com.almasb.fxgl.input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/**
 * Basic mapping of action name to its trigger.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class InputMapping {
    private String actionName;
    private Object trigger;
    private InputModifier modifier;

    /**
     * Constructs an input mapping of given action name to a key with modifier.
     *
     * @param actionName action name
     * @param key the key to bind to
     * @param modifier the modifier
     */
    public InputMapping(String actionName, KeyCode key, InputModifier modifier) {
        this.actionName = actionName;
        trigger = key;
        this.modifier = modifier;
    }

    /**
     * Constructs an input mapping of given action name to a key with no modifier.
     *
     * @param actionName action name
     * @param key the key to bind to
     */
    public InputMapping(String actionName, KeyCode key) {
        this(actionName, key, InputModifier.NONE);
    }

    /**
     * Constructs an input mapping of given action name to a button with modifier.
     *
     * @param actionName action name
     * @param btn mouse button
     * @param modifier modifier
     */
    public InputMapping(String actionName, MouseButton btn, InputModifier modifier) {
        this.actionName = actionName;
        trigger = btn;
        this.modifier = modifier;
    }

    /**
     * Constructs an input mapping of given action name to a mouse button with no modifier.
     *
     * @param actionName action name
     * @param btn mouse button
     */
    public InputMapping(String actionName, MouseButton btn) {
        this(actionName, btn, InputModifier.NONE);
    }

    /**
     * @return true iff mapping is to KeyCode
     */
    boolean isKeyTrigger() {
        return trigger instanceof KeyCode;
    }

    /**
     * @return true iff mapping is to MouseButton
     */
    boolean isButtonTrigger() {
        return trigger instanceof MouseButton;
    }

    String getActionName() {
        return actionName;
    }

    InputModifier getModifier() {
        return modifier;
    }

    KeyCode getKeyTrigger() {
        return (KeyCode) trigger;
    }

    MouseButton getButtonTrigger() {
        return (MouseButton) trigger;
    }
}
