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

package com.almasb.fxgl.app;

/**
 * Represents the entire FXGL infrastructure.
 * Can be used to pass internal properties (key-value pair) around.
 * Can be used for communication between non-related parts.
 * Not to be abused.
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public final class FXGL {

    /**
     * Get value of an int property.
     *
     * @param key property key
     * @return int value
     */
    public static int getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    /**
     * Get value of a double property.
     *
     * @param key property key
     * @return double value
     */
    public static double getDouble(String key) {
        return Double.parseDouble(getProperty(key));
    }

    /**
     * Get value of a boolean property.
     *
     * @param key property key
     * @return boolean value
     */
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    /**
     * @param key property key
     * @return property value
     */
    public static String getProperty(String key) {
        String value = System.getProperty("FXGL." + key);
        if (value == null)
            throw new IllegalArgumentException("Key \"" + key + "\" not found!");
        return value;
    }

    /**
     * Set an int, double, boolean or String property.
     *
     * @param key property key
     * @param value property value
     */
    public static void setProperty(String key, Object value) {
        System.setProperty("FXGL." + key, String.valueOf(value));
    }
}
