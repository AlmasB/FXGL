/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */
package com.almasb.fxgl.util;

import com.almasb.fxgl.core.logging.Logger;
import com.gluonhq.charm.down.Platform;

import java.util.ResourceBundle;

/**
 * Holds FXGL version info.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Version {

    private Version() {}

    private static final Logger log = Logger.get(Version.class);

    private static final String FXGL_VERSION;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("com.almasb.fxgl.app.system");

        FXGL_VERSION = resources.getString("fxgl.version");
    }

    public static void print() {
        log.info("FXGL-" + getAsString() + " on " + Platform.getCurrent());
        log.info("Source code and latest versions at: https://github.com/AlmasB/FXGL");
        log.info("             Join the FXGL chat at: https://gitter.im/AlmasB/FXGL");
    }

    /**
     * @return compile time version of FXGL
     */
    public static String getAsString() {
        return FXGL_VERSION;
    }
}
