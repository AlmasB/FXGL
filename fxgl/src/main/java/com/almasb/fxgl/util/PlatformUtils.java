/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2017 AlmasB (almaslvl@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
