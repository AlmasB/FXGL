/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.app;

import com.almasb.fxgl.scene.FXGLScene;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
abstract class AppState extends State {

    private FXGLScene scene;

    AppState(FXGLScene scene) {
        this.scene = scene;
    }

    final FXGLScene getScene() {
        return scene;
    }
}
