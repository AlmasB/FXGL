package com.almasb.fxgl.util;

/**
 * Created by stefanbanu on 21.01.2017.
 */
public class PlatformUtils {

    private static String OS = null;

    public static String getOsName() {
        if(OS == null) { OS = System.getProperty("os.name"); }
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

    // Operation System version
    public static String getOsVersionString() {
        return System.getProperty("os.version");
    }

    // Operation System architecture 32 or 64
    public static String getOsArchString() {
        return System.getProperty("os.version");
    }
}
