/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015-2016 AlmasB (almaslvl@gmail.com)
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

import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Holds version info about various frameworks used in FXGL.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class Version {

    private static final Logger log = FXGLLogger.getLogger("FXGL.Version");

    private static final String FXGL_VERSION;
    private static final String JAVAFX_VERSION;
    private static final String JBOX_VERSION;
    private static final String KOTLIN_VERSION;

    static {
        ResourceBundle resources = ResourceBundle.getBundle("com.almasb.fxgl.util.version");

        FXGL_VERSION = resources.getString("fxgl.version");
        JAVAFX_VERSION = resources.getString("javafx.version");
        JBOX_VERSION = resources.getString("jbox.version");
        KOTLIN_VERSION = resources.getString("kotlin.version");
    }

    public static void print() {
        log.info("FXGL-" + getAsString());
        log.info("JavaFX-" + getJavaFXAsString());
        log.info("JBox2D-" + getJBox2DAsString());
        log.info("Kotlin-" + getKotlinAsString());
        log.info("Source code and latest builds at: https://github.com/AlmasB/FXGL");
    }

    /**
     * @return compile time version of FXGL
     */
    public static String getAsString() {
        return FXGL_VERSION;
    }

    /**
     * @return compile time version of JavaFX
     */
    public static String getJavaFXAsString() {
        return JAVAFX_VERSION;
    }

    /**
     * @return compile time version of JBox2D
     */
    public static String getJBox2DAsString() {
        return JBOX_VERSION;
    }

    /**
     * @return compile time version of Kotlin
     */
    public static String getKotlinAsString() {
        return KOTLIN_VERSION;
    }
}
