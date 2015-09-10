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

import com.almasb.fxgl.FXGLManager;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Provides access to mouse state and allows binding of actions
 * to key and mouse events
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class InputManager extends FXGLManager {

    //private static final Logger log = FXGLLogger.getLogger("FXGL.InputManager");

    private Scene gameScene;

    /**
     * Holds mouse state information
     */
    private Mouse mouse = new Mouse();

    /**
     * Contains a list of user actions and keys/mouse buttons which trigger those
     * actions.
     */
    private ObservableList<InputBinding> bindings = FXCollections.observableArrayList();

    /**
     *
     * @return unmodifiable view of the input bindings registered
     */
    public ObservableList<InputBinding> getBindings() {
        return FXCollections.unmodifiableObservableList(bindings);
    }

    /**
     * Currently active actions.
     */
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

        gameScene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            handlePressed(new Trigger(event.getCode()));
        });

        gameScene.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            handleReleased(new Trigger(event.getCode()));
        });

        gameScene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            handlePressed(new Trigger(event.getButton()));
        });
        gameScene.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            handleReleased(new Trigger(event.getButton()));
        });

        gameScene.setOnMousePressed(mouse::update);
        gameScene.setOnMouseDragged(mouse::update);
        gameScene.setOnMouseReleased(mouse::update);
        gameScene.setOnMouseMoved(mouse::update);
    }

    private void handlePressed(Trigger trigger) {
        if (isAnyMenuOpen())
            return;

        bindings.stream()
            .filter(binding -> {
                if (trigger.key == null) {
                    return binding.isTriggered(trigger.btn);
                }
                else {
                    return binding.isTriggered(trigger.key);
                }
            })
            .findAny()
            .map(InputBinding::getAction)
            .filter(action -> !currentActions.contains(action))
            .ifPresent(currentActions::add);
    }

    private void handleReleased(Trigger trigger) {
        if (isAnyMenuOpen())
            return;

        bindings.stream()
            .filter(binding -> {
                if (trigger.key == null) {
                    return binding.isTriggered(trigger.btn);
                }
                else {
                    return binding.isTriggered(trigger.key);
                }
            })
            .findAny()
            .map(InputBinding::getAction)
            .ifPresent(currentActions::remove);
    }

    private boolean isAnyMenuOpen() {
        return app.getSceneManager().isGameMenuOpen() || app.getSceneManager().isMainMenuOpen();
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

    /**
     * Clears all input, that is releases all key presses and mouse clicks
     * for a single frame
     */
    public void clearAllInput() {
        currentActions.clear();
        mouse.leftPressed = false;
        mouse.rightPressed = false;
    }

    /**
     * Bind given action to a mouse button.
     *
     * @param action
     * @param btn
     */
    public void addAction(UserAction action, MouseButton btn) {
        bindings.add(new InputBinding(action, btn));
    }

    /**
     * Bind given action to a keyboard key.
     *
     * @param action
     * @param key
     */
    public void addAction(UserAction action, KeyCode key) {
        bindings.add(new InputBinding(action, key));
    }

    /**
     * Find binding for given action.
     *
     * @param action
     * @return
     */
    private Optional<InputBinding> findBindingByAction(UserAction action) {
        return bindings.stream()
                .filter(binding -> binding.getAction().equals(action))
                .findAny();
    }

    private boolean isKeyBound(KeyCode key) {
        return bindings.stream()
                .anyMatch(binding -> binding.isTriggered(key));
    }

    private boolean isButtonBound(MouseButton btn) {
        return bindings.stream()
                .anyMatch(binding -> binding.isTriggered(btn));
    }

    /**
     * Rebinds an action to given key.
     *
     * @param action
     * @param key
     * @return true if rebound, false if action not found or
     *      there is another action bound to key
     */
    public boolean rebind(UserAction action, KeyCode key) {
        Optional<InputBinding> maybeBinding = findBindingByAction(action);
        if (!maybeBinding.isPresent() || isKeyBound(key))
            return false;

        maybeBinding.get().setTrigger(key);
        return true;
    }

    /**
     * Rebinds an action to given mouse button.
     *
     * @param action
     * @param btn
     * @return true if rebound, false if action not found or
     *      there is another action bound to mosue button
     */
    public boolean rebind(UserAction action, MouseButton btn) {
        Optional<InputBinding> maybeBinding = findBindingByAction(action);
        if (!maybeBinding.isPresent() || isButtonBound(btn))
            return false;

        maybeBinding.get().setTrigger(btn);
        return true;
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
            if (isAnyMenuOpen())
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

    /**
     * Convenience wrapper for input types.
     *
     */
    private static class Trigger {
        private KeyCode key;
        private MouseButton btn;

        public Trigger(KeyCode key) {
            this.key = key;
        }

        public Trigger(MouseButton btn) {
            this.btn = btn;
        }
    }
}
