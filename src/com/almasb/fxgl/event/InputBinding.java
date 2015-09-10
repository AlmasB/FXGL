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

import java.util.Optional;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

/**
 * Represents an input binding of a single action and
 * all keys / mouse buttons to which the action is bound.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class InputBinding {
    private final UserAction action;

    private Optional<KeyCode> key = Optional.empty();
    private Optional<MouseButton> btn = Optional.empty();

    private ReadOnlyStringWrapper triggerName = new ReadOnlyStringWrapper();

    public ReadOnlyStringProperty triggerNameProperty() {
        return triggerName.getReadOnlyProperty();
    }

    public InputBinding(UserAction action, KeyCode key) {
        this.action = action;
        setTrigger(key);
    }

    public InputBinding(UserAction action, MouseButton btn) {
        this.action = action;
        setTrigger(btn);
    }

    public boolean isTriggered(KeyCode k) {
        return key.filter(keyCode -> keyCode == k).isPresent();
    }

    public boolean isTriggered(MouseButton b) {
        return btn.filter(button -> button == b).isPresent();
    }

    public void setTrigger(KeyCode k) {
        this.btn = Optional.empty();
        this.key = Optional.of(k);
        triggerName.set(k.getName());
    }

    public void setTrigger(MouseButton b) {
        this.key = Optional.empty();
        this.btn = Optional.of(b);
        triggerName.set(b.toString());
    }

    public void removeTriggers() {
        key = Optional.empty();
        btn = Optional.empty();
        triggerName.set("UNDEFINED");
    }

    public UserAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return action.getName() + " " + triggerName.get();
    }
}
