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

package com.almasb.fxgl.service;

import com.almasb.fxgl.annotation.OnUserAction;
import com.almasb.fxgl.input.InputMapping;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.Trigger;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.Viewport;
import com.almasb.fxgl.service.listener.FXGLListener;
import com.almasb.fxgl.service.listener.UserProfileSavable;
import com.almasb.fxgl.time.UpdateEventListener;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Map;

/**
 * Allows input data queries as well as action bindings.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface Input extends UserProfileSavable, UpdateEventListener {

    /**
     * Called on key event.
     *
     * @param event key event
     */
    void onKeyEvent(KeyEvent event);

    /**
     * Called on mouse event.
     *
     * @param event mouse event
     * @param viewport current viewport where the even occurred
     * @param scaleRatio scale ratio of the display where the event occurred
     */
    void onMouseEvent(MouseEvent event, Viewport viewport, double scaleRatio);

    /**
     * Setting to false will disable capturing of input events.
     * Not captured events will not be processed.
     *
     * @param register true to enable, false to disable
     * @defaultValue true
     */
    void setRegisterInput(boolean register);

    /**
     * @return are input events being registered
     */
    boolean isRegisterInput();

    /**
     * Setting to false will disable processing of input events.
     * The events may still be captured but they will not be processed.
     *
     * @param process true to enable, false to disable
     * @defaultValue true
     */
    void setProcessInput(boolean process);

    /**
     * @return are input events being processed
     */
    boolean isProcessInput();

    /**
     * @param key the key to check
     * @return true iff key is currently (physically) held; mocking does not trigger this
     */
    boolean isHeld(KeyCode key);

    /**
     * @param button the button to check
     * @return true iff button is currently (physically) held; mocking does not trigger this
     */
    boolean isHeld(MouseButton button);

    /**
     * @return registered action bindings
     */
    Map<UserAction, Trigger> getBindings();

    /**
     * Bind given action to a keyboard key.
     *
     * @param action the action to bind
     * @param key the key
     * @throws IllegalArgumentException if action with same name exists or key is in use
     */
    default void addAction(UserAction action, KeyCode key) {
        addAction(action, key, InputModifier.NONE);
    }

    /**
     * Bind given action to a keyboard key with special modifier key.
     *
     * @param action the action to bind
     * @param key the key
     * @param modifier the key modifier
     * @throws IllegalArgumentException if action with same name exists or key is in use
     */
    void addAction(UserAction action, KeyCode key, InputModifier modifier);

    /**
     * Rebinds an existing action to given key.
     *
     * @param action the user action
     * @param key the key to rebind to
     * @return true if rebound, false if action not found or
     * there is another action bound to key
     */
    default boolean rebind(UserAction action, KeyCode key) {
        return rebind(action, key, InputModifier.NONE);
    }

    /**
     * Rebinds an existing action to given key.
     *
     * @param action the user action
     * @param key the key to rebind to
     * @param modifier the key modifier
     * @return true if rebound, false if action not found or
     * there is another action bound to key
     */
    boolean rebind(UserAction action, KeyCode key, InputModifier modifier);

    /**
     * Bind given action to a mouse button with special modifier key.
     *
     * @param action the action to bind
     * @param button the mouse button
     * @throws IllegalArgumentException if action with same name exists or button is in use
     */
    default void addAction(UserAction action, MouseButton button) {
        addAction(action, button, InputModifier.NONE);
    }

    /**
     * Bind given action to a mouse button with special modifier key.
     *
     * @param action the action to bind
     * @param button the mouse button
     * @param modifier the button modifier
     * @throws IllegalArgumentException if action with same name exists or button is in use
     */
    void addAction(UserAction action, MouseButton button, InputModifier modifier);

    /**
     * Rebinds an action to given mouse button.
     *
     * @param action the user action
     * @param button the mouse button
     * @return true if rebound, false if action not found or
     * there is another action bound to mouse button
     */
    default boolean rebind(UserAction action, MouseButton button) {
        return rebind(action, button, InputModifier.NONE);
    }

    /**
     * Rebinds an action to given mouse button.
     *
     * @param action the user action
     * @param button the mouse button
     * @param modifier the buttin modifier
     * @return true if rebound, false if action not found or
     * there is another action bound to mouse button
     */
    boolean rebind(UserAction action, MouseButton button, InputModifier modifier);

    /**
     * Add input mapping. The actual implementation needs to be specified by
     * {@link OnUserAction} annotation.
     *
     * @param mapping the mapping
     */
    void addInputMapping(InputMapping mapping);

    /**
     * Given an object, scans its methods for {@link OnUserAction} annotation
     * and creates UserActions from its data.
     *
     * @param instance the class instance to scan
     */
    void scanForUserActions(Object instance);

    /**
     * Clears all active actions.
     * Releases all key presses and mouse clicks for a single frame.
     */
    void clearAll();

    /* MOCKING */

    /**
     * Mocks key press event.
     * The behavior is equivalent to
     * user pressing and holding the key.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param key the key to mock
     */
    default void mockKeyPress(KeyCode key) {
        mockKeyPress(key, InputModifier.NONE);
    }

    /**
     * Mocks key press event. The behavior is equivalent to
     * user pressing and holding the key with the modifier.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param key the key to mock
     * @param modifier key modifier
     */
    void mockKeyPress(KeyCode key, InputModifier modifier);

    /**
     * Mocks key release event. The behavior is equivalent to
     * user releasing the key.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param key the key to mock
     */
    default void mockKeyRelease(KeyCode key) {
        mockKeyRelease(key, InputModifier.NONE);
    }

    /**
     * Mocks key release event. The behavior is equivalent to
     * user releasing the key and the modifier.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param key the key to mock
     * @param modifier the modifier
     */
    void mockKeyRelease(KeyCode key, InputModifier modifier);

    /**
     * Mocks button press event. The behavior is equivalent to
     * user pressing and holding the button.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param button the button to mock
     */
    default void mockButtonPress(MouseButton button) {
        mockButtonPress(button, InputModifier.NONE);
    }

    /**
     * Mocks button press event. The behavior is equivalent to
     * user pressing and holding the button.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param button the button to mock
     * @param modifier input modifier
     */
    default void mockButtonPress(MouseButton button, InputModifier modifier) {
        mockButtonPress(button, getMouseXWorld(), getMouseYWorld(), modifier);
    }

    /**
     * Mocks button press event. The behavior is equivalent to
     * user pressing and holding the button at x, y.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     * This does not affect mouse UI coordinates but affects game world coordinates.
     *
     * @param button the button to mock
     * @param gameX x in game world
     * @param gameY y in game world
     */
    default void mockButtonPress(MouseButton button, double gameX, double gameY) {
        mockButtonPress(button, gameX, gameY, InputModifier.NONE);
    }

    /**
     * Mocks button press event. The behavior is equivalent to
     * user pressing and holding the button and the modifier at x, y.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     * This does not affect mouse UI coordinates but affects game world coordinates.
     *
     * @param button the button to mock
     * @param gameX x in game world
     * @param gameY y in game world
     * @param modifier the modifier
     */
    void mockButtonPress(MouseButton button, double gameX, double gameY, InputModifier modifier);

    /**
     * Mocks button release event. The behavior is equivalent to
     * user releasing the button.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param button the button to mock
     */
    default void mockButtonRelease(MouseButton button) {
        mockButtonRelease(button, InputModifier.NONE);
    }

    /**
     * Mocks button release event. The behavior is equivalent to
     * user releasing the button.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     *
     * @param button the button to mock
     * @param modifier the modifier
     */
    default void mockButtonRelease(MouseButton button, InputModifier modifier) {
        mockButtonRelease(button, getMouseXWorld(), getMouseYWorld(), modifier);
    }

    /**
     * Mocks button release event. The behavior is equivalent to
     * user releasing the button.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     * This does not affect mouse UI coordinates but affects game world coordinates.
     *
     * @param button the button to mock
     * @param gameX x in game world
     * @param gameY y in game world
     */
    default void mockButtonRelease(MouseButton button, double gameX, double gameY) {
        mockButtonRelease(button, gameX, gameY, InputModifier.NONE);
    }

    /**
     * Mocks button release event. The behavior is equivalent to
     * user releasing the button.
     * Note: the event will be processed directly even if register input is false.
     * The event will NOT be processed if process input is false.
     * This does not affect mouse UI coordinates but affects game world coordinates.
     *
     * @param button the button to mock
     * @param gameX x in game world
     * @param gameY y in game world
     * @param modifier the modifier
     */
    void mockButtonRelease(MouseButton button, double gameX, double gameY, InputModifier modifier);

    /* MOUSE DATA */

    /**
     * @return cursor point in game coordinate space.
     */
    Point2D getMousePositionWorld();

    /**
     * @return cursor x in game coordinate space.
     */
    default double getMouseXWorld() {
        return getMousePositionWorld().getX();
    }

    /**
     * @return cursor y in game coordinate space.
     */
    default double getMouseYWorld() {
        return getMousePositionWorld().getY();
    }

    /**
     * @return cursor point in screen coordinate space.
     */
    Point2D getMousePositionUI();

    /**
     * @return cursor x in screen coordinate space.
     */
    default double getMouseXUI() {
        return getMousePositionUI().getX();
    }

    /**
     * @return cursor y in screen coordinate space.
     */
    default double getMouseYUI() {
        return getMousePositionUI().getY();
    }

    /**
     * @param gamePosition point in game world
     * @return vector from given point to mouse cursor point
     */
    default Point2D getVectorToMouse(Point2D gamePosition) {
        return getMousePositionWorld().subtract(gamePosition);
    }

    /**
     * @param gamePosition point in game world
     * @return vector from mouse cursor point to given point
     */
    default Point2D getVectorFromMouse(Point2D gamePosition) {
        return getVectorToMouse(gamePosition).multiply(-1);
    }
}
