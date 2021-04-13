/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core;

/**
 * Allows registering [Updatable] objects whose updates will be driven by this runner.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public interface UpdatableRunner {

    void addListener(Updatable updatable);

    void removeListener(Updatable updatable);
}
