/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.controllerinput;

import com.almasb.fxgl.controllerinput.impl.GameControllerImpl;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.logging.Logger;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.core.math.FXGLMath.map;
import static com.almasb.fxgl.input.virtual.VirtualButton.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
public final class GameController {

    private static final Logger log = Logger.get(GameController.class);

    private static final Map<VirtualButton, Integer> buttonIDs = new EnumMap<>(VirtualButton.class);

    private ReadOnlyDoubleWrapper leftTriggerValue = new ReadOnlyDoubleWrapper(0.0);
    private ReadOnlyDoubleWrapper rightTriggerValue = new ReadOnlyDoubleWrapper(0.0);

    private ReadOnlyObjectWrapper<Point2D> leftStickValue = new ReadOnlyObjectWrapper<>(new Point2D(0.0, 0.0));
    private ReadOnlyObjectWrapper<Point2D> rightStickValue = new ReadOnlyObjectWrapper<>(new Point2D(0.0, 0.0));

    private Map<VirtualButton, Boolean> states = new EnumMap<>(VirtualButton.class);

    private List<Input> inputHandlers = new ArrayList<>();

    private final int id;

    static {
        // /**
        // *  The list of buttons available from a controller
        // */
        //typedef enum
        //{
        //    SDL_CONTROLLER_BUTTON_INVALID = -1,
        //    SDL_CONTROLLER_BUTTON_A = 0,
        //    SDL_CONTROLLER_BUTTON_B = 1,
        //    SDL_CONTROLLER_BUTTON_X = 2,
        //    SDL_CONTROLLER_BUTTON_Y = 3,
        //    SDL_CONTROLLER_BUTTON_BACK = 4,
        //    SDL_CONTROLLER_BUTTON_GUIDE = 5,
        //    SDL_CONTROLLER_BUTTON_START = 6,
        //    SDL_CONTROLLER_BUTTON_LEFTSTICK = 7,
        //    SDL_CONTROLLER_BUTTON_RIGHTSTICK = 8,
        //    SDL_CONTROLLER_BUTTON_LEFTSHOULDER = 9,
        //    SDL_CONTROLLER_BUTTON_RIGHTSHOULDER = 10,
        //    SDL_CONTROLLER_BUTTON_DPAD_UP = 11,
        //    SDL_CONTROLLER_BUTTON_DPAD_DOWN = 12,
        //    SDL_CONTROLLER_BUTTON_DPAD_LEFT = 13,
        //    SDL_CONTROLLER_BUTTON_DPAD_RIGHT = 14,
        //    SDL_CONTROLLER_BUTTON_MAX = 15
        //} SDL_GameControllerButton;

        buttonIDs.put(A, 0);
        buttonIDs.put(B, 1);
        buttonIDs.put(X, 2);
        buttonIDs.put(Y, 3);

        buttonIDs.put(LB, 9);
        buttonIDs.put(RB, 10);

        buttonIDs.put(UP, 11);
        buttonIDs.put(DOWN, 12);
        buttonIDs.put(LEFT, 13);
        buttonIDs.put(RIGHT, 14);
    }

    GameController(int id) {
        this.id = id;

        for (VirtualButton button : VirtualButton.values()) {
            states.put(button, false);
        }
    }

    public void addInputHandler(Input input) {
        inputHandlers.add(input);
    }

    public void removeInputHandler(Input input) {
        inputHandlers.remove(input);
    }

    void update() {
        updateButtons();
        updateAxes();
    }

    private void updateButtons() {
        for (VirtualButton button : VirtualButton.values()) {
            if (!buttonIDs.containsKey(button)) {
                log.warning("No button id mapping for button: " + button);
                continue;
            }

            var wasPressed = states.get(button);
            var isPressed = GameControllerImpl.isButtonPressed(id, buttonIDs.get(button));

            if (isPressed && !wasPressed) {
                inputHandlers.forEach(input -> input.pressVirtual(button));
            }

            if (!isPressed && wasPressed) {
                inputHandlers.forEach(input -> input.releaseVirtual(button));
            }

            states.put(button, isPressed);
        }
    }

    private void updateAxes() {
        // typedef enum
        //{
        //    SDL_CONTROLLER_AXIS_INVALID = -1,
        //    SDL_CONTROLLER_AXIS_LEFTX = 0,
        //    SDL_CONTROLLER_AXIS_LEFTY = 1,
        //    SDL_CONTROLLER_AXIS_RIGHTX = 2,
        //    SDL_CONTROLLER_AXIS_RIGHTY = 3,
        //    SDL_CONTROLLER_AXIS_TRIGGERLEFT, = 4
        //    SDL_CONTROLLER_AXIS_TRIGGERRIGHT = 5,
        //    SDL_CONTROLLER_AXIS_MAX = 6
        //} SDL_GameControllerAxis;

        // Range [-32768..32767]
        var leftStickX = GameControllerImpl.getAxis(id, 0);
        var leftStickY = GameControllerImpl.getAxis(id, 1);

        var rightStickX = GameControllerImpl.getAxis(id, 2);
        var rightStickY = GameControllerImpl.getAxis(id, 3);

        leftStickValue.setValue(new Point2D(
                map(leftStickX, -32768.0, 32767.0, -1.0, 1.0),
                map(leftStickY, -32768.0, 32767.0, -1.0, 1.0)
        ));

        rightStickValue.setValue(new Point2D(
                map(rightStickX, -32768.0, 32767.0, -1.0, 1.0),
                map(rightStickY, -32768.0, 32767.0, -1.0, 1.0)
        ));

        // Range: [0..32767]
        var leftTrigger = GameControllerImpl.getAxis(id, 4);
        var rightTrigger = GameControllerImpl.getAxis(id, 5);

        leftTriggerValue.setValue(
                map(leftTrigger, 0.0, 32767.0, 0.0, 1.0)
        );

        rightTriggerValue.setValue(
                map(rightTrigger, 0.0, 32767.0, 0.0, 1.0)
        );
    }

    public boolean isPressed(VirtualButton button) {
        return states.get(button);
    }

    public double getLeftTriggerValue() {
        return leftTriggerValue.get();
    }

    public ReadOnlyDoubleProperty leftTriggerValueProperty() {
        return leftTriggerValue.getReadOnlyProperty();
    }
    public double getRightTriggerValue() {
        return rightTriggerValue.get();
    }

    public ReadOnlyDoubleProperty rightTriggerValueProperty() {
        return rightTriggerValue.getReadOnlyProperty();
    }

    public Point2D getLeftStickValue() {
        return leftStickValue.get();
    }

    public ReadOnlyObjectProperty<Point2D> leftStickValueProperty() {
        return leftStickValue.getReadOnlyProperty();
    }

    public Point2D getRightStickValue() {
        return rightStickValue.get();
    }

    public ReadOnlyObjectProperty<Point2D> rightStickValueProperty() {
        return rightStickValue.getReadOnlyProperty();
    }
}
