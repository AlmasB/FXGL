/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.core;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class RunWithFX implements BeforeAllCallback {

    private static boolean jfxStarted = false;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (jfxStarted)
            return;

        jfxStarted = true;
        Platform.startup(() -> {});
    }
}