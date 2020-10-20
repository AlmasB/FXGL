/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.controllerinput;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
final class NativeController {

    native void connect();

    native void disconnect();

    // TODO: distinguish between controllers (e.g. if more than 1)
    static native boolean isButtonPressed(int buttonId);
}
