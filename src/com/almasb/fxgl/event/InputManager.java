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

import java.util.HashMap;
import java.util.Map;

import com.almasb.fxgl.FXGLManager;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Provides access to mouse state and allows binding of actions
 * to key and mouse events
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class InputManager extends FXGLManager {

    private Scene gameScene;

    /**
     * Holds mouse state information
     */
    private Mouse mouse = new Mouse();

    private Map<KeyCode, UserAction> keyBindings = new HashMap<>();
    private Map<MouseButton, UserAction> mouseBindings = new HashMap<>();

    // TODO: replace with observable maps
    /**
     * Returns a new map containing keys and actions bound
     * to those keys
     *
     * @return key bindings
     */
    public Map<KeyCode, UserAction> getKeyBindings() {
        return new HashMap<>(keyBindings);
    }

    /**
     * Returns a new map containing mouse buttons and actions bound
     * to those buttons
     *
     * @return mouse bindings
     */
    public Map<MouseButton, UserAction> getMouseBindings() {
        return new HashMap<>(mouseBindings);
    }

    //private Map<KeyCode, Boolean> keys = new HashMap<>();

    private ObservableList<UserAction> currentActions = FXCollections.observableArrayList();

    public InputManager() {
        currentActions.addListener(new ListChangeListener<UserAction>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends UserAction> c) {
                while (c.next()) {
                    if (!processActions)
                        continue;

                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(action -> action.onActionBegin());
                    }
                    else if (c.wasRemoved()) {
                        c.getRemoved().forEach(action -> action.onActionEnd());
                    }
                }
            }
        });
    }

    public void init(Scene mainScene) {
        this.gameScene = mainScene;
        gameScene.setOnKeyPressed(event -> {
            if (app.isGameMenuOpen() || app.isMainMenuOpen())
                return;

            KeyCode key = event.getCode();
            UserAction action = keyBindings.get(key);

            if (action != null && !currentActions.contains(action)) {
                currentActions.add(action);
            }

        });
        gameScene.setOnKeyReleased(event -> {
            if (app.isGameMenuOpen() || app.isMainMenuOpen())
                return;

            KeyCode key = event.getCode();
            UserAction action = keyBindings.get(key);

            if (action != null) {
                currentActions.remove(action);
            }
        });

        gameScene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (app.isGameMenuOpen() || app.isMainMenuOpen())
                return;

            MouseButton btn = event.getButton();
            UserAction action = mouseBindings.get(btn);

            if (action != null) {
                currentActions.add(action);
            }
        });

        gameScene.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (app.isGameMenuOpen() || app.isMainMenuOpen())
                return;

            MouseButton btn = event.getButton();
            UserAction action = mouseBindings.get(btn);

            if (action != null) {
                currentActions.remove(action);
            }
        });

        gameScene.setOnMousePressed(mouse::update);
        gameScene.setOnMouseDragged(mouse::update);
        gameScene.setOnMouseReleased(mouse::update);
        gameScene.setOnMouseMoved(mouse::update);
    }

    /**
     * Called by FXGL GameApplication to process all input.
     *
     * @param now
     */
    @Override
    protected void onUpdate(long now) {
        if (processActions) {
            currentActions.forEach(UserAction::onAction);
        }

        Point2D origin = app.getSceneManager().getViewportOrigin();
        mouse.x = mouse.screenX / app.getSceneManager().getSizeRatio() + origin.getX();
        mouse.y = mouse.screenY / app.getSceneManager().getSizeRatio() + origin.getY();
    }

    private boolean processActions = true;

    /**
     * Setting to false will not run any actions bound to key/mouse press.
     * The events will still continue to be registered.
     *
     * @param b
     */
    public void setProcessActions(boolean b) {
        processActions = b;
    }

//    /**
//     * @param key
//     * @return
//     *          true iff key is currently pressed
//     */
//    private boolean isPressed(KeyCode key) {
//        return keys.getOrDefault(key, false);
//    }

    /**
     * Clears all input, that is releases all key presses and mouse clicks
     * for a single frame
     */
    public void clearAllInput() {
        currentActions.clear();
        mouse.leftPressed = false;
        mouse.rightPressed = false;
    }

    public void addAction(UserAction action, MouseButton btn) {
        mouseBindings.put(btn, action);
    }

    public void addAction(UserAction action, KeyCode key) {
        keyBindings.put(key, action);
    }

    /**
     * Returns mouse object that contains constantly updated
     * data about mouse state
     *
     * @return
     */
    public Mouse getMouse() {
        return mouse;
    }

    /**
     * Holds mouse state information
     *
     * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
     */
    public final class Mouse {
        private Mouse() {}

        /**
         * Hold the value of x and y coordinate of the mouse cursor
         * in the current frame (tick) within the game with applied translations
         */
        public double x, y;

        /**
         * Hold the value of x and y coordinate of the mouse cursor
         * in the current frame (tick) within the screen coordinate system
         */
        public double screenX, screenY;

        /**
         * Hold the state of left and right
         * mouse buttons in the current frame (tick)
         */
        public boolean leftPressed, rightPressed;

        /**
         * The last internal event
         */
        private MouseEvent event;

        private void update(MouseEvent event) {
            if (app.isGameMenuOpen() || app.isMainMenuOpen())
                return;

            this.event = event;
            this.screenX = event.getSceneX();
            this.screenY = event.getSceneY();

            Point2D origin = app.getSceneManager().getViewportOrigin();
            this.x = screenX / app.getSceneManager().getSizeRatio() + origin.getX();
            this.y = screenY / app.getSceneManager().getSizeRatio() + origin.getY();

            if (leftPressed) {
                if (event.getButton() == MouseButton.PRIMARY && isReleased(event)) {
                    leftPressed = false;
                }
            }
            else {
                leftPressed = event.getButton() == MouseButton.PRIMARY && isPressed(event);
            }

            if (rightPressed) {
                if (event.getButton() == MouseButton.SECONDARY && isReleased(event)) {
                    rightPressed = false;
                }
            }
            else {
                rightPressed = event.getButton() == MouseButton.SECONDARY && isPressed(event);
            }
        }

        private boolean isPressed(MouseEvent event) {
            return event.getEventType() == MouseEvent.MOUSE_PRESSED
                    || event.getEventType() == MouseEvent.MOUSE_DRAGGED;
        }

        private boolean isReleased(MouseEvent event) {
            return event.getEventType() == MouseEvent.MOUSE_RELEASED
                    || event.getEventType() == MouseEvent.MOUSE_MOVED;
        }

        /**
         * It's unlikely that you'll need this.
         *
         * @return last JavaFX mouse event
         */
        public final MouseEvent getEvent() {
            return event;
        }
    }
}
