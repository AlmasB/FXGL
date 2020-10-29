/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.controllerinput.impl;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class GameControllerImpl {

    public static native int getBackendVersion();

    /**
     * Connect to controllers that are currently plugged in.
     *
     * @return number of controllers that we connected to
     */
    public static native int connectControllers();

    /**
     * Updates state from hardware.
     *
     * @param controllerId the controller number
     */
    public static native void updateState(int controllerId);

    /**
     * Call updateState() before querying button presses.
     *
     * @param controllerId the controller number
     * @param buttonId the button number as defined by SDL_GameControllerButton enum
     * @return true if button is pressed, false otherwise
     */
    public static native boolean isButtonPressed(int controllerId, int buttonId);

    /**
     * Call updateState() before querying axis values.
     *
     * @param controllerId the controller number
     * @param axisId the axis number as defined by SDL_GameControllerAxis enum
     * @return a value ranging from -32768 to 32767. Triggers, however, range from 0 to 32767.
     */
    public static native double getAxis(int controllerId, int axisId);

    /**
     * Disconnect all controllers that are currently plugged in.
     */
    public static native void disconnectControllers();
}
