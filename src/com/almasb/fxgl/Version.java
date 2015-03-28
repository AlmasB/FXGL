package com.almasb.fxgl;

import java.util.logging.Logger;

public final class Version {

    private static final Logger log = FXGLLogger.getLogger("Version");

    public static int getMajor() {
        return 0;
    }

    public static int getMinor() {
        return 0;
    }

    public static int getPatch() {
        return 4;
    }

    public static void print() {
        log.info("FXGL-" + getMajor() + "." + getMinor() + "." + getPatch());
        log.info("Source code and latest builds at: https://github.com/AlmasB/FXGL");
    }
}
