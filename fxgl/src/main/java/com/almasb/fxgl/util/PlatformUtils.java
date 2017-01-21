package com.almasb.fxgl.util;

/**
 * Created by stefanbanu on 21.01.2017.
 */
public class PlatformUtils {

    // Operation System name
    public static String getOsString() {
        return  System.getProperty("os.name");
    }

    // Operation System version
    public static String getJavaOsVersionString() {
        return System.getProperty("os.version");
    }

    // Operation System architecture 32 or 64
    public static String getOsArchString() {
        return System.getProperty("os.version");
    }
}
