/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app.listener;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface ExitListener {

    /**
     * Called just before FXGL application exits.
     * Do NOT make any asynchronous calls as they may not complete.
     */
    void onExit();
}
