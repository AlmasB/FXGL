/*
 * The MIT License (MIT)
 *
 * FXGL - JavaFX Game Library
 *
 * Copyright (c) 2015 AlmasB (almaslvl@gmail.com)
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

package com.almasb.fxgl.donotuse;

/**
 * API INCOMPLETE
 *
 * Represents the entire FXGL infrastructure.
 * Can be used to pass internal properties / data around.
 * Especially useful for passing data between managers.
 * Must not be abused too much and must not be used for other types
 * of communications.
 *
 * The following property keys are currently defined by FXGL:
 * <ul>
 * </ul>
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
public enum FXGLSystem {
    INSTANCE;

    /**
     * Set an int property.
     *
     * @param key property key
     * @param value property value
     */
    public void setInt(String key, int value) {
        setProperty(key, value);
    }

    /**
     * Get value of an int property.
     *
     * @param key property key
     * @return int value
     */
    public int getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    private String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null)
            throw new IllegalArgumentException("Key \"" + key + "\" not found!");
        return value;
    }

    private void setProperty(String key, Object value) {
        System.setProperty("FXGL." + key, String.valueOf(value));
    }
}
