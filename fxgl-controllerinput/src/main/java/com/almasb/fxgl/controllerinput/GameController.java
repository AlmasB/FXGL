/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.controllerinput;

import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.logging.Logger;

import java.util.EnumMap;
import java.util.Map;

import static com.almasb.fxgl.input.virtual.VirtualButton.*;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class GameController {

    private static final Logger log = Logger.get(GameController.class);

    private static final Map<VirtualButton, Integer> buttonIDs = new EnumMap<>(VirtualButton.class);

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

        buttonIDs.put(UP, 11);
        buttonIDs.put(DOWN, 12);
        buttonIDs.put(LEFT, 13);
        buttonIDs.put(RIGHT, 14);
    }

    private NativeController controller = new NativeController();

    public boolean isPressed(VirtualButton button) {
        if (!buttonIDs.containsKey(button)) {
            log.warning("No button id mapping for button: " + button);
            return false;
        }

        return NativeController.isButtonPressed(buttonIDs.get(button));
    }

    public void connect() {
        controller.connect();
    }

    public void disconnect() {
        controller.disconnect();
    }
}
