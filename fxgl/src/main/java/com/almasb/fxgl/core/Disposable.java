/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

/**
 * Origin: libGDX.
 */

package com.almasb.fxgl.core;

/**
 * Interface for disposable resources.
 *
 * @author mzechner
 */
public interface Disposable {

    /**
     * Releases all resources of this object.
     */
    void dispose();
}

