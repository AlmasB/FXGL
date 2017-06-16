/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.util;

/**
 * Provides information about platform specifics.
 *
 * @author stefanbanu
 */
public final class PlatformUtils {

    private PlatformUtils() {}

    private static String OS = null;

    private static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS.toLowerCase();
    }

    public static boolean isWindows() {
        return getOsName().contains("win");
    }

    public static boolean isUnix(){
        return getOsName().contains("nix");
    }

    public static boolean isMac(){
        return getOsName().contains("mac");
    }

    public static boolean isLinux(){
        return getOsName().contains("nux");
    }

    public static boolean isSolaris(){
        return getOsName().contains("sunos");
    }

    /**
     * @return Operation System version
     */
    public static String getOsVersionString() {
        return System.getProperty("os.version");
    }

    /**
     * @return Operation System architecture 32 or 64
     */
    public static String getOsArchString() {
        return System.getProperty("os.version");
    }
}
