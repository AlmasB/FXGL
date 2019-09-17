/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.test;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public final class RunWithFX implements BeforeAllCallback {

    private static boolean jfxStarted = false;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (jfxStarted)
            return;

        try {
            // this immediately throws IllegalStateException if javafx is not initialized
            Platform.runLater(() -> {});
        } catch (IllegalStateException e) {
            jfxStarted = true;
            Platform.startup(() -> {});
        }
    }
}
